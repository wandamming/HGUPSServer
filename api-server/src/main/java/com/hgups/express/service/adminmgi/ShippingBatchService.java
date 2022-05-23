package com.hgups.express.service.adminmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.PointScanRecord;
import com.hgups.express.domain.ShippingBatch;
import com.hgups.express.domain.User;
import com.hgups.express.domain.param.CreateShippingBatchParam;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.exception.MyException;
import com.hgups.express.mapper.ShippingBatchMapper;
import com.hgups.express.mapper.UserSacksMapper;
import com.hgups.express.service.usermgi.RightsManagementService;
import com.hgups.express.service.usermgi.UserService;
import com.hgups.express.service.waybillmgi.PointScanRecordService;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.util.BatchLabelUtils;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author fanc
 * 2020/7/7 0007-16:51
 */
@Service
@Slf4j
public class ShippingBatchService extends ServiceImpl<ShippingBatchMapper,ShippingBatch> {

    @Resource
    private ShippingBatchMapper shippingBatchMapper;
    @Resource
    private UserSacksMapper userSacksMapper;
    @Resource
    private UserService userService;
    @Resource
    private PointScanRecordService pointScanRecordService;
    @Autowired
    private WayBillService wayBillService;
    @Resource
    private RightsManagementService rightsManagementService;

    //创建航运批次
    public ShippingBatch createShippingBatch(CreateShippingBatchParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        User user = userService.selectById(loginUserId);
        ShippingBatch shippingBatch = DomainCopyUtil.map(param, ShippingBatch.class);
        shippingBatch.setUserId(loginUserId);
        String sacksNumber = userSacksMapper.getSacksNumber(14);
        shippingBatch.setTrackingNumber(sacksNumber);
        shippingBatch.setCreateTime(new Date());
        int processRole = rightsManagementService.isProcessRole(loginUserId);
        if (processRole==1){
            shippingBatch.setIsProcess(1);
        }else {
            shippingBatch.setIsProcess(0);
        }
        String shippingBatchCode = BatchLabelUtils.createShippingBatchPDF(shippingBatch, user.getUsername(), user.getCompany());//生成PDF
        shippingBatch.setCoding(shippingBatchCode);
        Integer insert = shippingBatchMapper.insert(shippingBatch);
        if (insert>0) {
            ShippingBatch shippingBatch1 = shippingBatchMapper.selectById(shippingBatch.getId());
            PointScanRecord pointScanRecord = new PointScanRecord();
            pointScanRecord.setOrderTrackingNumber(shippingBatch1.getTrackingNumber());
            pointScanRecord.setPointType(3);//运单类型
            pointScanRecord.setSysRecord(1);//系统生成状态
            pointScanRecord.setScanUserName(ShiroUtil.getLoginUser().getUsername());
            pointScanRecord.setPointScanName("批次已创建");
            pointScanRecord.setScanTime(shippingBatch1.getCreateTime());
            pointScanRecordService.insert(pointScanRecord);
            return shippingBatch1;
        }
        return null;

    }

    //根据条件获取航运批次
    public List<ShippingBatch> getAllShippingBatch(PageParam param,EntityWrapper wrapper){
        Page<ShippingBatch> page = new Page<>(param.getCurrent(),param.getSize());
        List list = shippingBatchMapper.selectPage(page, wrapper);
        return list;
    }

    public int updateSSF(List<Integer> ids, boolean ssf) {

        for(Integer id : ids) {
            ShippingBatch batch = selectById(id);
            log.info(" updateSSF batch: " + batch);
            if(batch == null) {
                log.warn(" updateSSF batch is null ..");
                continue;
            }

            if(ssf == batch.isHasSSF()) {
                log.warn(" updateSSF SSF state is same...");
                continue;
            }

            //更新批次的SSF状态
            batch.setHasSSF(ssf);
            boolean code = updateById(batch);
            log.info(" updateSSF code: " + code);

            //更新运单SSF并且上传
            wayBillService.updateSSFByShipBatch(batch, ssf);
        }

        return ResponseCode.SUCCESS_CODE;
    }

    public int updateSpEventState(List<Integer> ids, String state) throws MyException {

        for(Integer id : ids) {

            ShippingBatch batch = selectById(id);
            log.info(" updateSpEventState batch: " + batch);
            if (batch == null) {
                log.warn(" updateSpEventState batch is null ..");
                continue;
            }

            batch.setSpEventState(state);
            //更新批次 预上线的状态
            boolean code = updateById(batch);
            log.info(" updateSpEventState code: " + code);

            //更新运单 预上线的状态
            wayBillService.uploadShipPartnerStateByShipBatch(batch, state);
        }

        return ResponseCode.SUCCESS_CODE;
    }


}
