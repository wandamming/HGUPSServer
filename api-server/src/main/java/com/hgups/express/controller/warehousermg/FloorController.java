package com.hgups.express.controller.warehousermg;

import com.hgups.express.domain.Floor;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.IdLongParam;
import com.hgups.express.service.warehousemgi.FloorService;
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
@Api(description = "海外仓楼层相关API")
@Slf4j
@RestController
@RequestMapping("floor")
public class FloorController {


    @Resource
    private FloorService floorService;


    @PostMapping("/addUpdateFloor")
    @ApiOperation(value = "添加修改楼层")
    public Response addUpdateFloor(@RequestBody Floor param){
        Response response = new Response();
        Integer integer = floorService.addUpdateFloor(param);
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

    @PostMapping("/deleteFloor")
    @ApiOperation(value = "删除楼层")
    public Response deleteFloor(@RequestBody IdLongParam param){
        Response response = new Response();
        boolean b = floorService.deleteFloor(param.getId());
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

    @PostMapping("/listFloor")
    @ApiOperation(value = "楼层列表")
    public Response listFloor(@RequestBody IdLongParam param){
        Response response = new Response();
        List<Floor> floors = floorService.listFloorByNid(param.getId());
        response.setData(floors);
        return response;
    }

}
