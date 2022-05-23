package com.hgups.express.controller.warehousermg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.WarehouseCost;
import com.hgups.express.domain.param.IdLongParam;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.service.warehousemgi.WarehouseCostService;
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
 * 2020/9/19 0019-20:41
 */
@Api(description = "海外仓Zone价格相关API")
@Slf4j
@RestController
@RequestMapping("warehouseCost")
public class WarehouseCostController {

    @Resource
    private WarehouseCostService warehouseCostService;



    @ApiOperation(value = "获取海外仓运单价格")
    @PostMapping("/getWarehouseCost")
    public Response  getWarehouseCost(@RequestBody PageParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        EntityWrapper<WarehouseCost> wrapper = new EntityWrapper<>();
        wrapper.orderBy("weight", true);
        Page<WarehouseCost> page = new Page<>(param.getCurrent(), param.getSize());
        Page<WarehouseCost> page1 = warehouseCostService.selectPage(page,wrapper);
        List<WarehouseCost> records = page1.getRecords();

        Map<Object, Object> map = new HashMap<>();
        int total = warehouseCostService.selectCount(null);//总条数
        map.put("current", param.getCurrent());
        map.put("total", total);
        map.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总页数
        map.put("records", records);
        response.setData(map);
        return response;
    }

    @ApiOperation(value = "添加修改海外仓运单费用")
    @PostMapping("/addUpdateWarehouseCost")
    public Response addUpdateWarehouseCost(@RequestBody WarehouseCost param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();

        WarehouseCost warehouseCost = DomainCopyUtil.map(param, WarehouseCost.class);
        if (0 == param.getId()) {
            boolean insert = warehouseCostService.insert(warehouseCost);
            if (insert) {
                response.setStatusCode(200);
                response.setMsg("添加成功");
                return response;
            }
            response.setStatusCode(201);
            response.setMsg("添加失败");
            return response;
        } else {
            boolean b = warehouseCostService.updateById(warehouseCost);
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

    @ApiOperation(value = "删除海外仓运单费用")
    @PostMapping("/deleteWarehouseCost")
    public Response deleteWarehouseCost(@RequestBody IdLongParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();

        boolean b = warehouseCostService.deleteById(param.getId());

        if (b) {
            response.setStatusCode(200);
            response.setMsg("删除成功");
            return response;
        }
        response.setStatusCode(203);
        response.setMsg("删除失败");
        return response;
    }

}
