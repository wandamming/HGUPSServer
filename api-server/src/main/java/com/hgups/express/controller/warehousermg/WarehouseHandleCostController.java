package com.hgups.express.controller.warehousermg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.WarehouseHandleCost;
import com.hgups.express.domain.param.IdLongParam;
import com.hgups.express.domain.param.WarehouseHandleCostListParam;
import com.hgups.express.service.warehousemgi.WarehouseHandleCostService;
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
import java.util.Map;

/**
 * @author fanc
 * 2020/12/10-17:30
 */
@Api(description = "海外仓操作费用相关API")
@Slf4j
@RestController
@RequestMapping("warehouseHandleCost")
public class WarehouseHandleCostController {

    @Resource
    private WarehouseHandleCostService warehouseHandleCostService;

    @ApiOperation(value = "海外仓操作费列表")
    @PostMapping("/warehouseHandleCostList")
    public Response warehouseRentCostList(@RequestBody WarehouseHandleCostListParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        Page<WarehouseHandleCost> page = new Page<>(param.getCurrent(),param.getSize());
        EntityWrapper<WarehouseHandleCost> wrapper = new EntityWrapper<>();
        //收费类型(1：一件代发 2：非一件代发)
        wrapper.eq("charge_type",param.getChargeType());
        Page<WarehouseHandleCost> page1 = warehouseHandleCostService.selectPage(page,wrapper);
        int count = warehouseHandleCostService.selectCount(wrapper);
        Map<Object,Object> map = new HashMap<>();
        map.put("current",param.getCurrent());
        map.put("total",count);
        map.put("pages",(count%param.getSize())==0?count/param.getSize():count/param.getSize()+1);//总页数
        map.put("records",page1.getRecords());
        response.setData(map);
        return response;
    }

    @ApiOperation(value = "增加修改海外仓操作费")
    @PostMapping("/addOrUpdateWarehouseHandleCost")
    public Response addOrUpdateWarehouseRentCost(@RequestBody WarehouseHandleCost param) {
        Response response = new Response();
        boolean b = warehouseHandleCostService.insertOrUpdate(param);
        if (b){
            response.setStatusCode(200);
            response.setMsg("成功");
        }else {
            response.setStatusCode(199);
            response.setMsg("失败");
        }
        return response;
    }

    @ApiOperation(value = "删除海外仓操作费")
    @PostMapping("/deleteWarehouseHandleCost")
    public Response deleteWarehouseRentCost(@RequestBody IdLongParam param) {
        Response response = new Response();
        Long id = param.getId();
        if (id==null){
            response.setStatusCode(300);
            response.setMsg("参数错误");
            return response;
        }
        boolean b = warehouseHandleCostService.deleteById(id);
        if (b){
            response.setStatusCode(200);
            response.setMsg("删除成功");
        }else {
            response.setStatusCode(199);
            response.setMsg("删除失败");
        }
        return response;
    }

}
