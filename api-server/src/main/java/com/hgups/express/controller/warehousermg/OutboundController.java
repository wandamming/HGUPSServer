package com.hgups.express.controller.warehousermg;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.hgups.express.constant.Constant;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.Outbound;
import com.hgups.express.domain.ProductInfo;
import com.hgups.express.domain.Receive;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.*;
import com.hgups.express.exception.MyException;
import com.hgups.express.service.warehousemgi.OutboundService;
import com.hgups.express.service.warehousemgi.ProductInfoService;
import com.hgups.express.service.waybillmgi.PointScanRecordService;
import com.hgups.express.service.waybillmgi.ReceiveService;
import com.hgups.express.util.MyFileUtil;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.util.USPSApi;
import com.sun.org.apache.regexp.internal.RE;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author fanc
 * 2020/9/19 0019-20:41
 */
@Api(description = "海外仓出库相关API")
@Slf4j
@RestController
@RequestMapping("outbound")
public class OutboundController {

    @Resource
    private OutboundService outboundService;
    @Resource
    private ReceiveService receiveService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Resource
    private ProductInfoService productInfoService;
    @Resource
    private PointScanRecordService pointScanRecordService;

    //UI操作方式，多用户单SKU
    @ApiOperation(value = "批量创建出库单")
    @PostMapping("/batchAddOutbound")
    public Response batchAddOutbound(@RequestBody BatchAddOutBoundParam param) {
        Response response = new Response();
        List<OutboundService.OutboundError> outboundErrors = outboundService.batchAddOutbound(param);
        boolean flag = false;
        for (OutboundService.OutboundError outboundError : outboundErrors) {
            Integer code = outboundError.getCode();
            if (code != 200) {
                flag = true;
                break;
            }
        }
        if (flag) {
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("创建失败");
            response.setData(outboundErrors);
        } else {
            response.setStatusCode(ResponseCode.SUCCESS_CODE);
            response.setMsg("创建成功");
        }
        return response;
    }

    //Excel操作方式：第四种 多用户多SKU
    @ApiOperation(value = "批量导入创建多Sku出库单")
    @PostMapping("/batchImportMoreSkuAddOutbound")
    public Response batchImportMoreSkuAddOutbound(@RequestBody BatchImportMoreSkuAddOutboundParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        //批量创建参数
        BatchAddOutBoundParam batchAddOutBoundParam = new BatchAddOutBoundParam();
        batchAddOutBoundParam.setLogisticsMode(1);
        List<OutBoundParam> outBoundParams = new ArrayList<>();
        int index = 0;
        List<USPSApi.Address> srcs = new ArrayList<>();
        List<ImportMoreSkuWarehouseInfoParam> batchImportProductParamList = param.getBatchImportProductParamList();
        for (ImportMoreSkuWarehouseInfoParam batchParam : batchImportProductParamList) {
            index++;
            // 必须检验地址
            //batchParam.setCheckAddress(true);
            if (batchParam.isCheckAddress()) {
                USPSApi.Address address = new USPSApi.Address();
                address.state = batchParam.getProvinceEname();
                address.city = batchParam.getCityEname();
                address.zipCode5 = batchParam.getPostalCode();
                address.zipCode4 = batchParam.getPostalCodet();
                address.address1 = batchParam.getAddressOne();
                address.address2 = batchParam.getAddressTwo();
                address.index = index;
                srcs.add(address);
            }
        }

        List<USPSApi.Address> addresses = USPSApi.batchValidateAddress(srcs);
        for (int j = 0; j < addresses.size(); j++) {
            if (!addresses.get(j).isValid) {
                batchImportProductParamList.set(j, null);
            }
        }

        for (ImportMoreSkuWarehouseInfoParam batchParam : batchImportProductParamList) {
            if (batchParam == null) {
                outBoundParams.add(null);
                continue;
            }
            Receive receive = new Receive();
            receive.setPostalCodet(batchParam.getPostalCodet());
            receive.setPostalCode(batchParam.getPostalCode());
            receive.setAddressOne(batchParam.getAddressOne());
            receive.setAddressTwo(batchParam.getAddressTwo());
            receive.setCityEname(batchParam.getCityEname());
            receive.setCompany(batchParam.getCompany());
            receive.setCountries(batchParam.getCountries());
            receive.setName(batchParam.getName());
            receive.setPhone(batchParam.getPhone());
            receive.setPhonePrefix(batchParam.getPhonePrefix());
            receive.setProvinceEname(batchParam.getProvinceEname());
            receive.setEmail(batchParam.getEmail());
            receive.setUserId(loginUserId);
            receive.setIsSave("0");
            receiveService.insert(receive);
            OutBoundParam outBoundParam = new OutBoundParam();
            outBoundParam.setReceiveId(receive.getId());
            List<InventoryProducerParam> producerList = new ArrayList<>();

            for(ImportMoreSkuWarehouseInfoParam.ProductionVo pVo: batchParam.getProductionVo()) {
                InventoryProducerParam inventoryProducerParam = new InventoryProducerParam();
                inventoryProducerParam.setProducerId(pVo.getProducerId());
                inventoryProducerParam.setProducerNumber(pVo.getProducerNumber());
                producerList.add(inventoryProducerParam);
            }

            outBoundParam.setProducerList(producerList);
            outBoundParams.add(outBoundParam);
        }

        batchAddOutBoundParam.setOutBoundParams(outBoundParams);
        List<OutboundService.OutboundError> outboundErrors = outboundService.batchAddOutbound(batchAddOutBoundParam);
        boolean flag = false;
        for (OutboundService.OutboundError outboundError : outboundErrors) {
            Integer code = outboundError.getCode();
            if (code != 200) {
                flag = true;
                break;
            }
        }
        if (flag) {
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("创建失败");
            response.setData(outboundErrors);
        } else {
            response.setStatusCode(ResponseCode.SUCCESS_CODE);
            response.setMsg("创建成功");
        }
        return response;

    }

    //Excel操作方式：第三种 多用户单SKU
    @ApiOperation(value = "批量导入创建出库单")
    @PostMapping("/batchImportAddOutbound")
    public Response batchImportAddOutbound(@RequestBody BatchImportAddOutboundParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        //批量创建参数
        BatchAddOutBoundParam batchAddOutBoundParam = new BatchAddOutBoundParam();
        batchAddOutBoundParam.setLogisticsMode(1);
        List<OutBoundParam> outBoundParams = new ArrayList<>();
        int index = 0;
        List<USPSApi.Address> srcs = new ArrayList<>();
        List<BatchImportProductParam> batchImportProductParamList = param.getBatchImportProductParamList();
        for (BatchImportProductParam batchParam : batchImportProductParamList) {
            index++;
            // 必须检验地址
            //batchParam.setCheckAddress(true);
            if (batchParam.isCheckAddress()) {
                USPSApi.Address address = new USPSApi.Address();
                address.state = batchParam.getProvinceEname();
                address.city = batchParam.getCityEname();
                address.zipCode5 = batchParam.getPostalCode();
                address.zipCode4 = batchParam.getPostalCodet();
                address.address1 = batchParam.getAddressOne();
                address.address2 = batchParam.getAddressTwo();
                address.index = index;
                srcs.add(address);
            }
        }
        List<USPSApi.Address> addresses = USPSApi.batchValidateAddress(srcs);
        for (int j = 0; j < addresses.size(); j++) {
            if (!addresses.get(j).isValid) {
                batchImportProductParamList.set(j, null);
            }
        }
        for (BatchImportProductParam batchParam : batchImportProductParamList) {
            if (batchParam == null) {
                outBoundParams.add(null);
                continue;
            }
            Receive receive = new Receive();
            receive.setPostalCodet(batchParam.getPostalCodet());
            receive.setPostalCode(batchParam.getPostalCode());
            receive.setAddressOne(batchParam.getAddressOne());
            receive.setAddressTwo(batchParam.getAddressTwo());
            receive.setCityEname(batchParam.getCityEname());
            receive.setCompany(batchParam.getCompany());
            receive.setCountries(batchParam.getCountries());
            receive.setName(batchParam.getName());
            receive.setPhone(batchParam.getPhone());
            receive.setPhonePrefix(batchParam.getPhonePrefix());
            receive.setProvinceEname(batchParam.getProvinceEname());
            receive.setEmail(batchParam.getEmail());
            receive.setUserId(loginUserId);
            receive.setIsSave("0");
            receiveService.insert(receive);
            OutBoundParam outBoundParam = new OutBoundParam();
            outBoundParam.setReceiveId(receive.getId());
            List<InventoryProducerParam> producerList = new ArrayList<>();
            InventoryProducerParam inventoryProducerParam = new InventoryProducerParam();
            inventoryProducerParam.setProducerId(batchParam.getProducerId());
            inventoryProducerParam.setProducerNumber(batchParam.getProducerNumber());
            producerList.add(inventoryProducerParam);
            outBoundParam.setProducerList(producerList);
            outBoundParams.add(outBoundParam);
        }
        batchAddOutBoundParam.setOutBoundParams(outBoundParams);
        List<OutboundService.OutboundError> outboundErrors = outboundService.batchAddOutbound(batchAddOutBoundParam);
        boolean flag = false;
        for (OutboundService.OutboundError outboundError : outboundErrors) {
            Integer code = outboundError.getCode();
            if (code != 200) {
                flag = true;
                break;
            }
        }
        if (flag) {
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("创建失败");
            response.setData(outboundErrors);
        } else {
            response.setStatusCode(ResponseCode.SUCCESS_CODE);
            response.setMsg("创建成功");
        }
        return response;
    }

    //UI操作方式，单用户多商品
    @ApiOperation(value = "创建出库单")
    @PostMapping("/addOutbound")
    public Response addOutbound(@RequestBody AddOutBoundParam param) {
        OutboundService.OutboundResponse outboundResponse = new OutboundService.OutboundResponse();
        Response response = new Response();
        Integer replaceSend = param.getReplaceSend();
        OutboundService.OutboundError outboundError = new OutboundService.OutboundError();
        if (replaceSend == null) {
            response.setStatusCode(300);
            response.setMsg("参数错误");
            return response;
        } else if (replaceSend == 1) {
            //一件代发
            outboundResponse = outboundService.addOutbound(param);
            outboundError = outboundResponse.getOutboundError();
        } else if (replaceSend == 2) {
            //非一件代发
            outboundResponse = outboundService.addNotReplaceSendOutbound(param);
            outboundError = outboundResponse.getOutboundError();
        } else {
            response.setStatusCode(300);
            response.setMsg("参数错误");
            return response;
        }
        if (outboundError.getCode() == 200) {
            response.setStatusCode(200);
            response.setMsg("创建成功");
            response.setData(outboundResponse.getObject());
        } else if (outboundError.getCode() == 301) {
            response.setStatusCode(301);
            response.setMsg(outboundError.getErrorMsg());
        } else if (outboundError.getCode() == 300) {
            response.setStatusCode(300);
            response.setMsg("入境口岸异常");
        } else if (outboundError.getCode() == 201) {
            response.setStatusCode(201);
            response.setMsg("地址信息错误");
        } else if (outboundError.getCode() == 202) {
            response.setStatusCode(202);
            response.setMsg("重量超出DHL打单范围");
        } else {
            response.setStatusCode(199);
            response.setMsg("创建失败");
        }
        return response;
    }

    @ApiOperation(value = "修改出库单")
    @PostMapping("/updateOutbound")
    public Response updateOutbound(@RequestBody AddOutBoundParam param) {
        Response response = new Response();
        Integer replaceSend = param.getReplaceSend();
        OutboundService.OutboundError outboundError = null;
        if (replaceSend == 1) {
            outboundError = outboundService.updateOutbound(param);
        } else if (replaceSend == 2) {
            outboundError = outboundService.updateNotReplaceSendOutbound(param);
        } else {
            response.setStatusCode(300);
            response.setMsg("参数错误");
            return response;
        }
        Integer code = outboundError.getCode();
        if (code != 200) {
            response.setStatusCode(199);
            response.setMsg(outboundError.getErrorMsg());
        } else {
            response.setStatusCode(200);
            response.setMsg("修改成功");

        }
        return response;
    }

    @ApiOperation(value = "用户出库单列表")
    @PostMapping("/outboundList")
    public Response outboundList(@RequestBody OutboundListParam param) {
        Response response = new Response();
        List<Outbound> outbounds = outboundService.outboundList(param);

        Map<Object, Object> result = new HashMap<>();
        int total = outboundService.outboundCount(param);
        result.put("total", total);
        result.put("size", param.getSize());
        result.put("current", param.getCurrent());
        result.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总条数
        result.put("records", outbounds);
        response.setStatusCode(200);
        response.setData(result);
        return response;
    }


    @ApiOperation(value = "取消出库单")
    @PostMapping("/deleteOutbound")
    public Response deleteOutbound(@RequestBody LongIdParam param) {
        Response response = new Response();
        try {
            outboundService.deleteOutbound(param);
            response.setStatusCode(200);
            response.setMsg("取消成功");
        } catch (MyException e) {
            response.setResponseByErrorMsg(e.getMessage());
        }

        return response;
    }

    @ApiOperation(value = "提交出库单")
    @PostMapping("/submitOutbound")
    public Response submitOutbound(@RequestBody IdLongParam param) {
        Response response = new Response();
        try {
            boolean b = outboundService.submitOutbound(param.getId());
            if (b) {
                response.setStatusCode(200);
                response.setMsg("提交成功");
                return response;
            }
            response.setStatusCode(201);
            response.setMsg("提交失败");
        } catch (MyException e) {
            response.setResponseByErrorMsg(e.getMessage());
        }

        return response;
    }

    @ApiOperation(value = "批量提交出库单")
    @PostMapping("/batchSubmitOutbound")
    public Response batchSubmitOutbound(@RequestBody LongIdParam param) {
        Response response = new Response();
        List<Long> ids = param.getIds();
        if (ids == null || ids.size() == 0) {
            response.setStatusCode(300);
            response.setMsg("参数错误");
            return response;
        }
        try {
            boolean b = outboundService.batchSubmitOutbound(ids);
            if (b) {
                response.setStatusCode(200);
                response.setMsg("提交成功");
                return response;
            }
            response.setStatusCode(201);
            response.setMsg("提交失败");
        } catch (MyException e) {
            response.setResponseByErrorMsg(e.getMessage());
        }
        return response;
    }

    @ApiOperation(value = "批量出库单详情")
    @PostMapping("/getBatchOutboundDetails")
    public Response getBatchOutboundDetails(@RequestBody BatchOutboundParam param) {
        Response response = new Response();

        List<OutboundDetailsVo> outboundDetailsVos = new ArrayList<>();
        List<NotReplaceSendOutboundDetailsVo> notReplaceSendOutboundDetailsVos = new ArrayList<>();

        for (long id : param.getIds()) {
            Outbound outbound = outboundService.selectById(id);
            if (outbound.getReplaceSend() == 1) {
                OutboundDetailsVo outboundDetails = outboundService.getOutboundDetails(id);
                outboundDetailsVos.add(outboundDetails);
            } else {
                NotReplaceSendOutboundDetailsVo notReplaceSendOutboundDetails = outboundService.getNotReplaceSendOutboundDetails(id);
                notReplaceSendOutboundDetailsVos.add(notReplaceSendOutboundDetails);
            }
        }

        if (Constant.OUTBOUND_TYPE_WAYBILL == param.getOutboundType()) {
            response.setData(outboundDetailsVos);
        } else {
            response.setData(notReplaceSendOutboundDetailsVos);
        }
        return response;
    }

    @ApiOperation(value = "出库单详情")
    @PostMapping("/getOutboundDetails")
    public Response getOutboundDetails(@RequestBody IdLongParam param) {
        Response response = new Response();
        Long id = param.getId();
        if (id == null) {
            response.setStatusCode(300);
            response.setMsg("参数错误");
            return response;
        }
        Outbound outbound = outboundService.selectById(id);
        if (outbound.getReplaceSend() == 1) {
            OutboundDetailsVo outboundDetails = outboundService.getOutboundDetails(id);
            response.setData(outboundDetails);
        } else {
            NotReplaceSendOutboundDetailsVo notReplaceSendOutboundDetails = outboundService.getNotReplaceSendOutboundDetails(id);
            response.setData(notReplaceSendOutboundDetails);
        }

        return response;
    }

    @ApiOperation(value = "出库单修改详情")
    @PostMapping("/getUpdateOutboundDetails")
    public Response getUpdateOutboundDetails(@RequestBody IdLongParam param) {
        Response response = new Response();


        Long id = param.getId();
        if (id == null) {
            response.setStatusCode(300);
            response.setMsg("参数错误");
            return response;
        }
        Outbound outbound = outboundService.selectById(id);
        if (outbound.getReplaceSend() == 1) {
            UpdateOutboundDetailsVo updateOutboundDetails = outboundService.getUpdateOutboundDetails(param.getId());
            response.setData(updateOutboundDetails);
        } else {
            UpdateReplaceSendOutboundDetailsVo updateReplaceSendOutboundDetailsVo = outboundService.UpdateReplaceSendOutboundDetails(param.getId());
            response.setData(updateReplaceSendOutboundDetailsVo);
        }
        return response;
    }

    @ApiOperation(value = "导出出库单信息Excel")
    @GetMapping("/exportOutbound")
    public ResponseEntity exportOutbound(@RequestParam List ids) {
        ShiroUtil.getLoginUserId();
        List<OutboundDetailsVo> outboundList = outboundService.getOutboundDetailList(ids);
        boolean isReplaceSend = outboundList != null && !outboundList.isEmpty() && 1 == outboundList.get(0).getReplaceSend();
        int productNumberIndex = isReplaceSend ? 8 : 7;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//设置日期格式
        List<String> headRow1;

        if (isReplaceSend) {
            headRow1 = Lists.newArrayList("序号", "出库单号", "运单号", "出库状态", "SKU种类总数量"
                    , "出库总数量", "出库总重量", "SKU编码", "数量", "发件人姓名", "发件人地址", "收件人姓名", "收件人地址", "物流方式"
                    , "创建时间", "处理时间", "出库时间");
        } else {
            headRow1 = Lists.newArrayList("序号", "出库单号", "出库状态", "SKU种类总数量"
                    , "出库总数量", "出库总重量", "SKU编码", "数量", "物流方式"
                    , "创建时间", "处理时间", "出库时间");
        }

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

        int no = 0;
        for (OutboundDetailsVo x : outboundList) {

            if (x == null || x.getProductInfoList() == null || x.getProductInfoList().isEmpty()) {
                //没有对应出库SKU时候
                continue;
            }

            Map<String, List<Object>> dataListMap = new HashMap<>();
            for (OutboundDetailsSubsetVo vo : x.getProductInfoList()) {
                String sku = vo.getSkuCode();
                int number = vo.getProductNumber();

                if(dataListMap.containsKey(sku)) {
                    List<Object> dataList = dataListMap.get(sku);
                    int total = (int)dataList.get(productNumberIndex);
                    dataList.set(productNumberIndex, total + number);
                    continue;
                }

                List<Object> dataList = Lists.newArrayList();
                dataListMap.put(sku, dataList);

                dataList.add(++no);
                dataList.add(x.getOutboundOrder());

                if (isReplaceSend) {
                    dataList.add(x.getWarehouseWaybillNumber());
                }

                if (x.getState() == 3) {
                    dataList.add("已创建");
                } else if (x.getState() == 4) {
                    dataList.add("待出库");
                } else if (x.getState() == 5) {
                    dataList.add("处理中");
                } else if (x.getState() == 6) {
                    dataList.add("已出库");
                } else if (x.getState() == 7) {
                    dataList.add("问题单");
                } else {
                    dataList.add("--");
                }

                dataList.add(x.getSkuOutboundNumber());
                dataList.add(x.getOutboundNumber());
                dataList.add(x.getOutboundWeight());

                dataList.add(vo.getSkuCode());
                dataList.add(vo.getProductNumber());

                if (isReplaceSend) {
                    dataList.add(x.getSendName());
                    dataList.add(x.getSendAddress());
                    dataList.add(x.getReceiveName());
                    dataList.add(x.getReceiveAddressTwo());
                }
                dataList.add(x.getLogisticsMode() == 1 ? "国内" : "国外");

                Date createTime = x.getCreateTime();//创建时间
                Date manageTime = x.getManageTime();//处理时间
                Date outboundTime = x.getOutboundTime();//出库时间

                if (null != createTime) {
                    dataList.add(df.format(createTime));
                } else {
                    dataList.add("--");
                }
                if (null != manageTime) {
                    dataList.add(df.format(manageTime));
                } else {
                    dataList.add("--");
                }
                if (null != outboundTime) {
                    dataList.add(df.format(outboundTime));
                } else {
                    dataList.add("--");
                }

            }

            for(List<Object> dataList: dataListMap.values()) {
                writer.writeRow(dataList);
            }
        }

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "出库单列表.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @ApiOperation(value = "导出出库清单PDF")
    @GetMapping("/exportOutboundPDF")
    public ResponseEntity batchExportWayPDF(@RequestParam List outboundIds) {
        File file = new File(outboundService.batchExportWayPDF(outboundIds));
        ResponseEntity entity = null;
        try {
            Date date = new Date();

            String exportFileName = "导出清单面单" + DateUtil.format(date, "yyyy-MM-dd-HH-mm-ss") + ".pdf";
            entity = MyFileUtil.downloadFile(file, exportFileName, httpServletRequest);
            boolean code = file.delete();
            log.info(" outboundPDF delete file: " + code);
        } catch (IOException e) {
            log.warn(" outboundPDF error: " + String.valueOf(e));
            e.printStackTrace();
        }
        return entity;
    }

    @ApiOperation(value = "导出出库运单PDF")
    @GetMapping("/batchExportOutboundWayBillPDF")
    public ResponseEntity batchExportOutboundWayBillPDF(@RequestParam List outboundIds) {
        String path = outboundService.batchExportOutboundWayBillPDF(outboundIds);
        File file = new File(path);
        ResponseEntity entity = null;
        try {
            Date date = new Date();

            String exportFileName = "导出运单面单" + DateUtil.format(date, "yyyy-MM-dd-HH-mm-ss") + ".pdf";
            entity = MyFileUtil.downloadFile(file, exportFileName, httpServletRequest);
            boolean code = file.delete();
            log.info(" outboundPDF delete file: " + code);
        } catch (IOException e) {
            log.warn(" outboundPDF error: " + String.valueOf(e));
            e.printStackTrace();
        }
        return entity;
    }

    @ApiOperation(value = "已生成出库清单列表")
    @PostMapping("/createOutboundList")
    public Response createOutboundList(@RequestBody createInventoryListParam param) {
        Response response = new Response();
        Map<Object, Object> map = new HashMap<>();
        map.put("current", (param.getCurrent() - 1) * param.getSize());
        map.put("size", param.getSize());
        map.put("likes", param.getLikes());
        List<Outbound> outboundList = outboundService.createOutboundList(map);
        Integer count = outboundService.createOutboundCount(map);
        Map<Object, Object> result = new HashMap<>();
        result.put("total", count);
        result.put("size", param.getSize());
        result.put("current", param.getCurrent());
        result.put("pages", (count % param.getSize()) == 0 ? count / param.getSize() : count / param.getSize() + 1);//总条数
        result.put("records", outboundList);
        response.setStatusCode(200);
        response.setData(result);
        return response;
    }


    @ApiOperation("导出海外仓批量模板Excel-多用户单SKU")
    @GetMapping(value = "/exportBatchWarehouseExcel")
    public ResponseEntity exportBatchWarehouseExcel() {
        List<String> headRow1 = Lists.newArrayList("产品SKU编码（必填）", "出库产品数量（必填）", "收件人姓名（必填）", "收件人公司（选填）", "收件人国家（必填）", "收件人州/省（简写）（必填）", "收件人城市（全称）（必填）", "收件人主要地址（必填）"
                , "收件人门牌号（选填）", "收件人邮编一(5位编码)（必填)", "收件人邮编二(4位编码)（选填)", "收件人邮箱（选填）", "收件人电话所属国家（86/1）（必填）", "收件人手机号(不要使用字符隔开)（必填）");
        ExcelWriter writer = ExcelUtil.getWriter();

        //合并单元格
        writer.renameSheet(0, "海外仓批量导入模版-多用户单SKU");
        writer.writeHeadRow(headRow1);

        //设置单元格格式为文本格式
        Sheet sheet = writer.getSheet();
        Workbook workbook = sheet.getWorkbook();
        HSSFCellStyle textStyle = (HSSFCellStyle) workbook.createCellStyle();
        HSSFDataFormat format = (HSSFDataFormat) workbook.createDataFormat();
        textStyle.setDataFormat(format.getFormat("@"));
        //设置第七列单元格格式为文本
        sheet.setDefaultColumnStyle(12, textStyle);

        for (int i = 0; i < headRow1.size(); i++) {
            writer.autoSizeColumn(i);
            writer.setColumnWidth(i, 30);
        }
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "海外仓批量导入模版-多用户单SKU.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiOperation("导出海外仓批量模板Excel--多用户多SKU")
    @GetMapping(value = "/exportBatchMoreSkuWarehouseExcel")
    public ResponseEntity exportBatchMoreSkuWarehouseExcel() {
        List<String> headRow1 = Lists.newArrayList(
                "产品1SKU编码（必填）", "出库产品1数量（必填）","产品2SKU编码（必填）", "出库产品2数量（必填）","产品3SKU编码（必填）", "出库产品3数量（必填）","产品4SKU编码（必填）", "出库产品4数量（必填）",
                "收件人姓名（必填）", "收件人公司（选填）", "收件人国家（必填）", "收件人州/省（简写）（必填）", "收件人城市（全称）（必填）", "收件人主要地址（必填）"
                , "收件人门牌号（选填）", "收件人邮编一(5位编码)（必填)", "收件人邮编二(4位编码)（选填)", "收件人邮箱（选填）", "收件人电话所属国家（86/1）（必填）", "收件人手机号(不要使用字符隔开)（必填）");
        ExcelWriter writer = ExcelUtil.getWriter();

        //合并单元格
        writer.renameSheet(0, "海外仓批量导入模版-多用户多SKU");
        writer.writeHeadRow(headRow1);

        //设置单元格格式为文本格式
        Sheet sheet = writer.getSheet();
        Workbook workbook = sheet.getWorkbook();
        HSSFCellStyle textStyle = (HSSFCellStyle) workbook.createCellStyle();
        HSSFDataFormat format = (HSSFDataFormat) workbook.createDataFormat();
        textStyle.setDataFormat(format.getFormat("@"));
        //设置第七列单元格格式为文本
        sheet.setDefaultColumnStyle(12, textStyle);

        for (int i = 0; i < headRow1.size(); i++) {
            writer.autoSizeColumn(i);
            writer.setColumnWidth(i, 30);
        }
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "海外仓批量导入模版-多用户多SKU.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiOperation(value = "批量导入海外仓出库信息")
    @PostMapping("/importWarehouseInfo")
    public Response importWarehouseInfo(MultipartFile excelFile) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        List<ImportWarehouseInfoParam> insertEntityList = null;
        if (excelFile == null) {
            response.setStatusCode(600);
            response.setMsg("Excel文件丢失");
            return response;
        }
        //是否有错误数据
        boolean flag = true;
        //错误信息集合
        List<ErrorMsg> errorMsgList = new ArrayList<>();
        //excel表信息
        List<List<Object>> lines = new ArrayList<>();
        try {
            lines = ExcelUtil.getReader(excelFile.getInputStream()).read();
        } catch (Exception e) {
            log.error("parse error:", e);
            response.setStatusCode(601);
            response.setMsg("文件解析异常，请检查excel文件格式是否正确!");
            return response;
        }
        insertEntityList = Lists.newArrayList();
        //序号
        int nid = 0;
        for (int i = 1; i < lines.size(); i++) {
            nid++;
            //返回的信息类
            ImportWarehouseInfoParam infoParam = new ImportWarehouseInfoParam();
            infoParam.setNid(nid);

            //行数据，如果不足14列，进行空格补齐
            List<Object> line = lines.get(i);
            int size = line.size();
            if ((14 - size) > 0) {
                for (int k = 0; k < 14 - size; k++) {
                    line.add("");
                }
            }

            //列号
            int index = 0;
            //sku产品编码
            String skuCode = String.valueOf(line.get(index++));
            EntityWrapper<ProductInfo> wrapper = new EntityWrapper<>();
            wrapper.eq("sku_code", skuCode);
            wrapper.eq("user_id", loginUserId);
            //产品信息
            ProductInfo productInfo = productInfoService.selectOne(wrapper);
            if (null == productInfo) {
                flag = false;
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 未找到SKU编码为" + skuCode + "的产品");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
                continue;
            } else {
                infoParam.setSkuCode(skuCode);
                infoParam.setEname(productInfo.getEName());
                infoParam.setCname(productInfo.getCName());
                infoParam.setUnitWeight(productInfo.getUnitWeight());
                infoParam.setInventoryNumber(productInfo.getInventoryNumber());
                infoParam.setProducerId(productInfo.getId());
            }
            //sku产品出库数量
            Integer producerNumber = 0;
            try {
                String s = String.valueOf(line.get(index++));
                if ("null".equals(s) || StringUtils.isEmpty(s)) {
                    producerNumber = 0;
                    throw new NullPointerException();
                }
                producerNumber = Integer.parseInt(s);
            } catch (Exception e) {
                flag = false;
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 出库数量填写错误");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            }

            if (flag) {
                if (producerNumber <= 0) {
                    //错误信息类
                    ErrorMsg errorMsg = new ErrorMsg();
                    errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 出库数量必须大于0（必填）");
                    errorMsg.setLinsIndex(i + 1);
                    errorMsg.setColumnIndex(index);
                    errorMsgList.add(errorMsg);
                }
            }
            infoParam.setProducerNumber(producerNumber);

            //姓名
            String name = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(name) || "null".equals(name)) {
                flag = false;
                name = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 收件人姓名（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            }
            //公司
            String company = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(company) || "null".equals(company)) {
                company = "";
            }
            //国家
            String countries = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(countries) || "null".equals(countries)) {
                flag = false;
                countries = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 国家（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            }
            //省份(英)
            String provinceEname = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(provinceEname) || "null".equals(provinceEname)) {
                flag = false;
                provinceEname = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 省/州（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            }
            //城市(英)
            String cityEname = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(cityEname) || "null".equals(cityEname)) {
                flag = false;
                cityEname = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 城市（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            }
            //主要地址
            String addressTwo = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(addressTwo) || "null".equals(addressTwo)) {
                flag = false;
                addressTwo = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 收件人主要地址（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            }
            //门牌号
            String addressOne = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(addressOne) || "null".equals(addressOne)) {
                addressOne = "";
            }
            //邮政编码一
            String postalCode = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(postalCode) || "null".equals(postalCode)) {
                flag = false;
                postalCode = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 邮编（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            } else {
                if (postalCode.length() < 3) {
                    flag = false;
                    postalCode = "";
                    //错误信息类
                    ErrorMsg errorMsg1 = new ErrorMsg();
                    errorMsg1.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 邮编格式错误（必填）");
                    errorMsg1.setLinsIndex(i + 1);
                    errorMsg1.setColumnIndex(index);
                    errorMsgList.add(errorMsg1);
                }
            }
            //邮政编码二
            String postalCodet = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(postalCodet) || "null".equals(postalCodet)) {
                postalCodet = "";
            }
            //邮箱
            String email = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(email) || "null".equals(email)) {
                email = "";
            }
            //电话前缀
            String phonePrefix = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(phonePrefix) || "null".equals(phonePrefix)) {
                flag = false;
                phonePrefix = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 收件人手机号所属国家（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            }
            //电话
            String phone = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(phone) || "null".equals(phone)) {
                flag = false;
                phone = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 收件人手机号（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            }

            infoParam.setName(name);
            infoParam.setCompany(company);
            infoParam.setCountries(countries);
            infoParam.setProvinceEname(provinceEname);
            infoParam.setCityEname(cityEname);
            infoParam.setPostalCode(postalCode);
            infoParam.setPostalCodet(postalCodet);
            infoParam.setAddressOne(addressOne);
            infoParam.setAddressTwo(addressTwo);
            infoParam.setPhone(phone);
            infoParam.setPhonePrefix(phonePrefix);
            infoParam.setEmail(email);
            insertEntityList.add(infoParam);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("resultDate", insertEntityList);
        map.put("errorMsg", errorMsgList);
        if (flag) {
            response.setStatusCode(200);
            response.setMsg("批量导入成功");
        } else {
            response.setStatusCode(199);
            response.setMsg("批量导入失败");
        }
        response.setData(map);
        return response;
    }


    @ApiOperation(value = "批量导入海外仓多SKU出库信息")
    @PostMapping("/importMoreSkuWarehouseInfo")
    public Response importMoreSkuWarehouseInfo(MultipartFile excelFile) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        List<ImportMoreSkuWarehouseInfoParam> insertEntityList = null;
        if (excelFile == null) {
            response.setStatusCode(600);
            response.setMsg("Excel文件丢失");
            return response;
        }
        //是否有错误数据
        boolean flag = true;
        //错误信息集合
        List<ErrorMsg> errorMsgList = new ArrayList<>();
        //excel表信息
        List<List<Object>> lines = new ArrayList<>();
        try {
            lines = ExcelUtil.getReader(excelFile.getInputStream()).read();
        } catch (Exception e) {
            log.error("parse error:", e);
            response.setStatusCode(601);
            response.setMsg("文件解析异常，请检查excel文件格式是否正确!");
            return response;
        }
        insertEntityList = Lists.newArrayList();
        //序号
        int nid = 0;
        for (int i = 1; i < lines.size(); i++) {
            nid++;
            //返回的信息类
            ImportMoreSkuWarehouseInfoParam infoParam = new ImportMoreSkuWarehouseInfoParam();
            infoParam.setNid(nid);

            //行数据，如果不足20列，进行空格补齐
            List<Object> line = lines.get(i);
            int size = line.size();
            if ((20 - size) > 0) {
                for (int k = 0; k < 20 - size; k++) {
                    line.add("");
                }
            }

            //列号
            int index = 0;
            int skuGroupNum = 4;
            int mustIndex = 1;   //最后必填的下标
            //========== 开始解析多Sku
            for (int j = 0; j < skuGroupNum; j++) {
                ImportMoreSkuWarehouseInfoParam.ProductionVo pVo = new ImportMoreSkuWarehouseInfoParam.ProductionVo();
                pVo.index = j;
                //sku产品出库数量
                Integer producerNumber = 0;
                //sku产品编码
                String skuCode = String.valueOf(line.get(index++));
                String productionNumberStr = String.valueOf(line.get(index + 1));
                log.info(" skuCode: " + skuCode + ", productionNumberStr: " + productionNumberStr);
                //如果都为空，认为是选填的
                if(!StringUtils.isEmpty(skuCode) || !StringUtils.isEmpty(productionNumberStr)) {
                    EntityWrapper<ProductInfo> wrapper = new EntityWrapper<>();
                    wrapper.eq("sku_code", skuCode);
                    wrapper.eq("user_id", loginUserId);
                    //产品信息
                    ProductInfo productInfo = productInfoService.selectOne(wrapper);
                    //sku产品出库数量
                    if (null == productInfo) {
                        flag = false;
                        pVo.valid = false;
                        //错误信息类
                        ErrorMsg errorMsg = new ErrorMsg();
                        errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 未找到SKU编码为" + skuCode + "的产品");
                        errorMsg.setLinsIndex(i + 1);
                        errorMsg.setColumnIndex(index);
                        errorMsgList.add(errorMsg);
                    } else {
                        pVo.setSkuCode(skuCode);
                        pVo.setEname(productInfo.getEName());
                        pVo.setCname(productInfo.getCName());
                        pVo.setUnitWeight(productInfo.getUnitWeight());
                        pVo.setInventoryNumber(productInfo.getInventoryNumber());
                        pVo.setProducerId(productInfo.getId());
                    }
                    try {
                        index++;
                        if ("null".equals(productionNumberStr) || StringUtils.isEmpty(productionNumberStr)) {
                            producerNumber = 0;
                            throw new NullPointerException();
                        }
                        producerNumber = Integer.parseInt(productionNumberStr);
                    } catch (Exception e) {
                        flag = false;
                        pVo.valid = false;
                        //错误信息类
                        ErrorMsg errorMsg = new ErrorMsg();
                        errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 出库数量填写错误");
                        errorMsg.setLinsIndex(i + 1);
                        errorMsg.setColumnIndex(index);
                        errorMsgList.add(errorMsg);
                    }

                    if (!flag && producerNumber <= 0) {
                        //错误信息类
                        flag = false;
                        pVo.valid = false;
                        ErrorMsg errorMsg = new ErrorMsg();
                        errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 出库数量必须大于0（必填）");
                        errorMsg.setLinsIndex(i + 1);
                        errorMsg.setColumnIndex(index);
                        errorMsgList.add(errorMsg);
                    }
                } else {
                    pVo.valid = false;
                }

                pVo.setProducerNumber(producerNumber);
                infoParam.getProductionVo().add(pVo);
            }
            //========= 上述要进行多sku的包装

            //姓名
            String name = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(name) || "null".equals(name)) {
                flag = false;
                name = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 收件人姓名（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            }
            //公司
            String company = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(company) || "null".equals(company)) {
                company = "";
            }
            //国家
            String countries = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(countries) || "null".equals(countries)) {
                flag = false;
                countries = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 国家（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            }
            //省份(英)
            String provinceEname = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(provinceEname) || "null".equals(provinceEname)) {
                flag = false;
                provinceEname = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 省/州（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            }
            //城市(英)
            String cityEname = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(cityEname) || "null".equals(cityEname)) {
                flag = false;
                cityEname = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 城市（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            }
            //主要地址
            String addressTwo = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(addressTwo) || "null".equals(addressTwo)) {
                flag = false;
                addressTwo = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 收件人主要地址（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            }
            //门牌号
            String addressOne = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(addressOne) || "null".equals(addressOne)) {
                addressOne = "";
            }
            //邮政编码一
            String postalCode = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(postalCode) || "null".equals(postalCode)) {
                flag = false;
                postalCode = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 邮编（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            } else {
                if (postalCode.length() < 3) {
                    flag = false;
                    postalCode = "";
                    //错误信息类
                    ErrorMsg errorMsg1 = new ErrorMsg();
                    errorMsg1.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 邮编格式错误（必填）");
                    errorMsg1.setLinsIndex(i + 1);
                    errorMsg1.setColumnIndex(index);
                    errorMsgList.add(errorMsg1);
                }
            }
            //邮政编码二
            String postalCodet = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(postalCodet) || "null".equals(postalCodet)) {
                postalCodet = "";
            }
            //邮箱
            String email = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(email) || "null".equals(email)) {
                email = "";
            }
            //电话前缀
            String phonePrefix = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(phonePrefix) || "null".equals(phonePrefix)) {
                flag = false;
                phonePrefix = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 收件人手机号所属国家（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            }
            //电话
            String phone = String.valueOf(line.get(index++));
            if (StringUtils.isEmpty(phone) || "null".equals(phone)) {
                flag = false;
                phone = "";
                //错误信息类
                ErrorMsg errorMsg = new ErrorMsg();
                errorMsg.setErrorMsg("Excel第" + (i + 1) + "行第" + index + "列: 收件人手机号（必填）");
                errorMsg.setLinsIndex(i + 1);
                errorMsg.setColumnIndex(index);
                errorMsgList.add(errorMsg);
            }

            infoParam.setName(name);
            infoParam.setCompany(company);
            infoParam.setCountries(countries);
            infoParam.setProvinceEname(provinceEname);
            infoParam.setCityEname(cityEname);
            infoParam.setPostalCode(postalCode);
            infoParam.setPostalCodet(postalCodet);
            infoParam.setAddressOne(addressOne);
            infoParam.setAddressTwo(addressTwo);
            infoParam.setPhone(phone);
            infoParam.setPhonePrefix(phonePrefix);
            infoParam.setEmail(email);
            insertEntityList.add(infoParam);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("resultDate", insertEntityList);
        map.put("errorMsg", errorMsgList);
        if (flag) {
            response.setStatusCode(200);
            response.setMsg("批量导入成功");
        } else {
            response.setStatusCode(199);
            response.setMsg("批量导入失败");
        }
        response.setData(map);
        return response;
    }

    @Data
    public class ErrorMsg {

        //行号
        private Integer linsIndex;
        //列号
        private Integer columnIndex;
        //错误信息
        private String errorMsg;
    }
}
