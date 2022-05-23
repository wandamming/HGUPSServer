package com.hgups.express.controller.warehousermg;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.hgups.express.constant.Constant;
import com.hgups.express.domain.Inventory;
import com.hgups.express.domain.ProductInfo;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.*;
import com.hgups.express.exception.MyException;
import com.hgups.express.service.warehousemgi.InventoryService;
import com.hgups.express.service.warehousemgi.ProductInfoService;
import com.hgups.express.service.waybillmgi.PointScanRecordService;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.MyFileUtil;
import com.hgups.express.util.ShiroUtil;
import com.jpay.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author fanc
 * 2020/6/14 0014-13:57
 */
@Api(description = "海外仓入库相关API")
@Slf4j
@RestController
@RequestMapping("inventory")
public class InventoryController {
    @Resource
    private InventoryService inventoryService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Resource
    private ProductInfoService productInfoService;
    @Resource
    private PointScanRecordService pointScanRecordService;


    @ApiOperation(value = "创建入库单")
    @PostMapping("/addInventory")
    public Response addInventory(@RequestBody InventoryParam param) {
        Response response = new Response();
        Inventory inventory = inventoryService.addInventory(param);
        if (inventory != null) {
            try {
                pointScanRecordService.addSysRecord(4, inventory.getWarehouseWaybillNumber(), "waitEnter", null, new Date(), "入库单号" + inventory.getReceiptOrder());
                response.setStatusCode(200);
                response.setMsg("创建成功");
                response.setData(inventory);
            } catch (MyException e) {
                response.setResponseByErrorMsg(e.getMessage());
            }
            return response;
        }
        response.setStatusCode(201);
        response.setMsg("创建失败");
        return response;
    }

    @ApiOperation(value = "删除入库单")
    @PostMapping("/deleteInventory")
    public Response deleteInventory(@RequestBody LongIdParam param) {
        Response response = new Response();
        try {
            boolean b = inventoryService.deleteInventory(param.getIds());
            if (b) {
                response.setMsg("删除成功");
                return response;
            }
        } catch (MyException e) {
            response.setResponseByErrorMsg(e.getMessage());
            return response;
        }
        response.setStatusCode(201);
        response.setMsg("删除失败");
        return response;
    }


    @ApiOperation(value = "入库单列表")
    @PostMapping("/inventoryList")
    public Response inventoryList(@RequestBody InventoryListParam param) {
        Response response = new Response();

        List<Inventory> inventoryList = inventoryService.inventoryList(param);
        Map<Object, Object> result = new HashMap<>();
        int total = inventoryService.inventoryListCount(param);
        result.put("total", total);
        result.put("size", param.getSize());
        result.put("current", param.getCurrent());
        result.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总条数
        result.put("records", inventoryList);
        response.setStatusCode(200);
        response.setData(result);
        return response;
    }

    @ApiOperation(value = "入库单详情")
    @PostMapping("/getInventoryDetails")
    public Response getInventoryDetails(@RequestBody IdLongParam param) {
        Response response = new Response();
        InventoryDetailsVo inventoryDetails = inventoryService.getInventoryDetails(param.getId());
        response.setData(inventoryDetails);
        return response;
    }

    @ApiOperation(value = "导出入库单信息Excel")
    @GetMapping("/exportInventory")
    public ResponseEntity exportInventory(@RequestParam List ids) {
        ShiroUtil.getLoginUserId();
        List<Inventory> inventoryList = inventoryService.selectBatchIds(ids);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//设置日期格式
        List<String> headRow1 = Lists.newArrayList("序号", "入库单号", "入库状态", "SKU数量"
                , "预约入库数量", "已到数量", "未到数量", "合格数量", "不合格数量", "入库重量", "创建时间"
                , "预到仓时间", "到仓时间", "仓库地址", "入库描述");
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.writeHeadRow(headRow1);
        for (int i = 0; i < headRow1.size(); i++) {
            writer.autoSizeColumn(i);
            if (i == 0) {
                writer.setColumnWidth(i, 12);
            } else {
                writer.setColumnWidth(i, 30);
            }
        }
        List i = new ArrayList();
        inventoryList.forEach(x -> {
            i.add("");
            List<Object> dataList = Lists.newArrayList();
            dataList.add(i.size());
            dataList.add(x.getReceiptOrder());
            if (x.getState() == 1) {
                dataList.add("待入库");
            } else if (x.getState() == 2) {
                dataList.add("已入库");
            } else if(x.getState() == 8) {
                dataList.add("已拒绝");
            } else {
                dataList.add("--");
            }
            dataList.add(x.getSkuNumber());
            dataList.add(x.getReceiptNumber());

            if(Constant.INVENTORY_STATE_STORED  == x.getState()) {
                dataList.add(x.getArrive());
                dataList.add(x.getNoArrive());
                dataList.add(x.getQualified());
                dataList.add(x.getNoQualified());
            } else {
                dataList.add("--");
                dataList.add("--");
                dataList.add("--");
                dataList.add("--");
            }
            dataList.add(x.getInventoryWeight());

            Date createTime = x.getCreateTime();//创建时间
            Date expectTime = x.getExpectTime();//预到仓时间
            Date arriveTime = x.getArriveTime();//到仓时间
            if (null != createTime) {
                dataList.add(df.format(createTime));
            } else {
                dataList.add("--");
            }
            if (null != expectTime) {
                dataList.add(df.format(expectTime));
            } else {
                dataList.add("--");
            }
            if (null != arriveTime) {
                dataList.add(df.format(arriveTime));
            } else {
                dataList.add("--");
            }

            if(Constant.INVENTORY_STATE_STORED == x.getState()) {
                dataList.add(x.getInventoryAddress());
            } else {
                dataList.add("--");
            }
            dataList.add(x.getDescribe());

            writer.writeRow(dataList);
        });

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "入库单列表.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @ApiOperation("导出入库单批量模板Excel")
    @GetMapping(value = "/exportBatchInventoryTemplateExcel")
    public ResponseEntity exportBatchInventoryTemplateExcel() {
        List<String> headRow1 = Lists.newArrayList("产品SkuCode", "产品数量");
        ExcelWriter writer = ExcelUtil.getWriter();

        writer.writeHeadRow(headRow1);

        for (int i = 0; i < headRow1.size(); i++) {
            writer.autoSizeColumn(i);
            writer.setColumnWidth(i, 30);
        }
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "入库单批量导入模版.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @ApiOperation(value = "导入入库单Excel")
    @PostMapping("/exportInventoryExcel")
    public Response exportInventoryExcel(MultipartFile excelFile) {
        Response response = new Response();
        if (excelFile == null) {
            response.setStatusCode(600);
            response.setMsg("Excel文件丢失");
            return response;
        }
        StringBuilder errorMsg = new StringBuilder();
        List<ExportProducerVo> exportProducerVos = new ArrayList<>();
        try {
            List<List<Object>> lines = ExcelUtil.getReader(excelFile.getInputStream()).read();
            for (int i = 1; i < lines.size(); i++) {
                List<Object> line = lines.get(i);

                int index = 0;
                try {
                    String skuCode = String.valueOf(line.get(index++));
                    String productNumber = String.valueOf(line.get(index));
                    EntityWrapper<ProductInfo> wrapper = new EntityWrapper<>();
                    wrapper.eq("sku_code", skuCode);
                    ProductInfo productInfo = productInfoService.selectOne(wrapper);
                    if (productInfo != null) {
                        ExportProducerVo producerVo = DomainCopyUtil.map(productInfo, ExportProducerVo.class);
                        producerVo.setProductNumber(Integer.parseInt(productNumber));
                        exportProducerVos.add(producerVo);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    errorMsg.append(String.format("第%s行只有%s列", i + 1, index)).append("\n");
                } catch (Exception e) {
                    errorMsg.append(String.format("第%s行第%s列解析失败", i + 1, index)).append("\n");
                }
            }
        } catch (Exception e) {
            log.error("parse error:", e);
            return new Response(601, "文件解析异常，请检查excel文件格式是否正确!", null);
        }

        if (StringUtils.isNotBlank(errorMsg.toString())) {
            return new Response(602, "文件解析异常，请检查excel文件格式是否正确!具体错误信息:" + errorMsg.toString(), null);
        }
        response.setData(exportProducerVos);
        response.setMsg("入库成功");
        response.setStatusCode(200);
        return response;
    }


    @ApiOperation(value = "已生成入库清单列表")
    @PostMapping("/createInventoryList")
    public Response createInventoryList(@RequestBody createInventoryListParam param) {
        Response response = new Response();
        Map<Object, Object> map = new HashMap<>();
        map.put("current", (param.getCurrent() - 1) * param.getSize());
        map.put("size", param.getSize());
        map.put("likes", param.getLikes());
        List<Inventory> inventoryList = inventoryService.createInventoryList(map);
        Integer count = inventoryService.createInventoryCount(map);
        Map<Object, Object> result = new HashMap<>();
        result.put("total", count);
        result.put("size", param.getSize());
        result.put("current", param.getCurrent());
        result.put("pages", (count % param.getSize()) == 0 ? count / param.getSize() : count / param.getSize() + 1);//总条数
        result.put("records", inventoryList);
        response.setStatusCode(200);
        response.setData(result);
        return response;
    }

}