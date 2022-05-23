package com.hgups.express.controller.waybillmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.domain.DhlCost;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.IdLongParam;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.service.warehousemgi.DhlCostService;
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
@Api(description = "DHL-Zone价格相关API")
@Slf4j
@RestController
@RequestMapping("dhlCost")
public class DhlCostController {

    @Resource
    private DhlCostService dhlCostService;


    @ApiOperation(value = "获取DHL价格")
    @PostMapping("/getDhl")
    public Response  getDhlCost(@RequestBody PageParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        EntityWrapper<DhlCost> wrapper = new EntityWrapper<>();
        wrapper.orderBy("weight", true);
        Page<DhlCost> page = new Page<>(param.getCurrent(), param.getSize());
        Page<DhlCost> page1 = dhlCostService.selectPage(page,wrapper);
        List<DhlCost> records = page1.getRecords();

        Map<Object, Object> map = new HashMap<>();
        int total = dhlCostService.selectCount(null);//总条数
        map.put("current", param.getCurrent());
        map.put("total", total);
        map.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总页数
        map.put("records", records);
        response.setData(map);
        return response;
    }

    @ApiOperation(value = "添加修改DHL运单费用")
    @PostMapping("/addUpdateDhlCost")
    public Response addUpdateDhlCost(@RequestBody DhlCost param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();

        DhlCost dhlCost = DomainCopyUtil.map(param, DhlCost.class);
        if (0 == param.getId()) {
            boolean insert = dhlCostService.insert(dhlCost);
            if (insert) {
                response.setStatusCode(200);
                response.setMsg("添加成功");
                return response;
            }
            response.setStatusCode(201);
            response.setMsg("添加失败");
            return response;
        } else {
            boolean b = dhlCostService.updateById(dhlCost);
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

    @ApiOperation(value = "删除DHL运单费用")
    @PostMapping("/deleteDhlCost")
    public Response deleteDhlCost(@RequestBody IdLongParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();

        boolean b = dhlCostService.deleteById(param.getId());

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
