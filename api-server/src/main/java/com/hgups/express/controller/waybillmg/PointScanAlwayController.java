package com.hgups.express.controller.waybillmg;

import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.*;
import com.hgups.express.exception.MyException;
import com.hgups.express.service.waybillmgi.PointScanAlwayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author fanc
 * 2020/11/5-11:31
 */
@RestController
@Api(description = "过点扫描相关API")
@RequestMapping("/pointScanAlway")
public class PointScanAlwayController {

    @Resource
    private PointScanAlwayService pointScanAlwayService;

    @PostMapping("/pointScanWayBillDetails")
    @ApiOperation(value = "APP过点扫描运单详情")
    public Response pointScanWayBillDetails(@RequestBody PointScanOrderDetailsParam param) {
        Response response = new Response();
        if (param.getOrder() == null) {
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("参数错误");
            return response;
        }
        PointScanWaybillDetails pointScanWaybillDetails = pointScanAlwayService.pointScanWayBillDetails(param);
        response.setData(pointScanWaybillDetails);
        return response;
    }

    @PostMapping("/pointScanShippingBatchDetails")
    @ApiOperation(value = "APP过点扫描批次详情")
    public Response pointScanShippingBatchDetails(@RequestBody PointScanOrderDetailsParam param) {
        Response response = new Response();
        if (param.getOrder() == null) {
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("参数错误");
            return response;
        }
        PointScanShippingBatchVo pointScanShippingBatchVo = pointScanAlwayService.pointScanShippingBatchDetails(param);
        response.setData(pointScanShippingBatchVo);
        return response;
    }

    @PostMapping("/pointScanShippingSacksDetails")
    @ApiOperation(value = "APP过点扫描麻袋详情")
    public Response pointScanShippingSacksDetails(@RequestBody PointScanOrderDetailsParam param) {
        Response response = new Response();
        if (param.getOrder() == null) {
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("参数错误");
            return response;
        }
        PointScanShippingSacksDetailsVo pointScanShippingSacksDetailsVo = pointScanAlwayService.pointScanShippingSacksDetails(param);
        response.setData(pointScanShippingSacksDetailsVo);
        return response;
    }


    @PostMapping("/appPointScan")
    @ApiOperation(value = "APP过点扫描")
    public Response appPointScan(@RequestBody PointScanParam param) {
        Response response = new Response();
        String trackingNumber = param.getTrackingNumber();
        if (trackingNumber == null) {
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("参数错误");
            return response;
        }
        try {
            response = pointScanAlwayService.appPointScan(param);
        } catch (MyException e) {
            response.setResponseByErrorMsg(e.getMessage());
        }
        return response;
    }


    @PostMapping("/pointScanInfoList")
    @ApiOperation(value = "运单过点扫描信息列表")
    public Response pointScanInfoList(@RequestBody @Valid PointScanParam param) {
        return pointScanAlwayService.pointScanInfoList(param);
    }

    @PostMapping("/pointScanInfoListV2")
    @ApiOperation(value = "运单过点扫描信息列表")
    public Response pointScanInfoListV2(@RequestBody @Valid PointScanParamV2 param) {
        Response response = new Response();
        try {
            response = pointScanAlwayService.pointScanInfoListV2(param.getId());
        } catch (MyException e) {
            response.setResponseByErrorMsg(e.getMessage());
        }
        return response;
    }

    @PostMapping("/appNotarizePointScan")
    @ApiOperation(value = "APP确认扫描")
    public Response appNotarizePointScan(@RequestBody NotarizePointScanParam param) {
        Response response = new Response();
        Integer scanState = param.getScanState();
        if (scanState == null) {
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("参数异常");
            return response;
        }
        if (param.getScanRoleId() == null) {
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("您当前没有任何点需要扫描");
            return response;
        }
        try {
            Integer integer = pointScanAlwayService.appNotarizePointScan(param);
            if (integer == 1) {
                response.setStatusCode(ResponseCode.SUCCESS_CODE);
                response.setMsg("扫码成功");
            } else if (integer == 2) {
                response.setStatusCode(ResponseCode.FAILED_CODE);
                response.setMsg("扫码失败");
            } else {
                response.setStatusCode(ResponseCode.FAILED_CODE);
                response.setMsg("参数异常");
            }
        } catch (MyException e) {
            response.setResponseByErrorMsg(e.getMessage());
        }

        return response;
    }


}
