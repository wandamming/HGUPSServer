package com.hgups.express.controller.warehousermg;

import com.hgups.express.domain.Response;
import com.hgups.express.domain.WarehouseOtherCost;
import com.hgups.express.domain.param.WarehouseHandleCostParam;
import com.hgups.express.domain.param.WarehouseRendCostParam;
import com.hgups.express.service.warehousemgi.WarehouseOtherCostService;
import com.hgups.express.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author fanc
 * 2020/9/19 0019-20:41
 */
@Api(description = "海外仓日租、操作价格相关API")
@Slf4j
@RestController
@RequestMapping("warehouseOtherCost")
public class WarehouseOtherCostController {

    @Resource
    private WarehouseOtherCostService warehouseOtherCostService;

    @ApiOperation(value = "获取海外仓其他费用")
    @PostMapping("/getWarehouseOtherCost")
    public Response getWarehouseOtherCost() {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        WarehouseOtherCost warehouseOtherCost = warehouseOtherCostService.getWarehouseOtherCost();
        response.setData(warehouseOtherCost);
        return response;
    }


    @ApiOperation(value = "修改海外仓操作费用")
    @PostMapping("/addUpdateWarehouseHandleCost")
    public Response addUpdateWarehouseHandleCost(@RequestBody WarehouseHandleCostParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        WarehouseOtherCost warehouseOtherCost = warehouseOtherCostService.getWarehouseOtherCost();
        warehouseOtherCost.setHandleOrderPrice(param.getHandleOrderPrice());
        boolean b = false;
        if (warehouseOtherCost.getId()==null||warehouseOtherCost.getId()==0){
            b = warehouseOtherCostService.insert(warehouseOtherCost);
        }else {
            b = warehouseOtherCostService.updateById(warehouseOtherCost);
        }
        if (b) {
            response.setStatusCode(200);
            response.setMsg("修改成功");
            return response;
        }
        response.setStatusCode(202);
        response.setMsg("修改失败");
        return response;
    }

    @ApiOperation(value = "修改海外仓日租时间及日租费用")
    @PostMapping("/addUpdateWarehouseRendCost")
    public Response addUpdateWarehouseRendCost(@RequestBody WarehouseRendCostParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        WarehouseOtherCost warehouseOtherCost = warehouseOtherCostService.getWarehouseOtherCost();

        warehouseOtherCost.setFreeDay(param.getFreeDay());
        warehouseOtherCost.setDayPrice(param.getDayPrice());
        boolean b = false;
        if (warehouseOtherCost.getId()==null||warehouseOtherCost.getId()==0){
            b = warehouseOtherCostService.insert(warehouseOtherCost);
        }else {
            b = warehouseOtherCostService.updateById(warehouseOtherCost);
        }
        if (b) {
            response.setStatusCode(200);
            response.setMsg("修改成功");
            return response;
        }
        response.setStatusCode(202);
        response.setMsg("修改失败");
        return response;
    }


}
