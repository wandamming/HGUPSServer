package com.hgups.express.controller.usermg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hgups.express.domain.DealDetail;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.User;
import com.hgups.express.domain.UserAccount;
import com.hgups.express.domain.param.RechargeParam;
import com.hgups.express.service.usermgi.DealDetailService;
import com.hgups.express.service.usermgi.UserService;
import com.hgups.express.service.waybillmgi.UserAccountService;
import com.hgups.express.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author fanc
 * 2020/6/13 0013-16:18
 */
@Api(description = "用户充值API")
@Service
@RestController
@RequestMapping("user")
public class UserAccountController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;

    @Resource
    private UserAccountService userAccountService;
    @Resource
    private DealDetailService dealDetailService;

    @ApiOperation(value = "用户充值")
    @PostMapping("/recharge")
    public Response userRecharge(@RequestBody RechargeParam param){
        //Long id = ShiroUtil.getLoginUserId();
        Response response = new Response();
        /*if(id!=1){
            response.setStatusCode(110);
            response.setMsg("非管理员不可充值");
            return response;
        }*/
        User user = userService.selectById(param.getUserId());
        if (user == null) {
            response.setStatusCode(189);
            response.setMsg("用户不存在");
            return response;
        }
        EntityWrapper<UserAccount> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id",param.getUserId());
        UserAccount userAccount = userAccountService.selectOne(wrapper);
        if (userAccount == null) {
            response.setStatusCode(188);
            response.setMsg("账户未开通");
            return response;
        }
        if(param.getRechargeAmount()>0){
            userAccount.setBalance(userAccount.getBalance()+param.getRechargeAmount());
            userAccountService.updateById(userAccount);
            logger.info("充值成功-->",param.getRechargeAmount());
            DealDetail dealDetail = new DealDetail();
            dealDetail.setBalance(userAccount.getBalance());
            dealDetail.setDealAmount(param.getRechargeAmount());
            dealDetail.setDealType(2);//1:扣费,2：充值,3：退款
            dealDetail.setState(1);//交易状态
            dealDetail.setUserId(param.getUserId());
            dealDetailService.insert(dealDetail);
            response.setStatusCode(200);
            response.setMsg("充值成功");
            return response;
        }else {
            response.setStatusCode(187);
            response.setMsg("充值失败");
            return response;
        }

    }

    @ApiOperation(value = "账户余额")
    @PostMapping("/getBalance")
    public Response getBalance(){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("user_id",loginUserId);
        UserAccount userAccount = userAccountService.selectOne(wrapper);
        double balance = userAccount.getBalance();
        response.setData(balance);
        return response;
    }

}
