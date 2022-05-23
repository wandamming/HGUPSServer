package com.hgups.express.controller.waybillmg;

import com.hgups.express.domain.Response;
import com.hgups.express.domain.Sender;
import com.hgups.express.domain.param.ParamId;
import com.hgups.express.domain.param.SenderParam;
import com.hgups.express.service.waybillmgi.SenderService;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.vo.PageParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/6/4 0004-13:43
 */
@Api(description = "发件人API")
@Slf4j
@RestController
@RequestMapping("/sender")
public class SenderController {

    @Autowired
    private SenderService senderService;

    @ApiOperation(value = "根据ID获取发件人信息")
    @GetMapping("/getSender")
    public Response getSender(@ApiParam(value = "id")@RequestParam("id") Integer id){
        Sender sender = senderService.getSender(id);
        return new Response(sender);
    }

    //全部发件人信息+分页+模糊查询
    @ApiOperation(value = "获取全部发件人信息")
    @PostMapping("/allSender")
    public Response allSender(@ApiParam(value = "分页参数")@RequestBody() PageParameters pageParameters){
        Long id = ShiroUtil.getLoginUserId();
        log.info(" 登录的id: " + id);
        if(null==pageParameters.getSize()&&null==pageParameters.getCurrent()){
            pageParameters.setSize(10);
            pageParameters.setCurrent(1);
        }
        Map<Object,Object> parm = new HashMap<>();
        parm.put("current",(pageParameters.getCurrent()-1)*pageParameters.getSize());
        parm.put("size",pageParameters.getSize());
        parm.put("likes", pageParameters.getLikes());
        parm.put("userId",id);

        List<Sender> records = senderService.allSender(parm);

        int total = senderService.getCount();
        Map<Object,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("size",pageParameters.getSize());
        map.put("pages",(total%pageParameters.getSize())==0?total/pageParameters.getSize():total/pageParameters.getSize()+1);
        map.put("current",pageParameters.getCurrent());
        map.put("records",records);
        return new Response(map);
    }

    @ApiOperation(value = "存储发件人信息")
    @PostMapping("/setSender")
    public Response setSender(@ApiParam(value = "发件人参数") @RequestBody SenderParam param){
        Integer senderId = senderService.setSender(param);
        Response response = new Response();

        if(senderId == -1){
            response.setStatusCode(130);
            response.setMsg("发件人地址信息有误，请重新输入");
            return response;
        }
        if(senderId == 0){
            response.setStatusCode(131);
            response.setMsg("保存到地址簿失败！");
            return response;
        }
        response.setStatusCode(200);
        response.setMsg("添加成功");
        return response;
    }

    @ApiOperation(value = "修改发件人信息")
    @PostMapping("/updateSender")
    public Response updateSender(@ApiParam(value = "发件人参数") @RequestBody Sender sender){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        sender.setUserId(loginUserId);
        boolean flag = senderService.updateSender(sender);
        if(flag){
            response.setStatusCode(200);
            response.setMsg("修改成功");
            return response;
        }
        response.setStatusCode(130);
        response.setMsg("发件人地址信息有误,修改失败");
        return response;
    }

    @ApiOperation(value = "删除发件人信息")
    @PostMapping("/deleteSender")
    public Response deleteSender(@ApiParam(value = "id") @RequestBody ParamId param){
        Response response = new Response();
        boolean flag = senderService.deleteSender(param.getId());
        if(flag){
            response.setStatusCode(200);
            response.setMsg("删除成功");
            return response;
        }
        response.setStatusCode(130);
        response.setMsg("删除失败");
        return response;
    }


}
