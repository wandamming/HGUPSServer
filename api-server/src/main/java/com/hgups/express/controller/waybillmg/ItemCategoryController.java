package com.hgups.express.controller.waybillmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.domain.ItemCategory;
import com.hgups.express.domain.Response;
import com.hgups.express.service.waybillmgi.ItemCategoryService;
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
 * 2020/6/10 0010-14:43
 */
@Api(description = "物品类型API")
@Slf4j
@RestController
@RequestMapping("/itemCategory")
public class ItemCategoryController {

    @Resource
    private ItemCategoryService itemCategoryServiceImp;

    @ApiOperation(value = "添加物品类型API")
    @PostMapping("/setItemCategory")
    public Response setItemCategory(@RequestBody ItemCategory itemCategory){

        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("name",itemCategory.getName());
        List<ItemCategory> ItemCategorys = itemCategoryServiceImp.selectList(wrapper);
        Map result = new HashMap();
        if(ItemCategorys.size()>0){
            result.put("message","包裹类型已存在");
            result.put("flag",false);
            return new Response(result);
        }else {
            boolean flag = itemCategoryServiceImp.setItemCategory(itemCategory);
            result.put("message","");
            result.put("flag",flag);
            return new Response(result);
        }
    }

    @ApiOperation(value = "修改物品类型")
    @PostMapping("/updateItemCategory")
    public Response updateParcelShape(@RequestBody ItemCategory itemCategory){
        ItemCategory itemCategory1 = itemCategoryServiceImp.selectById(itemCategory.getId());
        itemCategory1 = itemCategory;
        boolean flag = itemCategoryServiceImp.updateById(itemCategory1);
        return new Response(flag);
    }


    @ApiOperation(value = "获取物品类型API")
    @PostMapping("/getItemCategory")
    public Response getItemCategory(@ApiParam(value = "分页参数")@RequestBody PageParameters pageParameters){
        List<ItemCategory> items;
        if(null==pageParameters.getSize()&&null==pageParameters.getCurrent()){
            items = itemCategoryServiceImp.selectList(null);
            return new Response(items);
        }else{
            //分页
            EntityWrapper<ItemCategory> wrapper = new EntityWrapper<ItemCategory>();
            wrapper.orderBy("id",false); //倒序
            Page<ItemCategory> page = new Page<>(pageParameters.getCurrent(),pageParameters.getSize());
            Page<ItemCategory> itemPage = itemCategoryServiceImp.selectPage(page,wrapper);
            items = itemPage.getRecords();
            Map<Object,Object> map = new HashMap<>();
            int total = itemCategoryServiceImp.selectCount(null);//总页数
            map.put("total",total);
            map.put("current",pageParameters.getCurrent());
            map.put("pages",(total%pageParameters.getSize())==0?total/pageParameters.getSize():total/pageParameters.getSize()+1);//总条数
            map.put("records",items);
            return new Response(map);
        }
    }

    @ApiOperation(value = "删除物品类型")
    @PostMapping("/deleteItemCategory")
    public Response deleteItemCategory(@RequestBody ItemCategory itemCategory){
        boolean flag  = itemCategoryServiceImp.deleteItemCategory(itemCategory.getId());
        return new Response(flag);
    }


}
