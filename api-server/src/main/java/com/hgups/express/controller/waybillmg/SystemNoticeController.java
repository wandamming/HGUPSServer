package com.hgups.express.controller.waybillmg;

import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.SynoticeType;
import com.hgups.express.domain.SystemNotice;
import com.hgups.express.service.waybillmgi.SystemNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author fanc
 * 2021/7/19-11:48
 */

@Api(description = "系统通知列表API")
@Slf4j
@RestController
@RequestMapping("/systemNotice")
public class SystemNoticeController {
    @Resource
    private SystemNoticeService systemNoticeService;

    @ApiOperation(value = "获取系统通知消息")
    @PostMapping("/getSystemNotice")
    public Response getSystemNotice(@RequestBody SynoticeType param ){
        Response response = new Response();
        Page<SystemNotice> systemNotices = systemNoticeService.allSystemNotice(param);
        response.setData(systemNotices);
        return response;
    }


    @ApiOperation(value = "删除系统通知消息")
    @PostMapping("/deleteSystemNotice")
    public Response deleteSystemNotice(@ApiParam(value = "id") @RequestParam("id") Integer id){
        Response response = new Response();
        boolean flag = systemNoticeService.deleteSystemNotice(id);
        if(flag){
            response.setStatusCode(ResponseCode.SUCCESS_CODE);
            response.setMsg("删除成功");
            return response;
        }else{
        response.setStatusCode(ResponseCode.FAILED_CODE);
        response.setMsg("删除失败");
        return response;
        }
    }
}
