package com.hgups.express.controller.warehousermg;

import com.hgups.express.domain.Inventory;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.AuditInventoryParam;
import com.hgups.express.domain.param.InventoryListParam;
import com.hgups.express.domain.param.RefuseInventoryReasonParam;
import com.hgups.express.exception.MyException;
import com.hgups.express.service.warehousemgi.InventoryService;
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
 * 2020/6/14 0014-13:57
 */
@Api(description = "海外仓入库相关API")
@Slf4j
@RestController
@RequestMapping("adminInventory")
public class AdminInventoryController {
    @Resource
    private InventoryService inventoryService;


    @ApiOperation(value = "管理员入库单列表")
    @PostMapping("/adminInventoryList")
    public Response adminInventoryList(@RequestBody InventoryListParam param) {
        Response response = new Response();

        List<Inventory> inventoryList = inventoryService.adminInventoryList(param);
        Map<Object, Object> result = new HashMap<>();
        int total = inventoryService.adminInventoryListCount(param);
        result.put("total", total);
        result.put("size", param.getSize());
        result.put("current", param.getCurrent());
        result.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总条数
        result.put("records", inventoryList);
        response.setStatusCode(200);
        response.setData(result);
        return response;
    }

    //入库审核
    @ApiOperation(value = "入库单审核")
    @PostMapping("/auditInventory")
    public Response auditInventory(@RequestBody AuditInventoryParam param) {
        Response response = new Response();
        try {
            Integer integer = inventoryService.auditInventory(param);
            if (integer == 1) {
                response.setStatusCode(200);
                response.setMsg("已审核");
            } else if (integer == 0) {
                response.setStatusCode(201);
                response.setMsg("审核失败");
            } else if (integer == -1) {
                response.setStatusCode(201);
                response.setMsg("入库单不存在或已被审核");
            } else if (integer == -2) {
                response.setStatusCode(201);
                response.setMsg("该楼号不存在或已被删除,请重新选择");
            } else if (integer == -3) {
                response.setStatusCode(201);
                response.setMsg("该楼层不存在或已被删除,请重新选择");
            } else {
                response.setStatusCode(201);
                response.setMsg("该区域不存在或已被删除,请重新选择");
            }
        } catch (MyException e) {
            response.setResponseByErrorMsg(e.getMessage());
        }

        return response;
    }


    //拒绝入库
    @ApiOperation(value = "拒绝入库")
    @PostMapping("/refuseInventory")
    public Response refuseInventory(@RequestBody RefuseInventoryReasonParam param) {
        Response response = new Response();
        try {
            boolean b = inventoryService.refuseInventory(param);
            if (b) {
                response.setStatusCode(200);
                response.setMsg("已拒绝该产品入库");
            } else {
                response.setStatusCode(201);
                response.setMsg("拒绝失败");
            }
        } catch (MyException e) {
            e.printStackTrace();
        }
        return response;
    }
}