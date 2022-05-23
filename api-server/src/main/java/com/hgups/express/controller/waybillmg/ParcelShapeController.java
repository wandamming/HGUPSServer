package com.hgups.express.controller.waybillmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.domain.ParcelShape;
import com.hgups.express.domain.Response;
import com.hgups.express.service.waybillmgi.ParcelShapeService;
import com.hgups.express.vo.PageParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
 * 2020/6/10 0010-14:08
 */
@Api(description = "包裹类型API")
@Slf4j
@RestController
@RequestMapping("/parcelShape")
public class ParcelShapeController {

    @Resource
    private ParcelShapeService parcelShapeServiceImp;

    @ApiOperation(value = "添加包裹类型")
    @PostMapping("/setParcelShape")
    public Response setParcelShape(@RequestBody ParcelShape parcelShape){
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("parcel_shape_name",parcelShape.getParcelShapeName());
        List<ParcelShape> parcelShapes = parcelShapeServiceImp.selectList(wrapper);
        Map result = new HashMap();
        if(parcelShapes.size()>0){
            result.put("message","包裹类型已存在");
            result.put("flag",false);
            return new Response(result);
        }else{
            boolean flag = parcelShapeServiceImp.setParcelShape(parcelShape);
            result.put("message","");
            result.put("flag",flag);
            return new Response(result);
        }
    }

    @ApiOperation(value = "修改包裹类型")
    @PostMapping("/updateParcelShape")
    public Response updateParcelShape(@RequestBody ParcelShape parcelShape){
        ParcelShape parcelShape1 = parcelShapeServiceImp.selectById(parcelShape.getId());
        parcelShape1 = parcelShape;
        boolean flag = parcelShapeServiceImp.updateById(parcelShape1);
        return new Response(flag);
    }

    @ApiOperation(value = "获取包裹类型")
    @PostMapping("/getParcelShape")
    public Response getParcelShape(@ApiParam(value = "分页参数")@RequestBody() PageParameters pageParameters){
        List<ParcelShape> ParcelShape;
        if(null==pageParameters.getSize()&&null==pageParameters.getCurrent()){
            ParcelShape = parcelShapeServiceImp.selectList(null);
            return new Response(ParcelShape);
        }else{
            //分页
            EntityWrapper<ParcelShape> wrapper = new EntityWrapper<ParcelShape>();
            wrapper.orderBy("id",false); //倒序
            Page<ParcelShape> page = new Page<>(pageParameters.getCurrent(),pageParameters.getSize());
            Page<ParcelShape> parPage = parcelShapeServiceImp.selectPage(page,wrapper);
            ParcelShape = parPage.getRecords();
            Map map = new HashMap();
            int total = parcelShapeServiceImp.selectCount(null);//总页数
            map.put("total",total);
            map.put("pages",(total%pageParameters.getSize())==0?total/pageParameters.getSize():total/pageParameters.getSize()+1);//总条数
            map.put("records",ParcelShape);
            return new Response(map);
        }
    }

    @ApiOperation(value = "删除包裹类型")
    @PostMapping("/deleteParcelShape")
    public Response deleteParcelShape(@RequestBody ParcelShape parcelShape){
        boolean flag  = parcelShapeServiceImp.deleteParcelShape(parcelShape.getId());
        return new Response(flag);
    }


}
