package com.hgups.express.controller.warehousermg;

import com.hgups.express.domain.BuildingNo;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.BuildingNoFloorAreaParam;
import com.hgups.express.domain.param.IdLongParam;
import com.hgups.express.service.warehousemgi.BuildingNoService;
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
@Api(description = "海外仓楼号相关API")
@Slf4j
@RestController
@RequestMapping("buildingNo")
public class BuildingNoController {


    @Resource
    private BuildingNoService buildingNoService;


    @PostMapping("/addUpdateBuildingNo")
    @ApiOperation(value = "添加修改楼号")
    public Response addUpdateBuildingNo(@RequestBody BuildingNo param){
        Response response = new Response();
        Integer integer = buildingNoService.addUpdateBuildingNo(param);
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

    @PostMapping("/deleteBuildingNo")
    @ApiOperation(value = "删除楼号")
    public Response deleteBuildingNo(@RequestBody IdLongParam param){
        Response response = new Response();
        boolean b = buildingNoService.deleteBuildingNo(param.getId());
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

    @PostMapping("/listBuildingNo")
    @ApiOperation(value = "楼号列表")
    public Response listBuildingNo(){
        Response response = new Response();
        List<BuildingNo> buildingNoList = buildingNoService.listBuildingNo();
        response.setData(buildingNoList);
        return response;
    }

    @PostMapping("/allBuildingNoFloorArea")
    @ApiOperation(value = "楼号区域楼层列表")
    public Response allBuildingNoFloorArea(){
        Response response = new Response();
        List<BuildingNoFloorAreaParam> allBuildingNoFloorArea = buildingNoService.getAllBuildingNoFloorArea();
        response.setData(allBuildingNoFloorArea);
        return response;
    }

}
