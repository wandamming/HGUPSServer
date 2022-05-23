package com.hgups.express.controller.adminmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.business.dhl.DHLService;
import com.hgups.express.business.dhl.track.EventResponse;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.ShippingBatch;
import com.hgups.express.domain.WayBill;
import com.hgups.express.domain.param.DHLAdvanceOnlineParam;
import com.hgups.express.domain.param.InsertBatchWayBillError;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.service.adminmgi.ShippingBatchService;
import com.hgups.express.service.waybillmgi.WayBillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/11/12-11:06
 */
@Api(description = "DHL预上线API")
@RestController
@Slf4j
@RequestMapping("/DHLAdvance")
public class DHLAdvanceController {

    @Resource
    private DHLService dHLService;
    @Resource
    private WayBillService wayBillService;
    @Resource
    private ShippingBatchService shippingBatchService;


    @ApiOperation(value = "DHL运单预上线")
    @PostMapping("/DHLAdvanceOnlineWayBill")
    public Response DHLAdvanceOnlineWayBill(@RequestBody DHLAdvanceOnlineParam param) {
        Response response = new Response();
        List<InsertBatchWayBillError> errorList = new ArrayList<>();
        List<String> trackingNumbers = param.getTrackingNumbers();
        String description = param.getDescription();
        String location = param.getLocation();
        String province = param.getProvince();
        boolean flag = true;
        for (String trackingNumber : trackingNumbers) {
            EventResponse eventResponse = dHLService.postEventFull(trackingNumber, description, location, province);
            if (eventResponse==null){
                flag = false;
                InsertBatchWayBillError error = new InsertBatchWayBillError();
                error.setErrorMessage(trackingNumber+"运单预上线失败");
                errorList.add(error);
            }
        }
        if (flag){
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("预上线失败运单");
            response.setData(errorList);
        }else {
            response.setStatusCode(ResponseCode.SUCCESS_CODE);
            response.setMsg("预上线成功");
        }
        return response;
    }


    @ApiOperation(value = "DHL批次预上线")
    @PostMapping("/DHLAdvanceOnline")
    public Response DHLAdvanceOnline(@RequestBody DHLAdvanceOnlineParam param) {
        Response response = new Response();
        List<InsertBatchWayBillError> errorList = new ArrayList<>();
        List<Integer> ids = param.getIds();
        String description = param.getDescription();
        String location = param.getLocation();
        String province = param.getProvince();
        boolean flag = true;
        ShippingBatch shippingBatch = null;
        for (Integer id : ids) {
            shippingBatch = shippingBatchService.selectById(id);
            EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
            wrapper.eq("shipping_batch_id",id);
            List<WayBill> wayBills = wayBillService.selectList(wrapper);
            for (WayBill wayBill : wayBills) {
                EventResponse eventResponse = dHLService.postEventFull(wayBill.getTrackingNumber(), description, location, province);
                if (eventResponse==null){
                    flag = false;
                    InsertBatchWayBillError error = new InsertBatchWayBillError();
                    error.setErrorMessage("批次"+shippingBatch.getTrackingNumber()+":"+wayBill.getTrackingNumber()+"运单预上线失败");
                    errorList.add(error);
                }
            }
        }
        if (flag){
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("预上线失败运单");
            response.setData(errorList);
        }else {
            shippingBatch.setDhlLocation(location);
            shippingBatch.setDhlDesc(description);
            shippingBatch.setDhlProvince(province);
            shippingBatch.setSpEventState("已预上线");
            shippingBatchService.updateById(shippingBatch);
            response.setStatusCode(ResponseCode.SUCCESS_CODE);
            response.setMsg("预上线成功");
        }
        return response;
    }

    @ApiOperation(value = "DHL预上线批次列表")
    @PostMapping("/DHLCloseShippingBatchList")
    public Response DHLCloseShippingBatchList(@RequestBody PageParam param) {

        Response response = new Response();
        EntityWrapper<ShippingBatch> wrapper = new EntityWrapper<>();
        wrapper.orderBy("create_time",false);
        wrapper.eq("channel","dhl");
        wrapper.eq("state",2);//已关闭批次
        Page<ShippingBatch> page = new Page<>(param.getCurrent(),param.getSize());
        Page<ShippingBatch> page1 = shippingBatchService.selectPage(page, wrapper);
        int total = shippingBatchService.selectCount(wrapper);
        Map<Object,Object> map = new HashMap<>();
        map.put("current",param.getCurrent()+1);
        map.put("total",total);
        map.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);//总页数
        map.put("records",page1.getRecords());
        response.setStatusCode(200);
        response.setData(map);
        return response;

    }



}
