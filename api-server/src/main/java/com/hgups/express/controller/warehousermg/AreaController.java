package com.hgups.express.controller.warehousermg;

import com.hgups.express.domain.Area;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.IdLongParam;
import com.hgups.express.service.warehousemgi.AreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2020/9/25 0025-16:35
 */
@Api(description = "海外仓区域相关API")
@Slf4j
@RestController
@RequestMapping("area")
public class AreaController {


    @Resource
    private AreaService areaService;


    @PostMapping("/addUpdateArea")
    @ApiOperation(value = "添加修改区域")
    public Response addUpdateArea(@RequestBody Area param){
        Response response = new Response();
        Integer integer = areaService.addUpdateFloor(param);
        if (integer==1){
            response.setStatusCode(200);
            response.setMsg("修改成功");
            return response;
        }else if (integer==2){
            response.setStatusCode(200);
            response.setMsg("添加成功");
            return response;
        } else if (integer==-1){
            response.setStatusCode(201);
            response.setMsg("修改失败");
            return response;
        }else {
            response.setStatusCode(202);
            response.setMsg("添加失败");
            return response;
        }
    }

    @PostMapping("/deleteArea")
    @ApiOperation(value = "删除区域")
    public Response deleteArea(@RequestBody IdLongParam param){
        Response response = new Response();
        boolean b = areaService.deleteArea(param.getId());
        if (b){
            response.setStatusCode(200);
            response.setMsg("删除成功");
            return response;
        }else {
            response.setStatusCode(201);
            response.setMsg("删除失败");
            return response;
        }
    }

    @PostMapping("/listArea")
    @ApiOperation(value = "区域列表")
    public Response listArea(@RequestBody IdLongParam param){
        Response response = new Response();
        List<Area> areas = areaService.listArea(param.getId());
        response.setData(areas);
        return response;
    }

}
