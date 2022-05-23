package com.hgups.express.controller.warehousermg;

import com.hgups.express.domain.Response;
import com.hgups.express.domain.WarehouseProject;
import com.hgups.express.domain.WarehouseRentCost;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.domain.param.UpdateWarehouseRentCostParam;
import com.hgups.express.domain.param.WarehouseRentCostListVo;
import com.hgups.express.service.warehousemgi.WarehouseProjectService;
import com.hgups.express.service.warehousemgi.WarehouseRentCostService;
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
 * 2020/12/10-17:28
 */

@Api(description = "海外仓仓租价格相关API")
@Slf4j
@RestController
@RequestMapping("warehouseRentCost")
public class WarehouseRentCostController {

    @Resource
    private WarehouseRentCostService warehouseRentCostService;
    @Resource
    private WarehouseProjectService warehouseProjectService;

    @ApiOperation(value = "海外仓仓库仓租项目列表")
    @PostMapping("/warehouseProjectList")
    public Response warehouseProjectList() {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        List<WarehouseProject> warehouseProjects = warehouseProjectService.selectList(null);
        response.setData(warehouseProjects);
        return response;
    }

    @ApiOperation(value = "海外仓仓库仓租费用列表")
    @PostMapping("/warehouseRentCostList")
    public Response warehouseRentCostList(@RequestBody PageParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        List<WarehouseRentCostListVo> warehouseRentCostListVos = warehouseRentCostService.warehouseRentCostList(param);
        int count = warehouseProjectService.selectCount(null);
        Map<Object,Object> map = new HashMap<>();
        map.put("current",param.getCurrent());
        map.put("total",count);
        map.put("pages",(count%param.getSize())==0?count/param.getSize():count/param.getSize()+1);//总页数
        map.put("records",warehouseRentCostListVos);
        response.setData(map);
        return response;
    }

    @ApiOperation(value = "修改海外仓仓库仓租费用")
    @PostMapping("/updateWarehouseRentCost")
    public Response updateWarehouseRentCost(@RequestBody UpdateWarehouseRentCostParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        //仓租费ID
        Long id = param.getId();
        if (id==null){
            response.setStatusCode(300);
            response.setMsg("参数错误");
            return response;
        }
        WarehouseRentCost warehouseRentCost = warehouseRentCostService.selectById(id);
        warehouseRentCost.setChargePrice(param.getPrice());
        boolean b = warehouseRentCostService.updateById(warehouseRentCost);
        if (b){
            response.setStatusCode(200);
            response.setMsg("修改成功");
        }else {
            response.setStatusCode(199);
            response.setMsg("修改失败");
        }
        return response;
    }

}
