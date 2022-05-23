package com.hgups.express.controller.usermg;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.domain.OperateLog;
import com.hgups.express.domain.OperateModule;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.User;
import com.hgups.express.domain.param.OperateLogListParam;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.mapper.OperateLogMapper;
import com.hgups.express.service.usermgi.OperateLogService;
import com.hgups.express.service.usermgi.OperateModuleService;
import com.hgups.express.service.usermgi.UserService;
import com.hgups.express.util.ResultParamUtil;
import com.hgups.express.vo.OperateLogVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(description = "操作日志API")
@Slf4j
@RestController
@RequestMapping("/operation")
public class OperateLogController {

    @Resource
    private OperateLogService operateLogService;

    @Resource
    private OperateModuleService operateModuleService;

    @Resource
    UserService userService;

    @Resource
    OperateLogMapper operateLogMapper;

    @ApiOperation(value = "获取账号列表")
    @PostMapping("/getUserIdList")
    public Response getUserIdList(){
        Response response = new Response();
        List<Map<String, Object>> modules = operateLogMapper.getUserId();
        response.setData(modules);
        return response;
    }

    @ApiOperation(value = "获取模块列表")
    @PostMapping("/getModuleList")
    public Response getModuleList(){
        Response response = new Response();
        EntityWrapper<OperateModule> wrapper = new EntityWrapper<>();
        wrapper.eq("is_show",1);
        List<OperateModule> modules =operateModuleService.selectList(wrapper);
        response.setData(modules);
        return response;
    }


    @ApiOperation(value = "获取操作日志列表API")
    @PostMapping("/getOperateLogList")
    public Response getOperateLogList(@ApiParam(value = "分页参数") @RequestBody OperateLogListParam pageParam) {

        Response response = new Response();
        Page<OperateLogVo> logsUnitPage = operateLogService.listOperateLog(pageParam);
        response.setData(logsUnitPage);
        return response;
    }

}
