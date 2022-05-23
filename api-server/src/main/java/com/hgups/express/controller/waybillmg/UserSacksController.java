package com.hgups.express.controller.waybillmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.UserSacks;
import com.hgups.express.domain.WayBill;
import com.hgups.express.domain.param.*;
import com.hgups.express.service.waybillmgi.UserSacksService;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.ShiroUtil;
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
 * 2020/7/4 0004-16:03
 */
@Api(description = "麻袋API")
@Slf4j
@RestController
@RequestMapping("/userSacks")
public class UserSacksController {

    @Resource
    private UserSacksService userSacksService;

    @Resource
    private WayBillService wayBillService;

    @PostMapping("/setUserSacks")
    @ApiOperation(value = "创建麻袋")
    public Response setUserSacks(@RequestBody CreateUserSacksParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        UserSacks userSacks = userSacksService.setUserSacks(param, loginUserId);
        if(null==userSacks){
            response.setStatusCode(300);
            response.setMsg("麻袋创建失败");
            return response;
        }
        response.setStatusCode(200);
        response.setData(userSacks);
        return response;
    }
    //获取此批次所有麻袋
    @PostMapping("/getBatchUserSacks")
    @ApiOperation(value = "获取此批次全部麻袋")
    public Response getBatchUserSacks(@RequestBody ParamId param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        List<UserSacks> userSacksList = userSacksService.getBatchUserSacks(param);
        List<BatchUserSacksParam> batchUserSacksParamList = DomainCopyUtil.mapList(userSacksList, BatchUserSacksParam.class);
        response.setData(batchUserSacksParamList);
        response.setStatusCode(200);
        return response;
    }


    //删除麻袋
    @PostMapping("/deleteUserSacks")
    @ApiOperation(value = "删除麻袋")
    public Response deleteUserSacks(@RequestBody DeleteSacksParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();

        int sackId = param.getSacksId();
        int batchId = param.getBatchId();
        boolean delete = userSacksService.deleteById(sackId);
        if (!delete){
            response.setMsg("未找到麻袋");
            response.setStatusCode(300);
            return response;
        }
        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id", loginUserId);
        wrapper.eq("user_sacks_id",sackId);
        wrapper.eq("user_batch_id",batchId);
        List<WayBill> wayBillList = wayBillService.selectList(wrapper);
        for(WayBill wayBill:wayBillList){
            wayBill.setUserSacksId(0);
            wayBill.setUserBatchId(0);
            wayBillService.updateWayBill(wayBill);
        }
        response.setStatusCode(200);
        return response;

    }

    //根据id获取麻袋全部运单信息
    @PostMapping("/getSacksWayBill")
    @ApiOperation(value = "获取此麻袋全部运单")
    public Response getSacksWayBill(@RequestBody GetSacksWayBillParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        List<WayBill> sacksWayBill = userSacksService.getSacksWayBill(param);
        UserSacks userSacks = userSacksService.selectById(param.getSacksId());
        if(null==userSacks){
            response.setMsg("未找到该麻袋");
            response.setStatusCode(300);
            return response;
        }
        BatchUserSacksParam userSacksParam  = DomainCopyUtil.map(userSacks, BatchUserSacksParam.class);
        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.eq("user_sacks_id", param.getSacksId());
        int total = wayBillService.selectCount(wrapper);
        Map<Object,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("size",param.getSize());
        map.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);
        map.put("records",sacksWayBill);
        map.put("sacks",userSacksParam);
        response.setData(map);
        response.setStatusCode(200);
        return response;
    }

}
