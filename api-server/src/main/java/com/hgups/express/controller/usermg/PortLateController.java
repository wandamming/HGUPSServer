package com.hgups.express.controller.usermg;

import com.hgups.express.domain.PortLate;
import com.hgups.express.domain.PortLateDhl;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.domain.param.ParamId;
import com.hgups.express.domain.param.PortLateListVo;
import com.hgups.express.service.usermgi.PortLateDhlService;
import com.hgups.express.service.usermgi.PortLateService;
import com.hgups.express.util.DomainCopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/6/24 0024-16:40
 */
@Api(description = "后程用户入境口岸API")
@Slf4j
@RestController
@RequestMapping("/portLate")
public class PortLateController {


    @Resource
    private PortLateService portLateService;
    @Resource
    private PortLateDhlService portLateDhlService;

    @ApiOperation(value = "获取后程用户入境口岸列表")
    @PostMapping("/getPortLateList")
    public Response getPortLateList(@RequestBody PageParam param) {
        Response response = new Response();
        List<PortLate> portLateList = portLateService.getPortLateList(param);
        Integer total = portLateService.getPortLateListCount();
        List<PortLateListVo> portLateListVos = DomainCopyUtil.mapList(portLateList, PortLateListVo.class);
        Map<Object, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("size",param.getSize());
        map.put("current", param.getCurrent());
        map.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总条数
        map.put("records", portLateListVos);
        response.setData(map);
        return response;
    }

    @ApiOperation(value = "获取后程用户DHL入境口岸列表")
    @PostMapping("/getPortLateDhlList")
    public Response getPortLateDhlList(@RequestBody PageParam param) {
        Response response = new Response();
        List<PortLateDhl> portLateDhlList = portLateDhlService.getPortLateDhlList(param);
        Integer total = portLateDhlService.getPortLateDhlListCount();
        List<PortLateListVo> portLateListVos = new ArrayList<>();
        for (PortLateDhl portLateDhl : portLateDhlList) {
            PortLateListVo portLateListVo = new PortLateListVo();
            portLateListVo.setId(portLateDhl.getId());
            portLateListVo.setLateState(portLateDhl.getLateDhlState());
            portLateListVo.setPortLateName(portLateDhl.getPortLateDhlName());
            portLateListVos.add(portLateListVo);
        }
        Map<Object, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("size",param.getSize());
        map.put("current", param.getCurrent());
        map.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总条数
        map.put("records", portLateListVos);
        response.setData(map);
        return response;
    }

    @ApiOperation(value = "修改后程用户入境口岸状态")
    @PostMapping("/updatePortLateState")
    public Response updatePortLateState(@RequestBody ParamId param) {
        Response response = new Response();
        Integer update = portLateService.getPortLateListState(param);
        if (update==1){
            response.setStatusCode(200);
            response.setMsg("修改成功");
            return response;
        }else if (update==2){
            response.setStatusCode(201);
            response.setMsg("修改失败,该入境口岸已被管理员关闭");
            return response;
        }else {
            response.setStatusCode(202);
            response.setMsg("修改异常,请联系管理员");
            return response;
        }
    }

    @ApiOperation(value = "修改后程用户DHL入境口岸状态")
    @PostMapping("/updatePortLateDhlState")
    public Response updatePortLateDhlState(@RequestBody ParamId param) {
        Response response = new Response();
        Integer update = portLateDhlService.getPortLateDhlListState(param);
        if (update==1){
            response.setStatusCode(200);
            response.setMsg("修改成功");
            return response;
        }else if (update==2){
            response.setStatusCode(201);
            response.setMsg("修改失败,该入境口岸已被管理员关闭");
            return response;
        }else {
            response.setStatusCode(202);
            response.setMsg("修改异常,请联系管理员");
            return response;
        }
    }
}
