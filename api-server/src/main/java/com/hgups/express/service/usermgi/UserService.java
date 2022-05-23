package com.hgups.express.service.usermgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.RegisterParam;
import com.hgups.express.domain.param.UpdateUserPasswordParam;
import com.hgups.express.mapper.UserAccountMapper;
import com.hgups.express.mapper.UserMapper;
import com.hgups.express.service.waybillmgi.PortEntryService;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.MD5Utils;
import com.hgups.express.util.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2020/6/11 0011-11:00
 */
@Service
@Slf4j
public class UserService extends ServiceImpl<UserMapper,User>{

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleService roleService;

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private UserService userService;

    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private PortLateService portLateService;

    @Resource
    private PortEntryService portEntryService;

    public void sendSimpleMail(String to,String title,String content){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(title);
        message.setText(content);
        mailSender.send(message);
        log.info("邮件发送成功");
    }

    @Transactional
    public String userRegister(RegisterParam registerParam){
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("username",registerParam.getUsername());
        List<User> auser = userMapper.selectList(wrapper);
        if(auser.size()>0){
            return "160"; //用户名已存在
        }
        User user = DomainCopyUtil.map(registerParam, User.class);
        try {
            //user.setSalt(registerParam.username + registerParam.password);
            //生成盐
            String salt = new SecureRandomNumberGenerator().nextBytes().toString();
            //user.setPassword(MD5Utils.md5(user.getPassword(),user.getSalt(),1024));
            user.setPassword(MD5Utils.md5(user.getPassword(),salt,1024));
            user.setSalt(salt);
            Integer flag = userMapper.insert(user);
            if (flag>0){
                UserAccount userAccount = new UserAccount();
                userAccount.setUserId(user.getId());
                userAccountMapper.insert(userAccount);
                List<Integer> roleIds = registerParam.getRoleIds();
                System.out.println("----角色id-----"+roleIds);
                if (roleIds!=null){

                    //判断后台添加的用户是否带 后程用户角色
                    if(roleIds.contains(RightsManagementService.PROCESS_ID)) {
                        EntityWrapper<PortEntry> wrapper1 = new EntityWrapper();
                        wrapper1.eq("type", "tail");
                        portLateService.adjustLatePorts(false, true, portEntryService.selectList(wrapper1), user.getId());
                    }

                    for (Integer roleId:roleIds){
                        UserRole userRole = new UserRole();
                        Role role = roleService.selectById(roleId);
                        if (null!=role){
                            userRole.setRoleId(roleId);
                            userRole.setUserId(user.getId());
                            userRoleService.insert(userRole);
                        }

                    }
                }
                return "200";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "161"; //注册失败
    }

    public User getByUsername(String username) {
        EntityWrapper<User> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("username", username);
        return selectOne(entityWrapper);
    }

    //查询全部用户信息(无分页)
    public List<User> getAllUser(){
        List<User> users = userMapper.selectList(null);
        return users;
    }

    //修改用户信息时判断电话信息是否重复
    public List<User> isUserInfoRepeatPhone(User user){
        return userMapper.isUserInfoRepeatPhone(user);
    }

    //修改用户信息时判断邮箱信息是否重复
    public List<User> isUserInfoRepeatEmail(User user){
        return userMapper.isUserInfoRepeatEmail(user);
    }

    @Transactional
    public Integer updateUserPwd(UpdateUserPasswordParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        if(null!=param.getNewPwd()&&param.getNewPwd().length()>=6){
            User user = userService.selectById(loginUserId);
            String salt = user.getSalt();
            String password = user.getPassword();
            String oldPwd = MD5Utils.md5(param.getOldPwd(), salt, 1024);
            if (oldPwd.equals(password)){
                //生成新盐
                String newSalt = new SecureRandomNumberGenerator().nextBytes().toString();
                String newPwd = MD5Utils.md5(param.getNewPwd(), newSalt, 1024);
                user.setPassword(newPwd);
                user.setSalt(newSalt);
                userService.updateById(user);
                return 1;
            }
            return 0;
        }
        return -1;


    }
}
