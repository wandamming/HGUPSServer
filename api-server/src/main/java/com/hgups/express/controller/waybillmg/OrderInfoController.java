package com.hgups.express.controller.waybillmg;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.domain.vo.*;
import com.hgups.express.mapper.CityMapper;
import com.hgups.express.mapper.PlatformMapper;
import com.hgups.express.mapper.StoreMapper;
import com.hgups.express.service.warehousemgi.ProductInfoService;
import com.hgups.express.service.waybillmgi.*;
import com.hgups.express.util.MyFileUtil;
import com.hgups.express.util.ResultParamUtil;
import com.hgups.express.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author wandaming
 * 2021/7/21-11:06
 */

@Api(description = "----------订单列表API")
@Slf4j
@RestController
@RequestMapping("/OrderList")
public class OrderInfoController {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private ProductInfoService productInfoService;

    @Resource
    private ChannelService channelService;
    

    @Resource
    private OrderTypeService orderTypeService;


    @Resource
    private PlatformMapper platformMapper;


    @Resource
    private DeliverModeService deliverModeService;


    @Resource
    private StoreMapper storeMapper;

    @Resource
    private CityMapper cityMapper;


    @ApiOperation(value = "显示订单列表API", notes = "此接口用于获取订单列表")
    @PostMapping("/getList")
    public Response getOrderList(@ApiParam(value = "订单列表参数") @RequestBody OrderParam param) {
        Response response = new Response();
        Page<OrderInfoVo> vo = orderInfoService.getOrderInfo(param);
        response.setData(vo);
        return response;
    }

    @ApiOperation(value = "显示订单详情API", notes = "此接口用于获取订单详情")
    @PostMapping("/getDetails")
    public Response getOrderDetails(@ApiParam(value = "id") @RequestBody IdParam param) {
        Response response = new Response();
        OrderDetailsVo vo = orderInfoService.getOrderDetails(param);
        response.setData(vo);
        return response;

    }


    @ApiOperation(value = "查看订单中商品信息API")
    @PostMapping("/getOrderProduct")
    public Response getOrderProduct(@RequestBody IdPageParam param) {
        Response response = new Response();
        Page<ProductInfoVo> vo = productInfoService.getOrderProduct(param);
        response.setData(vo);
        return response;
    }


    @ApiOperation(value = "批量删除订单列表API", notes = "此接口用于批量删除订单")
    @PostMapping("/deleteBatchOrder")
    public Response deleteBatchOrder(@RequestBody IdsParam param) {
        Response response = new Response();
        boolean flag = orderInfoService.deleteBatchOrder(param);
        if (flag) {
            response.setStatusCode(ResponseCode.SUCCESS_CODE);
            response.setMsg("删除成功");
            return response;
        }
        response.setStatusCode(ResponseCode.FAILED_CODE);
        response.setMsg("删除失败");
        return response;
    }


    @ApiOperation(value = "导出订单Excel", notes = "此接口用于将订单以Excel形式导出")
    @GetMapping("/exportExcel")
    public ResponseEntity exportOrderExcel(@RequestParam List orderList) {
        return orderInfoService.exportOrderExcel(orderList);
    }


    @ApiOperation(value = "发货", notes = "此接口用于发货操作，修改订单状态")
    @PostMapping("/deliverGoods")
    public Response deliverGoods(@RequestBody DeliverGoodsParam param) {
        Response response = new Response();
        List<DeliverGoodsVo> vo = new ArrayList<>();
        response.setData(vo);
        return response;
    }


    @ApiOperation(value = "费用预算API", notes = "此接口用于根据订单路线预算费用")
    @PostMapping(value = "/getCost")
    public Response getCost(@RequestBody OrderCostParam pageParam) {
        Response response = new Response();
        List<OrderCostVo> pageList = orderInfoService.getOrderCost(pageParam);
        Integer pageCount = orderInfoService.getOrderCostCount(pageParam);
        Map result = ResultParamUtil.result(pageList, pageCount, pageParam.getCurrent() / pageParam.getSize() + 1, pageParam.getSize());

        response.setData(result);
        return response;
    }


    @ApiOperation(value = "下拉框总接口", notes = "用于对订单筛选时所用的下拉框的显示，包括：订" +
            "单状态，所属平台，店铺名称，发货区域，" +
            "发货渠道，发货路线，配送方式，有无库存，订单类型")
    @PostMapping("/getComboBox")
    public Response getOrderComboBox() {
        Response response = new Response();
        //List<OrderComboBoxVo> vo = new ArrayList<>();

        List<Map<String, Object>> platformName = platformMapper.getPlatformName();

        List<Map<String, Object>> storeName = storeMapper.getStoreName();

        EntityWrapper<Channel> channelEntityWrapper = new EntityWrapper<>();
        channelEntityWrapper.eq("is_show", 1);
        List<Channel> channelList = channelService.selectList(channelEntityWrapper);

        List<Map<String, Object>> cityName = cityMapper.getCityName();

        EntityWrapper<DeliverMode> deliverModeEntityWrapper = new EntityWrapper<>();
        List<DeliverMode> modeList = deliverModeService.selectList(deliverModeEntityWrapper);

        EntityWrapper<OrderType> orderTypeEntityWrapper = new EntityWrapper<>();
        List<OrderType> orderTypeList = orderTypeService.selectList(orderTypeEntityWrapper);


        OrderComboBoxVo orderComboBoxVo = new OrderComboBoxVo();
        orderComboBoxVo.setPlatform(platformName);
        orderComboBoxVo.setStore(storeName);
        orderComboBoxVo.setCity(cityName);
        orderComboBoxVo.setChannel(channelList);
        orderComboBoxVo.setDeliverMode(modeList);
        orderComboBoxVo.setOrderType(orderTypeList);


        response.setData(orderComboBoxVo);
        return response;
    }


    @ApiOperation("导出批量模板Excel")
    @GetMapping(value = "/exportBatchOrderExcel")
    public ResponseEntity exportBatchOrderExcel() {
        List<String> headRow1 = Lists.newArrayList("序号", "平台订单号", "所属平台", "店铺名称", "电商订单号", "客户名称", "联系电话", "订单状态", "下单时间",
                "发件人","联系电话","发货城市","具体地址","邮政编码","发货渠道","发货路线","sku编号","图片","中文名称","英文名称","数量");
        ExcelWriter writer = ExcelUtil.getWriter();

        //合并单元格
        writer.renameSheet(0, "批量导入模板");
        writer.writeHeadRow(headRow1);

        //设置单元格格式为文本格式
        Sheet sheet = writer.getSheet();
        Workbook workbook = sheet.getWorkbook();
        HSSFCellStyle textStyle = (HSSFCellStyle) workbook.createCellStyle();
        HSSFDataFormat format = (HSSFDataFormat) workbook.createDataFormat();
        textStyle.setDataFormat(format.getFormat("@"));
        //设置第七列单元格格式为文本

        for (int i = 0; i < headRow1.size(); i++) {
            writer.autoSizeColumn(i);
            writer.setColumnWidth(i, 21);
        }

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "批量导入模版.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiOperation(value = "批量导入订单")
    @PostMapping("/insertBatchOrder")
    public Response insertBatchOrder(MultipartFile excelFile) {
        Response response = new Response();
        Response insertBatchOrder = orderInfoService.insertBatchOrder(excelFile);
        response.setData(insertBatchOrder);
        return response;
}


}

