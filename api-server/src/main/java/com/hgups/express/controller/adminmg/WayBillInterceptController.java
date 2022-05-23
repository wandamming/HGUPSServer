package com.hgups.express.controller.adminmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.WayBill;
import com.hgups.express.domain.param.AddWayBillInterceptParam;
import com.hgups.express.domain.param.InsertBatchWayBillError;
import com.hgups.express.domain.param.WayBillInterceptParam;
import com.hgups.express.service.usermgi.RightsManagementService;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author fanc
 * 2020/7/22 0022-14:17
 */
@Api(description = "封发拦截API")
@RestController
@Slf4j
@RequestMapping("/WayBillIntercept")
public class WayBillInterceptController {

    @Resource
    private WayBillService wayBillService;
    @Resource
    private RightsManagementService rightsManagementService;

    @ApiOperation(value = "航运运单拦截列表")
    @PostMapping("/getWayBillIntercept")
    public Response getWayBillIntercept(@RequestBody WayBillInterceptParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        int processRole = rightsManagementService.isProcessRole(loginUserId);
        //是否是后程用户
        if (processRole==1){
            param.setProcessUserId(loginUserId);
        }
        int superRole = rightsManagementService.isSuperRole(loginUserId);
        //是否是超级用户
        if (superRole==1){
            param.setProcessUserId(null);
        }
        Response response = new Response();
        int userId = param.getUserId();
        String interceptBeginTime = param.getInterceptBeginTime();
        String interceptEndTime = param.getInterceptEndTime();
        List<String> trackingNumbers = param.getTrackingNumbers();
        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.eq("is_intercept",1);
        if (processRole==1 && superRole!=1){
            wrapper.eq("user_id",loginUserId);
        }
        if (userId!=0&&(!"".equals(userId))){ //判断是否需要根据单号查询
            wrapper.eq("user_id",userId);
        }
        if (null!=trackingNumbers&&trackingNumbers.size()>0&&(!"".equals(trackingNumbers.get(0)))){ //判断是否需要根据单号查询
            wrapper.in("tracking_number",trackingNumbers);
        }
        if (null!=interceptBeginTime&&!"".equals(interceptBeginTime)&&null!=interceptEndTime&&!"".equals(interceptEndTime)){
            wrapper.ge("intercept_time",interceptBeginTime);
            wrapper.le("intercept_time",interceptEndTime);
        }

        Page<WayBill> page = new Page<>(param.getCurrent(),param.getSize());
        Page<WayBill> pageList = wayBillService.selectPage(page, wrapper);
        List<WayBill> records = pageList.getRecords();
        Map<Object,Object> result = new HashMap<>();
        int total = wayBillService.selectCount(wrapper);//总条数
        result.put("total",total);
        result.put("current",param.getCurrent());
        result.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);//总页数
        result.put("records",records);
        response.setData(result);
        return response;
    }

    @ApiOperation(value = "添加拦截运单")
    @PostMapping("/addWayBillIntercept")
    public Response addWayBillIntercept(@RequestBody AddWayBillInterceptParam param){
        Response response = new Response();

        List<String> trackingNumbers = param.getTrackingNumbers();
        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();

        List<InsertBatchWayBillError> errorMsgList = new ArrayList<>();
        for (int i = 0;i<trackingNumbers.size();i++){
            InsertBatchWayBillError errorMsg = new InsertBatchWayBillError();
            String trackingNumber = trackingNumbers.get(i);
            wrapper.eq("tracking_number",trackingNumber);
            WayBill wayBill = wayBillService.selectOne(wrapper);
            if (null==wayBill){
                errorMsg.setErrorMessage(trackingNumber+"失败：(未找到运单信息）");
                errorMsgList.add(errorMsg);
                continue;
            }
            if (0!=wayBill.getShippingSacksId()){
                errorMsg.setErrorMessage(trackingNumber+"失败：(当前运单已在麻袋"+wayBill.getShippingSacksId()+"中,不予许再操作）");
                errorMsgList.add(errorMsg);
                continue;
            }
            wayBill.setInterceptTime(new Date());
            errorMsg.setErrorMessage(trackingNumber+"成功：(当前运单已添加拦截）");
            errorMsgList.add(errorMsg);
            wayBill.setIsIntercept(1);
            wayBillService.updateById(wayBill);
        }

        response.setData(errorMsgList);
        return response;
    }





}
