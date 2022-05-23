package com.hgups.express.controller.usermg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.service.usermgi.*;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.MD5Utils;
import com.hgups.express.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fanc
 * 2020/6/10 0010-22:52
 */
@Api(description = "用户接口API")
@Slf4j
@RestController
@RequestMapping("/user")
@Configuration
public class UserController {

    @Value(value = "${token.timeout}")
    private Long tokenTimeout;

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private UserAndAccountService userAndAccountService;

    @Resource
    private RightsManagementService rightsManagementService;

    private Map<String, String> checkCodeMap = new HashMap<>();

    @ApiOperation(value = "连续登录接口API")
    @PostMapping("/continuousLogin")
    public Response continuousLogin(@RequestBody @Valid LoginParam user) throws InterruptedException {
        Response response = new Response();
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.username, user.password);
        subject.login(token);
        subject.getSession().setTimeout(tokenTimeout);
        for (int i = 0; i < 2000; i++) {
            System.out.println(i);
            ShiroUtil.getLoginUserId();
            Thread.sleep(3000);
        }
        System.out.println("==");
        System.out.println("==");
        System.out.println("==");
        return response;
    }

    @ApiOperation(value = "登录接口API")
    @PostMapping("/login")
    public Response login(@RequestBody @Valid LoginParam user) {
        log.info(" login user={}, tokenTimeout: {}", user, tokenTimeout);
        Response response = new Response();
        if (user.getUsername() == null || user.getPassword() == null) {
            response.setStatusCode(198);
            response.setMsg("账号或密码不能为空");
            return response;
        }
        if (user.getUsername().length() < 1 || user.getUsername().length() > 128) {
            response.setStatusCode(196);
            response.setMsg("账号长度不规范");
            return response;
        }
        if (user.getPassword().length() < 6 || user.getPassword().length() > 64) {
            response.setStatusCode(195);
            response.setMsg("密码必须6~64位");
            return response;
        }

        try {
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(user.username, user.password);
            subject.login(token);

            subject.getSession().setTimeout(tokenTimeout);
            Serializable id = subject.getSession().getId();
            List<MenuVo> menuVoList = rightsManagementService.userRoleMenu(ShiroUtil.getLoginUserId());
            Long loginUserId = ShiroUtil.getLoginUserId();
            EntityWrapper<UserRole> wrapper = new EntityWrapper<>();
            wrapper.eq("user_id", loginUserId);
            List<UserRole> userRoles = userRoleService.selectList(wrapper);
            Map<Object, Object> result = new HashMap<>();
            response.setStatusCode(200);
            response.setMsg("登陆成功");
            result.put("token", id);
            result.put("userId", loginUserId);
            result.put("menuVoList", menuVoList);
            result.put("roleIdList", userRoles);
            response.setData(result);
            log.info("登录成功————" + loginUserId);
            return response;
        } catch (UnknownAccountException e) {
            response.setStatusCode(109);
            response.setMsg("账户未注册");
            return response;
        } catch (AuthenticationException e) {
            response.setStatusCode(199);
            response.setMsg("用户名或密码错误");
            return response;
        }
    }

    @ApiOperation("注销（登出）")
    @PostMapping(value = "/out")
    public Response out() {
        Response responseMessage = new Response();
        try {
            Long loginUserId = ShiroUtil.getLoginUserId();
            log.info("userId {} logout", loginUserId);
        } catch (Exception e) {
            //pass
        }
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return responseMessage;
    }

    @ApiOperation(value = "用户注册API")
    @PostMapping("/register")
    public Response userRegister(@RequestBody @Valid RegisterParam registerParam) {

        Response response = new Response();
        if (registerParam.getUsername() == null || registerParam.getPassword() == null) {
            response.setStatusCode(198);
            response.setMsg("账号或密码不能为空");
            return response;
        }
        if (registerParam.getUsername().length() < 1 || registerParam.getUsername().length() > 128) {
            response.setStatusCode(196);
            response.setMsg("账号长度不规范");
            return response;
        }
        if (registerParam.getPassword().length() < 6 || registerParam.getPassword().length() > 64) {
            response.setStatusCode(195);
            response.setMsg("密码必须6~64位");
            return response;
        }
        if (registerParam.getPhone().length() != 11 && registerParam.getPhone().length() != 10) {
            response.setStatusCode(194);
            response.setMsg("手机号格式错误");
            return response;
        }
        List<User> users = userService.selectList(null);
        boolean flag2 = false;
        for (User user : users) {
            if (registerParam.getPhone().equals(user.getPhone())) {
                flag2 = true;
                break;
            }
        }
        if (flag2) {
            response.setStatusCode(195);
            response.setMsg("手机号已存在！");
            return response;
        }

        //正则验证
        String regEx1 = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        Pattern p = Pattern.compile(regEx1);
        Matcher m = p.matcher(registerParam.getEmail());
        boolean flag1 = false;
        for (User user : users) {
            if (registerParam.getEmail().equals(user.getEmail())) {
                flag1 = true;
                break;
            }
        }
        if (flag1) {
            response.setStatusCode(194);
            response.setMsg("邮箱已存在！");
            return response;
        }
        if (!m.matches()) {
            response.setStatusCode(193);
            response.setMsg("邮箱格式错误");
            return response;
        }

        String flag = userService.userRegister(registerParam);
        if ("200".equals(flag)) {
            response.setStatusCode(200);
            response.setMsg("注册成功！");
            return response;
        } else if ("160".equals(flag)) {
            response.setStatusCode(160);
            response.setMsg("用户名已存在！");
            return response;
        } else {
            response.setStatusCode(161);
            response.setMsg("注册失败！");
            return response;
        }
    }

    @ApiOperation(value = "用户审核")
    @PostMapping("/userAudit")
    public Response userAudit(@RequestBody IdParam param) {
        Response response = new Response();
        User user = userService.selectById(param.getId());
        if (null == user) {
            response.setMsg("用户不存在！");
            response.setStatusCode(201);
            return response;
        }
        user.setState(1);
        boolean update = userService.updateById(user);
        if (update) {
            response.setStatusCode(200);
            response.setMsg("审核成功！");
            return response;
        }
        response.setMsg("审核失败！");
        response.setStatusCode(202);
        return response;

    }

    @ApiOperation(value = "用户信息API")
    @PostMapping("/getUserAndAccount")
    public Response getUserAndAccount(@RequestBody UserAndAccountParam param) {
        Response response = new Response();
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("current", (param.getCurrent() - 1) * param.getSize());
        map.put("size", param.getSize());
        map.put("likes", param.getLikes());
        map.put("state", param.getState());
        List<UserAndAccount> records = userAndAccountService.getUserAndAccount(map);
        for (UserAndAccount userAndAccount : records) {
            int userId = userAndAccount.getId();
            System.out.println("用户ID------》》》》" + userId);
            EntityWrapper<UserRole> wrapper = new EntityWrapper<>();
            wrapper.eq("user_id", userId);
            List<UserRole> userRoles = userRoleService.selectList(wrapper);
            List<Integer> roleIds = new ArrayList<>();
            for (UserRole userRole : userRoles) {
                roleIds.add(userRole.getRoleId());
            }
            List<UserRoleParam> userRoleParams = new ArrayList<>();
            if (roleIds.size() > 0) {
                EntityWrapper<Role> wrapper1 = new EntityWrapper<>();
                wrapper1.in("id", roleIds);
                List<Role> roles = roleService.selectList(wrapper1);
                userRoleParams = DomainCopyUtil.mapList(roles, UserRoleParam.class);
            }
            userAndAccount.setRoleParam(userRoleParams);
        }
        int total = userService.selectCount(null);
        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("total", total);
        result.put("size", param.getSize());
        result.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);
        result.put("current", param.getCurrent());
        result.put("records", records);
        response.setData(result);
        return response;

    }

    @ApiOperation(value = "当前用户信息API")
    @PostMapping("/getUserInform")
    public Response getUserInform() {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        User user = userService.selectById(loginUserId);
        UserInfoParam userParam = DomainCopyUtil.map(user, UserInfoParam.class);
        response.setStatusCode(200);
        response.setData(userParam);
        return response;

    }

    @ApiOperation(value = "编辑用户信息")
    @PostMapping("/updateUserInfo")
    public Response updateUserInfo(@RequestBody UpdateUserInfoParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        User user = DomainCopyUtil.map(param, User.class);
        user.setId(loginUserId);
        boolean b = userService.updateById(user);
        if (b) {
            response.setStatusCode(200);
            response.setMsg("修改成功");
            return response;
        }
        response.setStatusCode(300);
        response.setMsg("修改失败");
        return response;

    }

    @ApiOperation(value = "修改密码")
    @PostMapping("/updateUserPassword")
    public Response updateUserPassword(@RequestBody UpdateUserPasswordParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();

        Integer integer = userService.updateUserPwd(param);


        if (null != param.getNewPwd() && param.getNewPwd().length() >= 6) {
            if (integer == 1) {
                response.setStatusCode(200);
                response.setMsg("修改成功");
                return response;
            }
            if (integer == 0) {
                response.setStatusCode(300);
                response.setMsg("密码错误,修改失败");
                return response;
            }

        }
        response.setStatusCode(301);
        response.setMsg("密码长度不得小于6位");
        return response;

    }

    @ApiOperation(value = "发送邮箱")
    @GetMapping("/senderEmail")
    public Response getCheckCode(@RequestParam String email) {
        Response response = new Response();
        String checkCode = String.valueOf(new Random().nextInt(899999) + 100000);
        checkCodeMap.put(email, checkCode);
        String message = "您修改密码的验证码为：" + checkCode;
        try {
            userService.sendSimpleMail(email, "验证码", message);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(201);
            response.setMsg("邮箱发送失败");
            return response;
        }
        return response;
    }

    @ApiOperation(value = "忘记密码")
    @PostMapping("/forgotPassword")
    public Response forgotPassword(@RequestBody ForgotPasswordParam param) {
        Response response = new Response();
        String userCode = param.getCheckCode();

        String checkCode = checkCodeMap.get(param.getEmail());

        if ((!StringUtils.isEmpty(checkCode)) && (!StringUtils.isEmpty(userCode))) {
            if (userCode.equals(checkCode)) {
                if (null != param.getNewPwd() && param.getNewPwd().length() >= 6) {
                    EntityWrapper<User> wrapper = new EntityWrapper<>();
                    wrapper.eq("email", param.getEmail());
                    User user = userService.selectOne(wrapper);
                    if (null == user) {
                        response.setStatusCode(303);
                        response.setMsg("用户不存在");
                        return response;
                    }
                    //生成新盐
                    String salt = new SecureRandomNumberGenerator().nextBytes().toString();
                    String newPwd = MD5Utils.md5(param.getNewPwd(), salt, 1024);
                    user.setPassword(newPwd);
                    user.setSalt(salt);
                    userService.updateById(user);
                    response.setStatusCode(200);
                    response.setMsg("修改成功");
                    return response;
                }
                response.setStatusCode(301);
                response.setMsg("密码长度不得小于6位");
                return response;
            }
        }
        response.setStatusCode(302);
        response.setMsg("验证码错误");
        return response;
    }





    /*public Response getEmailCode(String email){
        Response response = new Response();

        // 匹配邮箱正则表达式
        String regex = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        Pattern pattern = Pattern.compile(regex);

        if (!pattern.matcher(email).matches()){
            response.setStatusCode(303);
            response.setMsg("请输入正确的邮箱");
            return response;
        }

        String code = CodeUtil.generateCode(redisTemplate, email, "emailCode:", 60*10);

        // 发送验证码，需在配置文件设置邮箱参数
        emailService.sendCode(email, code);
        response.setMsg("验证码已发送");

        return response;
    }*/


}
