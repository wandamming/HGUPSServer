package com.hgups.express.controller.adminmg;

import com.hgups.express.domain.PortEntry;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.ServiceType;
import com.hgups.express.domain.User;
import com.hgups.express.domain.param.PortEntryTitleParam;
import com.hgups.express.domain.param.UserNameIdParam;
import com.hgups.express.service.usermgi.UserService;
import com.hgups.express.service.waybillmgi.PortEntryService;
import com.hgups.express.service.waybillmgi.ServiceTypeService;
import com.hgups.express.util.DomainCopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2020/7/7 0007-15:12
 */
@Api(description = "管理员运单列表")
@Slf4j
@RestController
@RequestMapping("/adminWayBillList")
public class AdminWayBillListController {

    @Resource
    private UserService userService;

    @Resource
    private ServiceTypeService serviceTypeService;

    @Resource
    private PortEntryService portEntryService;

    @ApiOperation(value = "获取用户名、ID")
    @PostMapping("/getUserNameId")
    public Response getUserNameId(){
        Response response = new Response();
        List<User> allUser = userService.getAllUser();
        UserNameIdParam users = DomainCopyUtil.map(allUser, UserNameIdParam.class);
        response.setStatusCode(200);
        response.setData(users);
        return response;
    }

    @ApiOperation(value = "获取服务类型列表")
    @PostMapping("/getServiceType")
    public Response getServiceType(){
        Response response = new Response();
        List<ServiceType> allServiceType = serviceTypeService.getAllServiceType();
        response.setStatusCode(200);
        response.setData(allServiceType);
        return response;
    }

    @ApiOperation(value = "获取服务类型列表")
    @PostMapping("/getPortEntry")
    public Response getPortEntry(){
        Response response = new Response();
        List<PortEntry> portEntries = portEntryService.selectList(null);
        List<PortEntryTitleParam> portEntryTitleParams = DomainCopyUtil.mapList(portEntries, PortEntryTitleParam.class);
        response.setData(portEntryTitleParams);
        response.setStatusCode(200);
        return response;
    }


}
