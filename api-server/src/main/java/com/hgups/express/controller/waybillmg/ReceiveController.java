package com.hgups.express.controller.waybillmg;

import com.hgups.express.domain.Receive;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.ParamId;
import com.hgups.express.domain.param.ReceiveParam;
import com.hgups.express.service.waybillmgi.ReceiveService;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.vo.PageParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/6/4 0004-14:32
 */
@Api(description = "收件人API")
@Slf4j
@RestController
@RequestMapping("/receive")
public class ReceiveController {

    @Resource
    private ReceiveService receiveService;

    @ApiOperation(value = "根据ID获取收件人信息")
    @GetMapping("/getReceive")
    public Response getReceive(@ApiParam(value = "id")@RequestParam("id") Integer id){
        Response response = new Response();
        Receive receive = receiveService.getReceive(id);
        response.setStatusCode(200);
        response.setData(receive);
        return response;
    }

    //全部发件人信息+分页+模糊查询
    @ApiOperation(value = "获取全部收件人信息")
    @PostMapping("/allReceive")
    public Response allReceive(@ApiParam(value = "分页参数")@RequestBody() PageParameters pageParameters){
        Long id = ShiroUtil.getLoginUserId();
        if(null==pageParameters.getSize()&&null==pageParameters.getCurrent()){
            pageParameters.setSize(10);
            pageParameters.setCurrent(1);
        }
        Map<Object,Object> parm = new HashMap<>();
        parm.put("current",(pageParameters.getCurrent()-1)*pageParameters.getSize());
        parm.put("size",pageParameters.getSize());
        parm.put("likes",pageParameters.getLikes());
        parm.put("userId",id);

        List<Receive> records = receiveService.allReceive(parm);

        int total = receiveService.getCount();
        Map<Object,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("size",pageParameters.getSize());
        map.put("pages",(total%pageParameters.getSize())==0?total/pageParameters.getSize():total/pageParameters.getSize()+1);
        map.put("current",pageParameters.getCurrent());
        map.put("records",records);
        return new Response(map);
    }

    @ApiOperation(value = "存储收件人信息")
    @PostMapping("/setReceive")
    public Response setReceive(@ApiParam(value = "提交发件人参数") @RequestBody ReceiveParam param){
        Response response = new Response();
        Integer receiveId = receiveService.setReceive(param);
        if(receiveId==-1){
            response.setStatusCode(130);
            response.setMsg("收件人信息有误，请重新输入");
            return  response;
        }
        if(receiveId==0){
            response.setStatusCode(131);
            response.setMsg("保存到地址簿失败");
            return  response;
        }
        response.setStatusCode(200);
        response.setMsg("添加成功");
        response.setData(receiveId);
        return  response;
    }

    @ApiOperation(value = "修改收件人信息")
    @PostMapping("/updateReceive")
    public Response updateReceive(@ApiParam(value = "发件人的参数") @RequestBody Receive receive){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        receive.setUserId(loginUserId);
        boolean flag = receiveService.updateReceive(receive);
        if(flag){
            response.setStatusCode(200);
            response.setMsg("修改成功");
            return response;
        }
        response.setStatusCode(130);
        response.setMsg("收件人地址信息有误,修改失败");
        return response;
    }

    @ApiOperation(value = "删除收件人信息")
    @PostMapping("/deleteReceive")
    public Response deleteReceive(@ApiParam(value = "发件人id") @RequestBody ParamId param){
        Response response = new Response();
        boolean flag = receiveService.deleteReceive(param.getId());
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
