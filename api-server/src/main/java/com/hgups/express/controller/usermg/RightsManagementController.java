package com.hgups.express.controller.usermg;

import com.hgups.express.domain.Response;
import com.hgups.express.domain.Role;
import com.hgups.express.domain.param.*;
import com.hgups.express.service.usermgi.RightsManagementService;
import com.hgups.express.service.usermgi.RoleService;
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
 * 2020/7/25 0025-20:20
 */

@Api(description = "权限管理api")
@Slf4j
@RestController
@RequestMapping("rights")
public class RightsManagementController {

    @Resource
    private RightsManagementService rightsManagementService;
    @Resource
    private RoleService roleService;


    @ApiOperation(value = "获取拥有物流过点扫描权限的角色")
    @PostMapping("/getLogisticsPointScanRoles")
    public Response getLogisticsPointScanRoles(){
        Response response = new Response();
        List<Role> pointScanRoles = roleService.getLogisticsPointScanRoles();
        response.setData(pointScanRoles);
        return response;
    }

    @ApiOperation(value = "获取拥有海外仓过点扫描权限的角色")
    @PostMapping("/getWarehousePointScanRoles")
    public Response getWarehousePointScanRoles(){
        Response response = new Response();
        List<Role> pointScanRoles = roleService.getWarehousePointScanRoles();
        response.setData(pointScanRoles);
        return response;
    }



    @ApiOperation(value = "添加修改角色API")
    @PostMapping("/addRole")
    public Response addRole(@RequestBody AddRoleParam param){
        Response response = new Response();
        Integer integer = rightsManagementService.addRole(param);
        if (1==integer){
            response.setStatusCode(200);
            response.setMsg("添加成功");
            return response;
        }
        if (-1==integer){
            response.setStatusCode(201);
            response.setMsg("添加失败");
            return response;
        }
        if (2==integer){
            response.setStatusCode(200);
            response.setMsg("修改成功");
            return response;
        }
        if (-2==integer){
            response.setStatusCode(203);
            response.setMsg("修改失败");
            return response;
        }
        response.setStatusCode(204);
        response.setMsg("失败");
        return response;
    }

    @ApiOperation(value = "获取菜单")
    @PostMapping("/getMenu")
    public Response getMenu(){
        Response response = new Response();
        List<MenuVo> menus = rightsManagementService.getMenu();
        response.setStatusCode(200);
        response.setData(menus);
        return response;
    }

    @ApiOperation(value = "删除角色")
    @PostMapping("/deleteRole")
    public Response deleteRole(@RequestBody IdParam param){
        Response response = new Response();
        Integer integer = rightsManagementService.deleteRole(param);
        if (integer==1){
            response.setStatusCode(200);
            response.setMsg("删除成功");
            return response;
        }
        if (integer==0){
            response.setStatusCode(202);
            response.setMsg("角色不存在");
            return response;
        }
        response.setStatusCode(201);
        response.setMsg("删除失败");
        return response;
    }

    @ApiOperation(value = "获取全部角色信息及菜单权限")
    @PostMapping("/getAllRoleMenu")
    public Response getAllRoleMenu(@RequestBody PageParam param){
        Response response = new Response();
        List<AllRoleParam> allRoleParamList = rightsManagementService.allRoleAndMenu(param);
        Map<Object, Object> map = new HashMap<>();
        int total = roleService.selectCount(null);//总页数
        map.put("total", total);
        map.put("current", param.getCurrent());
        map.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总条数
        map.put("records", allRoleParamList);
        response.setData(map);
        return response;
    }


}
