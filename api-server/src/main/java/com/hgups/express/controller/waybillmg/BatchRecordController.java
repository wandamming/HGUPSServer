package com.hgups.express.controller.waybillmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.domain.BatchRecord;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.IdPageParam;
import com.hgups.express.service.waybillmgi.BatchRecordService;
import com.hgups.express.service.waybillmgi.WayBillService;
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
 * 2020/7/1 0001-18:25
 */

@Api(description = "批量历史")
@Slf4j
@RestController
@RequestMapping("/BatchRecord")
public class BatchRecordController {

    @Resource
    private BatchRecordService batchRecordService;
    @Resource
    private WayBillService wayBillService;

    @ApiOperation(value = "获取批量历史列表")
    @PostMapping("getBatchRecord")
    public Response getBatchRecord(@RequestBody IdPageParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        int id = param.getId();
        Map<Object,Object> map = new HashMap<Object, Object>();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.orderBy("batch_id",false);
        wrapper.eq("user_id",loginUserId);
        if(id>0){
            wrapper.eq("batch_id",id);
            List batchRecordList = batchRecordService.selectList(wrapper);
            map.put("records",batchRecordList);
            response.setData(map);
            return response;
        }
        Page<BatchRecord> page = new Page<>(param.getCurrent(),param.getSize());
        Page<BatchRecord> pageList = batchRecordService.selectPage(page,wrapper);
        List<BatchRecord> batchRecordList = pageList.getRecords();

        EntityWrapper wrapper1 = new EntityWrapper();
        wrapper1.eq("user_id",loginUserId);
        int total = batchRecordService.getCount(wrapper1);//总条数
        map.put("total",total);
        map.put("current",param.getCurrent());
        map.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);//总页数
        map.put("records",batchRecordList);
        response.setData(map);
        return response;
    }


}
