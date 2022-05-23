package com.hgups.express.controller.warehousermg;

import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.Outbound;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.AdminBatchConfirmOutboundParam;
import com.hgups.express.domain.param.AdminOutboundParam;
import com.hgups.express.domain.param.LongIdParam;
import com.hgups.express.domain.param.OutboundListParam;
import com.hgups.express.exception.MyException;
import com.hgups.express.service.warehousemgi.OutboundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/9/19 0019-20:41
 */
@Api(description = "海外仓出库相关API")
@Slf4j
@RestController
@RequestMapping("outbound")
public class AdminOutboundController {

    @Resource
    private OutboundService outboundService;


    //进行出库
    @ApiOperation(value = "管理员进行出库")
    @PostMapping("/adminMarchOutbound")
    public Response adminMarchOutbound(@RequestBody LongIdParam param) {
        Response response = new Response();
        try {
            boolean b = outboundService.adminMarchOutbound(param.getIds());
            if (b) {
                response.setStatusCode(200);
                response.setMsg("成功,出库处理中");
            } else {
                response.setStatusCode(201);
                response.setMsg("进行出库失败");
            }
        } catch (MyException e) {
            response.setResponseByErrorMsg(e.getMessage());
        }

        return response;
    }


    //批量确定出库（进行扣费）
    @ApiOperation(value = "管理员批量确定出库")
    @PostMapping("/adminBatchConfirmOutbound")
    public Response adminBatchConfirmOutbound(@RequestBody AdminBatchConfirmOutboundParam param){
        Response response = new Response();
        List<OutboundService.BatchOutboundError> batchOutboundErrors = null;
        try {
            batchOutboundErrors = outboundService.adminBatchConfirmOutbound(param.getAdminOutboundParams());
        } catch (Exception e) {
            log.info("确定出库异常--->"+e.toString());
            response.setMsg(e.toString());
            response.setStatusCode(1999);
            return response;
        }
        boolean flag = false;
        for (OutboundService.BatchOutboundError batchOutboundError : batchOutboundErrors) {
            Integer code = batchOutboundError.getCode();
            if (code != 200) {
                flag = true;
            }
            if (flag) {
                response.setStatusCode(ResponseCode.FAILED_CODE);
                response.setMsg("出库失败");
                response.setData(batchOutboundErrors);
            } else {
                response.setStatusCode(ResponseCode.SUCCESS_CODE);
                response.setMsg("出库成功");
            }
        }
        return response;
    }

    //确定出库（进行扣费）
    @ApiOperation(value = "管理员确定出库")
    @PostMapping("/adminConfirmOutbound")
    public Response adminConfirmOutbound(@RequestBody AdminOutboundParam param){
        Response response = new Response();
        Integer integer = null;
        try {
            integer = outboundService.adminConfirmOutbound(param);
        } catch (Exception e) {
            log.info("确定出库异常--->"+e.toString());
            response.setMsg(e.toString());
            response.setStatusCode(1999);
            return response;
        }
        if (integer == 1) {
            response.setStatusCode(200);
            response.setMsg("出库成功");
        } else if (integer == -1) {
            response.setStatusCode(206);
            response.setMsg("出库失败,余额不足");
        } else {
            response.setStatusCode(201);
            response.setMsg("出库失败,扣费失败");
        }
        return response;
    }

    //管理员出库单列表
    @ApiOperation(value = "管理员出库单列表")
    @PostMapping("/adminOutboundList")
    public Response adminOutboundList(@RequestBody OutboundListParam param) {
        Response response = new Response();
        List<Outbound> outbounds = outboundService.adminOutboundList(param);
        Map<Object, Object> result = new HashMap<>();
        int total = outboundService.adminOutboundCount(param);
        result.put("total", total);
        result.put("size", param.getSize());
        result.put("current", param.getCurrent());
        result.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总条数
        result.put("records", outbounds);
        response.setStatusCode(200);
        response.setData(result);
        return response;
    }

}
