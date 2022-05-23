package com.hgups.express.controller.adminmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hgups.express.domain.PortEntry;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.User;
import com.hgups.express.domain.UserRole;
import com.hgups.express.domain.param.SystemUpdateUserParam;
import com.hgups.express.service.usermgi.PortLateService;
import com.hgups.express.service.usermgi.RightsManagementService;
import com.hgups.express.service.usermgi.UserRoleService;
import com.hgups.express.service.usermgi.UserService;
import com.hgups.express.service.waybillmgi.PortEntryService;
import com.hgups.express.util.DomainCopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2020/7/30 0030-11:11
 */
@Api(description = "管理员用户接口API")
@Slf4j
@RestController
@RequestMapping("/systemUser")
@Configuration
public class SystemUserController {

    @Resource
    private UserService userService;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private PortLateService portLateService;

    @Resource
    private PortEntryService portEntryService;

    @ApiOperation("管理员修改用户信息")
    @PostMapping(value = "/updateUser")
    public Response updateUserInfo(@RequestBody SystemUpdateUserParam param) {
        Response response = new Response();
        User user = DomainCopyUtil.map(param, User.class);
        List<User> userInfoRepeatPhone = userService.isUserInfoRepeatPhone(user);
        List<User> userInfoRepeatEmail = userService.isUserInfoRepeatEmail(user);
        if (userInfoRepeatPhone.size()>0){
            response.setStatusCode(201);
            response.setMsg("手机号已存在");
            return response;
        }
        if (userInfoRepeatEmail.size()>0){
            response.setStatusCode(202);
            response.setMsg("邮箱已存在");
            return response;
        }
        boolean b = userService.updateById(user);
        EntityWrapper<UserRole> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id",param.getId());
        userRoleService.delete(wrapper);

        List<Integer> roleIds = param.getRoleIds();
        Long UserId = Long.parseLong(String.valueOf(param.getId()));
        boolean flag = true;
        for (Integer roleId:roleIds){
            UserRole userRole = new UserRole();
            userRole.setUserId(UserId);
            userRole.setRoleId(roleId);
            boolean insert = userRoleService.insert(userRole);
            if (!insert){
                flag = false;
            }
        }
        //判断后台添加的用户是否带 后程用户角色
        EntityWrapper<PortEntry> wrapper1 = new EntityWrapper();
        wrapper1.eq("type", "tail");
        portLateService.adjustLatePorts(true, roleIds.contains(RightsManagementService.PROCESS_ID), portEntryService.selectList(wrapper1), user.getId());

        if (b&&flag){
            response.setStatusCode(200);
            response.setMsg("修改成功");
            return response;
        }
        response.setStatusCode(203);
        response.setMsg("修改失败");
        return response;

    }



}
