package com.hgups.express.controller.usermg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.domain.LateCost;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.IdParam;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.service.usermgi.LateCostService;
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
 * 2020/8/8 0008-14:48
 */
@Api(description = "后程用户运单价格API")
@Slf4j
@RestController
@RequestMapping("/lateCost")
public class LateCostController {

    @Resource
    private LateCostService lateCostService;


    @ApiOperation(value = "获取后程用户运单价格")
    @PostMapping("/getLateCost")
    public Response getLateCost(@RequestBody PageParam param){
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        EntityWrapper<LateCost> wrapper = new EntityWrapper<>();
        wrapper.orderBy("weight",true);
        Page<LateCost> page = new Page<>(param.getCurrent(),param.getSize());
        Page<LateCost> page1 = lateCostService.selectPage(page,wrapper);
        List<LateCost> records = page1.getRecords();

        Map<Object,Object> map = new HashMap<Object, Object>();
        int total = lateCostService.selectCount(null);//总条数
        map.put("current",param.getCurrent());
        map.put("total",total);
        map.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);//总页数
        map.put("records",records);
        response.setData(map);
        return response;
    }

    @ApiOperation(value = "添加修改后程用户操作费用")
    @PostMapping("/addUpdateLateCost")
    public Response addUpdateLateCost(@RequestBody LateCost param){
        ShiroUtil.getLoginUserId();
        Response response = new Response();

        LateCost lateCost = DomainCopyUtil.map(param, LateCost.class);
        if (0==param.getId()){
            boolean insert = lateCostService.insert(lateCost);
            if (insert){
                response.setStatusCode(200);
                response.setMsg("添加成功");
                return response;
            }
            response.setStatusCode(201);
            response.setMsg("添加失败");
            return response;
        }else {
            boolean b = lateCostService.updateById(lateCost);
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

    @ApiOperation(value = "删除后程用户操作费用")
    @PostMapping("/deleteLateCost")
    public Response deleteLateCost(@RequestBody IdParam param){
        ShiroUtil.getLoginUserId();
        Response response = new Response();

        boolean b = lateCostService.deleteById(param.getId());

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
