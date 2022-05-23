package com.hgups.express.controller.warehousermg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.WarehouseMaterialCost;
import com.hgups.express.domain.param.IdParam;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.service.warehousemgi.WarehouseMaterialCostService;
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
@Api(description = "海外仓包装价格相关API")
@Slf4j
@RestController
@RequestMapping("warehouseMaterialCost")
public class WarehouseMaterialCostController {

    @Resource
    private WarehouseMaterialCostService warehouseMaterialCostService;

    @ApiOperation(value = "获取海外仓包装费用")
    @PostMapping("/getWarehouseMaterialCost")
    public Response getWarehouseMaterialCost(@RequestBody PageParam param){
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        EntityWrapper<WarehouseMaterialCost> wrapper = new EntityWrapper<>();
        wrapper.orderBy("pack_price",true);
        Page<WarehouseMaterialCost> page = new Page<>(param.getCurrent(),param.getSize());
        Page<WarehouseMaterialCost> page1 = warehouseMaterialCostService.selectPage(page,wrapper);
        List<WarehouseMaterialCost> records = page1.getRecords();

        Map<Object,Object> map = new HashMap<>();
        int total = warehouseMaterialCostService.selectCount(null);//总条数
        map.put("current",param.getCurrent());
        map.put("total",total);
        map.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);//总页数
        map.put("records",records);
        response.setData(map);
        return response;


    }


    @ApiOperation(value = "添加修改海外仓包装费用")
    @PostMapping("/addUpdateWarehouseMaterialCost")
    public Response addUpdateWarehouseMaterialCost(@RequestBody WarehouseMaterialCost param){
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        WarehouseMaterialCost warehouseMaterialCost = DomainCopyUtil.map(param, WarehouseMaterialCost.class);
        if (param.getId()==null||0==param.getId()){
            boolean insert = warehouseMaterialCostService.insert(warehouseMaterialCost);
            if (insert){
                response.setStatusCode(200);
                response.setMsg("添加成功");
                return response;
            }
            response.setStatusCode(201);
            response.setMsg("添加失败");
            return response;
        }else {
            boolean b = warehouseMaterialCostService.updateById(warehouseMaterialCost);
            if (b){
                response.setStatusCode(200);
                response.setMsg("修改成功");
                return response;
            }
            response.setStatusCode(202);
            response.setMsg("修改失败");
            return response;
        }
    }

    @ApiOperation(value = "删除海外仓包装费用")
    @PostMapping("/deleteWarehouseMaterialCost")
    public Response deleteWarehouseMaterialCost(@RequestBody IdParam param){
        ShiroUtil.getLoginUserId();
        Response response = new Response();

        boolean b = warehouseMaterialCostService.deleteById(param.getId());

        if (b){
            response.setStatusCode(200);
            response.setMsg("删除成功");
            return response;
        }
        response.setStatusCode(203);
        response.setMsg("删除失败");
        return response;
    }

}
