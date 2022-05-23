package com.hgups.express.service.adminmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.PointScanRecord;
import com.hgups.express.domain.ShippingSacks;
import com.hgups.express.domain.User;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.domain.param.ShippingSacksParam;
import com.hgups.express.mapper.ShippingSacksMapper;
import com.hgups.express.mapper.UserSacksMapper;
import com.hgups.express.service.usermgi.RightsManagementService;
import com.hgups.express.service.usermgi.UserService;
import com.hgups.express.service.waybillmgi.PointScanRecordService;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.SacksLabelUtils;
import com.hgups.express.util.ShiroUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author fanc
 * 2020/7/8 0008-11:02
 */
@Service
public class ShippingSacksService extends ServiceImpl<ShippingSacksMapper,ShippingSacks> {

    @Resource
    private ShippingSacksMapper shippingSacksMapper;
    @Resource
    private UserSacksMapper userSacksMapper;
    @Resource
    private UserService userService;
    @Resource
    private RightsManagementService rightsManagementService;
    @Resource
    private PointScanRecordService pointScanRecordService;
    //创建航运麻袋
    public ShippingSacks setShippingSacks(ShippingSacksParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        User user = userService.selectById(loginUserId);
        ShippingSacks shippingSacks = DomainCopyUtil.map(param, ShippingSacks.class);
        String sacksNumber = userSacksMapper.getSacksNumber(14);
        shippingSacks.setUserId(loginUserId);
        int processRole = rightsManagementService.isProcessRole(loginUserId);
        if (processRole==1){
            shippingSacks.setIsProcess(1);
        }else {
            shippingSacks.setIsProcess(0);
        }
        shippingSacks.setSacksNumber(sacksNumber);
        shippingSacks.setCreateTime(new Date());
        String shippingSacksCode = SacksLabelUtils.createShippingSacksPDF(shippingSacks, user.getUsername(), user.getCompany());//生成PDF
        //String sacksCode = PDFToBase64Util.PDFToBase64(sacksNumber);
        shippingSacks.setCoding(shippingSacksCode);
        Integer insert = shippingSacksMapper.insert(shippingSacks);
        if(insert>0){
            ShippingSacks shippingSacks1 = shippingSacksMapper.selectById(shippingSacks.getId());
            PointScanRecord pointScanRecord = new PointScanRecord();
            pointScanRecord.setOrderTrackingNumber(shippingSacks1.getSacksNumber());
            pointScanRecord.setPointType(2);//运单类型
            pointScanRecord.setSysRecord(1);//系统生成状态
            pointScanRecord.setScanUserName(ShiroUtil.getLoginUser().getUsername());
            pointScanRecord.setPointScanName("麻袋已创建");
            pointScanRecord.setScanTime(shippingSacks1.getCreateTime());
            pointScanRecordService.insert(pointScanRecord);
            return shippingSacks1;
        }
        return null;
    }

    //根据条件获取航运批次
    public List<ShippingSacks> getAllShippingSacks(PageParam param, EntityWrapper wrapper){
        Page<ShippingSacks> page = new Page<>(param.getCurrent(),param.getSize());
        List list = shippingSacksMapper.selectPage(page, wrapper);
        return list;
    }


}
