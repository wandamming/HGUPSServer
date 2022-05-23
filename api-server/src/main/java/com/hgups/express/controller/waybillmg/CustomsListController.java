package com.hgups.express.controller.waybillmg;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.exception.MyException;
import com.hgups.express.service.adminmgi.ShippingBatchService;
import com.hgups.express.service.adminmgi.ShippingSacksService;
import com.hgups.express.service.usermgi.RightsManagementService;
import com.hgups.express.service.usermgi.UserService;
import com.hgups.express.service.waybillmgi.CustomsListService;
import com.hgups.express.service.waybillmgi.PointScanRecordService;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.util.MyFileUtil;
import com.hgups.express.util.PathUtils;
import com.hgups.express.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fanc
 * 2020/6/20 0020-14:53
 */
@Api(description = "报关、清关清单API")
@Slf4j
@RestController
@RequestMapping("/customsList")
public class CustomsListController {


    @Resource
    private CustomsListService customsListService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private WayBillService wayBillService;
    @Autowired
    private ShippingBatchService shippingBatchService;
    @Resource
    private ShippingSacksService shippingSacksService;

    @Resource
    private RightsManagementService rightsManagementService;
    @Resource
    private UserService userService;
    @Resource
    private PointScanRecordService pointScanRecordService;


    @ApiOperation(value = "验证订单号")
    @PostMapping("/checkWayBill")
    public Response checkWayBill(@RequestBody CheckWayBillParam param) {
        List<ParamId> paramIds = wayBillService.checkWayBill(param.getWayBillNumber());
        List list = new ArrayList();
        for (int i = 0; i < paramIds.size(); i++) {
            list.add(paramIds.get(i).getId());
        }
        Map<Object, Object> result = new HashMap<>();
        result.put("total", paramIds.size());
        result.put("wids", list);
        return new Response(200, "", result);
    }


    @ApiOperation(value = "导出清关清单Excel")
    @GetMapping("exportCustomsList")
    public ResponseEntity exportCustomsList(@RequestParam List wids,//运单ID
                                            @RequestParam Integer sid,//发件人ID
                                            @RequestParam String wamb,
                                            @RequestParam String airCarrierCode,
                                            @RequestParam String departureAirport,
                                            @RequestParam String airportOfArrival,
                                            @RequestParam String flightNo,
                                            @RequestParam String receiveCity,
                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime) {
        System.out.println("清关运单ID----》》" + wids);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String str = df.format(startTime);
        System.out.println(str);
        ShiroUtil.getLoginUserId();
        Map<Object, Object> map = new HashMap<>();
        map.put("wids", wids);

        //把新运单ID换成旧运单ID，否则会查不到
        List<String> newTrackingNumber = wayBillService.getOldTrackingNumber(map);
        Map map1 = new HashMap<>();
        map1.put("trackingNumbers", newTrackingNumber);
        if (newTrackingNumber != null && newTrackingNumber.size() != 0) {
            List<Integer> oldId = wayBillService.getIdByTrackingNumber(map1);
            for (Integer integer : oldId) {
                wids.add(integer);
            }
        }
        List<CustomsList> customsList = customsListService.getCustomsList(map);
        List<CustomsList> customsList2 = new ArrayList<>();
        Set<String> customsListSet = new HashSet<>();
        int count = 0;
        for (CustomsList customsList1 : customsList) {
            String trackingNumber = customsList1.getWayBill().getTrackingNumber();
            count++;
            customsListSet.add(trackingNumber);
            if (customsListSet.size() == count) {
                customsList2.add(customsList1);
                continue;
            }
            customsListSet.add(String.valueOf(count));
        }

        for (CustomsList list : customsList2) {
            Integer newWayBillId = list.getWayBill().getNewWayBillId();
            if (newWayBillId != null && newWayBillId != -1) {
                list.setWayBill(wayBillService.selectById(newWayBillId));
            }
        }

        List<String> headRow1 = Lists.newArrayList("MAWB (航空主运单号)", "", "", wamb);
        List<String> headRow2 = Lists.newArrayList("航班号", "", "", flightNo);
        List<String> headRow3 = Lists.newArrayList("航班出发时间", "", "", str);
        List<String> headRow4 = Lists.newArrayList("SacksNumber", "HAWBNumber ", "AirCarrierCode", "DepartureAirport", "AirportOfArrival"
                , "ScheduledDateOfArrival", "ShipperName", "ShipperAddress1", "ShipperAddress2", "ShipperCity", "ShipperPostalCode", "ShipperProvince", "ShipperStateProvinceCode", "ShipperCountry"
                , "ConsigneeName", "ConsigneeAddress1", "ConsigneeAddress2", "ConsigneeProvince", "ConsigneeCity", "ConsigneeStateProvinceCode", "ConsigneePostalCode", "ConsigneeCountry", "HAWBPieceCount", "HAWBWeight", "PIECEUOM", "ChineseDescription", "EnglishDescription", "CountryOfOrigin"
                , "HAWBValue", "CurrencyCode", "HAWBTrackingNumber", "BagContainerTrackingNumber", "ConsigneePhoneNumber", "LastMileCarrier", "HTS Code");
        List<String> headRow5 = Lists.newArrayList("麻袋单号", "分单 /货运代理运单", "航空公司代码"
                , "出发机场", "到达机场", "到达日期", "发货人名", "发货人地址1", "发货人地址2"
                , "发货人城市", "发货人邮编", "发货人省/州", "发货人州省代码", "发货人国家", "收货人名", "收货人地址1", "收货人地址2", "收货人省/州"
                , "收货人城市", "收货人市代码", "收货人邮编", "收货人国家", "件数", "重量(lb)", "-----", "中文品名", "英文品名", "生产地", "价值(￥:人民币)", "货币代码"
                , "分单单号", "-----", "收件人电话", "最后承载人", "清关代码");
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.merge(0, 0, 0, 2, headRow1, true);
        writer.merge(1, 1, 0, 2, headRow2, true);
        writer.merge(2, 2, 0, 2, headRow3, true);
        writer.writeHeadRow(headRow1);
        writer.writeHeadRow(headRow2);
        writer.writeHeadRow(headRow3);
        writer.writeHeadRow(headRow4);
        writer.writeHeadRow(headRow5);
        for (int i = 0; i < headRow4.size(); i++) {
            writer.autoSizeColumn(i);
            writer.setColumnWidth(i, 30);
        }
        for (int i = 0; i < headRow5.size(); i++) {
            writer.autoSizeColumn(i);
            writer.setColumnWidth(i, 30);
        }

        customsList2.forEach(x -> {
            List<Object> dataList = Lists.newArrayList();
            int shippingSacksId = x.getWayBill().getShippingSacksId();
            ShippingSacks shippingSacks = shippingSacksService.selectById(shippingSacksId);
            if (shippingSacks != null) {
                dataList.add(shippingSacks.getSacksNumber());
            } else {
                dataList.add("---");
            }
            dataList.add(x.getWayBill().getTrackingNumber());
            dataList.add(airCarrierCode);
            dataList.add(departureAirport);
            dataList.add(airportOfArrival);
            dataList.add("---");

            dataList.add(x.getWaybillContact().getSenderName());
            dataList.add(x.getWaybillContact().getSenderAddressOne());
            dataList.add(x.getWaybillContact().getSenderAddressTwo());
            dataList.add(x.getWaybillContact().getSenderCity());
            dataList.add(x.getWaybillContact().getSenderPostalCode());
            dataList.add(x.getWaybillContact().getSenderProvince());
            dataList.add("---");
            dataList.add(x.getWaybillContact().getSenderCountries());

            dataList.add(x.getWaybillContact().getReceiveName());
            dataList.add(x.getWaybillContact().getReceiveAddressOne());
            dataList.add(x.getWaybillContact().getReceiveAddressTwo());
            dataList.add(x.getWaybillContact().getReceiveProvince());
            dataList.add(x.getWaybillContact().getReceiveCity());
            dataList.add("---");
            dataList.add(x.getWaybillContact().getReceivePostalCode());
            dataList.add(x.getWaybillContact().getReceiveCountries());

            dataList.add("1");
            dataList.add(x.getWayBill().getWareWeight());
            dataList.add("K");
            dataList.add(x.getArticle().getCDescribe());
            dataList.add(x.getArticle().getEDescribe());
            dataList.add("CN");
            dataList.add(x.getArticle().getPrice() * x.getArticle().getNumber());
            dataList.add("USD");

            dataList.add(x.getWayBill().getTrackingNumber());
            dataList.add("2");
            dataList.add("+" + x.getWaybillContact().getReceivePhonePrefix() + " " + x.getWaybillContact().getReceivePhone());
            dataList.add("USPS");
            dataList.add(x.getArticle().getHtsEncode());

            writer.writeRow(dataList);
        });

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "清关清单.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiOperation(value = "根据批次导出清关清单Excel")
    @GetMapping("exportCustomsByBatchList")
    public ResponseEntity exportCustomsList(@RequestParam(value = "batchId") Integer batchId,//批次ID
                                            @RequestParam Integer sid,//发件人ID
                                            @RequestParam String wamb,
                                            @RequestParam String airCarrierCode,
                                            @RequestParam String departureAirport,
                                            @RequestParam String airportOfArrival,
                                            @RequestParam String flightNo,
                                            @RequestParam String receiveCity,
                                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime) {
        System.out.println("清关批次ID----》》" + batchId);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String str = df.format(startTime);
        System.out.println(str);
        ShiroUtil.getLoginUserId();
        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.eq("shipping_batch_id", batchId);
        wrapper.setSqlSelect("id");
        List<WayBill> wayBills = wayBillService.selectList(wrapper);
        List<Integer> wids = wayBills.stream().map(w -> w.getId()).collect(Collectors.toList());
        Map<Object, Object> map = new HashMap<>();
        map.put("wids", wids);
        //把新运单ID换成旧运单ID，否则会查不到
        List<String> newTrackingNumber = wayBillService.getOldTrackingNumber(map);
        Map map1 = new HashMap<>();
        map1.put("trackingNumbers", newTrackingNumber);
        if (newTrackingNumber != null && newTrackingNumber.size() != 0) {
            List<Integer> oldId = wayBillService.getIdByTrackingNumber(map1);
            for (Integer integer : oldId) {
                wids.add(integer);
            }
        }
        List<CustomsList> customsList = customsListService.getCustomsList(map);
        List<CustomsList> customsList2 = new ArrayList<>();
        Set<String> customsListSet = new HashSet<>();
        int count = 0;
        for (CustomsList customsList1 : customsList) {
            String trackingNumber = customsList1.getWayBill().getTrackingNumber();
            count++;
            customsListSet.add(trackingNumber);
            if (customsListSet.size() == count) {
                customsList2.add(customsList1);
                continue;
            }
            customsListSet.add(String.valueOf(count));
        }

        for (CustomsList list : customsList2) {
            Integer newWayBillId = list.getWayBill().getNewWayBillId();
            if (newWayBillId != null && newWayBillId != -1) {
                list.setWayBill(wayBillService.selectById(newWayBillId));
            }
        }

        List<String> headRow1 = Lists.newArrayList("MAWB (航空主运单号)", "", "", wamb);
        List<String> headRow2 = Lists.newArrayList("航班号", "", "", flightNo);
        List<String> headRow3 = Lists.newArrayList("航班出发时间", "", "", str);
        List<String> headRow4 = Lists.newArrayList("SacksNumber", "HAWBNumber ", "AirCarrierCode", "DepartureAirport", "AirportOfArrival"
                , "ScheduledDateOfArrival", "ShipperName", "ShipperAddress1", "ShipperAddress2", "ShipperCity", "ShipperPostalCode", "ShipperProvince", "ShipperStateProvinceCode", "ShipperCountry"
                , "ConsigneeName", "ConsigneeAddress1", "ConsigneeAddress2", "ConsigneeProvince", "ConsigneeCity", "ConsigneeStateProvinceCode", "ConsigneePostalCode", "ConsigneeCountry", "HAWBPieceCount", "HAWBWeight", "PIECEUOM", "ChineseDescription", "EnglishDescription", "CountryOfOrigin"
                , "HAWBValue", "CurrencyCode", "HAWBTrackingNumber", "BagContainerTrackingNumber", "ConsigneePhoneNumber", "LastMileCarrier", "HTS Code");
        List<String> headRow5 = Lists.newArrayList("麻袋单号", "分单 /货运代理运单", "航空公司代码"
                , "出发机场", "到达机场", "到达日期", "发货人名", "发货人地址1", "发货人地址2"
                , "发货人城市", "发货人邮编", "发货人省/州", "发货人州省代码", "发货人国家", "收货人名", "收货人地址1", "收货人地址2", "收货人省/州"
                , "收货人城市", "收货人市代码", "收货人邮编", "收货人国家", "件数", "重量(lb)", "-----", "中文品名", "英文品名", "生产地", "价值(￥:人民币)", "货币代码"
                , "分单单号", "-----", "收件人电话", "最后承载人", "清关代码");
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.merge(0, 0, 0, 2, headRow1, true);
        writer.merge(1, 1, 0, 2, headRow2, true);
        writer.merge(2, 2, 0, 2, headRow3, true);
        writer.writeHeadRow(headRow1);
        writer.writeHeadRow(headRow2);
        writer.writeHeadRow(headRow3);
        writer.writeHeadRow(headRow4);
        writer.writeHeadRow(headRow5);
        for (int i = 0; i < headRow4.size(); i++) {
            writer.autoSizeColumn(i);
            writer.setColumnWidth(i, 30);
        }
        for (int i = 0; i < headRow5.size(); i++) {
            writer.autoSizeColumn(i);
            writer.setColumnWidth(i, 30);
        }

        customsList2.forEach(x -> {
            List<Object> dataList = Lists.newArrayList();
            int shippingSacksId = x.getWayBill().getShippingSacksId();
            ShippingSacks shippingSacks = shippingSacksService.selectById(shippingSacksId);
            if (shippingSacks != null) {
                dataList.add(shippingSacks.getSacksNumber());
            } else {
                dataList.add("---");
            }
            dataList.add(x.getWayBill().getTrackingNumber());
            dataList.add(airCarrierCode);
            dataList.add(departureAirport);
            dataList.add(airportOfArrival);
            dataList.add("---");

            dataList.add(x.getWaybillContact().getSenderName());
            dataList.add(x.getWaybillContact().getSenderAddressOne());
            dataList.add(x.getWaybillContact().getSenderAddressTwo());
            dataList.add(x.getWaybillContact().getSenderCity());
            dataList.add(x.getWaybillContact().getSenderPostalCode());
            dataList.add(x.getWaybillContact().getSenderProvince());
            dataList.add("---");
            dataList.add(x.getWaybillContact().getSenderCountries());

            dataList.add(x.getWaybillContact().getReceiveName());
            dataList.add(x.getWaybillContact().getReceiveAddressOne());
            dataList.add(x.getWaybillContact().getReceiveAddressTwo());
            dataList.add(x.getWaybillContact().getReceiveProvince());
            dataList.add(x.getWaybillContact().getReceiveCity());
            dataList.add("---");
            dataList.add(x.getWaybillContact().getReceivePostalCode());
            dataList.add(x.getWaybillContact().getReceiveCountries());

            dataList.add("1");
            dataList.add(x.getWayBill().getWareWeight());
            dataList.add("K");
            dataList.add(x.getArticle().getCDescribe());
            dataList.add(x.getArticle().getEDescribe());
            dataList.add("CN");
            dataList.add(x.getArticle().getPrice() * x.getArticle().getNumber());
            dataList.add("USD");

            dataList.add(x.getWayBill().getTrackingNumber());
            dataList.add("2");
            dataList.add("+" + x.getWaybillContact().getReceivePhonePrefix() + " " + x.getWaybillContact().getReceivePhone());
            dataList.add("USPS");
            dataList.add(x.getArticle().getHtsEncode());

            writer.writeRow(dataList);
        });

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "清关清单.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiOperation("导出报关清单Excel")
    @GetMapping(value = "/exportDeclarationlExcel")
    public ResponseEntity exportDeclarationlExcel(@RequestParam List wids,
                                                  @RequestParam Integer sid,//发件人ID
                                                  @RequestParam String wamb,
                                                  @RequestParam String flightNo,
                                                  @RequestParam String receiveCity
    ) {

        ShiroUtil.getLoginUserId();
        Map map = new HashMap<>();
        map.put("wids", wids);

        //把新运单ID换成旧运单ID，否则会查不到
        List<String> newTrackingNumber = wayBillService.getOldTrackingNumber(map);
        Map map1 = new HashMap<>();
        map1.put("trackingNumbers", newTrackingNumber);
        if (newTrackingNumber != null && newTrackingNumber.size() != 0) {
            List<Integer> oldId = wayBillService.getIdByTrackingNumber(map1);
            for (Integer integer : oldId) {
                wids.add(integer);
            }
        }

        List<CustomsList> customsList = customsListService.getCustomsList(map);
        List<CustomsList> customsList2 = new ArrayList<>();
        Set<String> customsListSet = new HashSet<>();
        int count = 0;
        for (CustomsList customsList1 : customsList) {
            String trackingNumber = customsList1.getWayBill().getTrackingNumber();
            count++;
            customsListSet.add(trackingNumber);
            if (customsListSet.size() == count) {
                customsList2.add(customsList1);
                continue;
            }
            customsListSet.add(String.valueOf(count));
        }

        for (CustomsList list : customsList2) {
            Integer newWayBillId = list.getWayBill().getNewWayBillId();
            if (newWayBillId != null && newWayBillId != -1) {
                list.setWayBill(wayBillService.selectById(newWayBillId));
            }
        }

        List<String> headRow = Lists.newArrayList("客户袋号", "麻袋编号", "分单号码", "中文品名", "英文品名", "商品编码", "申报数量", "件数"
                , "毛重(lb)", "净重(lb)", "价值(￥:人民币)", "币值", "收件人", "收件人地址", "发件人", "发件人地址", "收件人证件类型", "收件人证件号码", "发件人省/州"
                , "发件人城市", "经营单位编号", "经营单位名称", "货主代码", "报关类型", "客户代码", "鉴定书号", "仓储", "分单总重(lb)"
                , "标记", "快递单号", "发件人电话", "收件人电话", "收件人省/州", "收件人城市", "收件邮政编码");
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.writeHeadRow(headRow);
        for (int i = 0; i < headRow.size(); i++) {
            writer.autoSizeColumn(i);
            writer.setColumnWidth(i, 30);
        }
        customsList2.forEach(x -> {
            List<Object> dataList = Lists.newArrayList();
            dataList.add("----");
            int shippingSacksId = x.getWayBill().getShippingSacksId();
            ShippingSacks shippingSacks = shippingSacksService.selectById(shippingSacksId);
            if (shippingSacks != null) {
                dataList.add(shippingSacks.getSacksNumber());
            } else {
                dataList.add("---");
            }
            dataList.add(x.getWayBill().getTrackingNumber());
            dataList.add(x.getArticle().getCDescribe());
            dataList.add(x.getArticle().getEDescribe());
            dataList.add(x.getArticle().getHsEncode());
            dataList.add(x.getArticle().getNumber());
            dataList.add(x.getArticle().getNumber());
            dataList.add(x.getWayBill().getWareWeight());
            dataList.add("-----");
            dataList.add(x.getArticle().getPrice());
            dataList.add("502");
            dataList.add(x.getWaybillContact().getReceiveName());
            dataList.add(x.getWaybillContact().getReceiveAddressTwo());
            dataList.add(x.getWaybillContact().getSenderName());
            dataList.add(x.getWaybillContact().getSenderAddressTwo());
            dataList.add("-----");
            dataList.add("-----");
            dataList.add(x.getWaybillContact().getSenderProvince());
            dataList.add(x.getWaybillContact().getSenderCity());
            dataList.add("-----");
            dataList.add("-----");
            dataList.add("-----");
            dataList.add("C");
            dataList.add("-----");
            dataList.add("-----");
            dataList.add("-----");
            dataList.add("-----");
            dataList.add("-----");
            dataList.add(x.getWayBill().getTrackingNumber());
            dataList.add("+" + x.getWaybillContact().getSenderPhonePrefix() + " " + x.getWaybillContact().getSenderPhone());
            dataList.add("+" + x.getWaybillContact().getReceivePhonePrefix() + " " + x.getWaybillContact().getReceivePhone());
            dataList.add(x.getWaybillContact().getReceiveProvince());
            dataList.add(x.getWaybillContact().getReceiveCity());
            dataList.add(x.getWaybillContact().getReceivePostalCode());

            writer.writeRow(dataList);
        });

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "报关清单.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiOperation("根据批次导出报关清单Excel")
    @GetMapping(value = "/exportDeclarationlByBatchExcel")
    public ResponseEntity exportDeclarationlExcel(@RequestParam(value = "batchId") Integer batchId,
                                                  @RequestParam Integer sid,//发件人ID
                                                  @RequestParam String wamb,
                                                  @RequestParam String flightNo,
                                                  @RequestParam String receiveCity
    ) {

        ShiroUtil.getLoginUserId();
        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.eq("shipping_batch_id", batchId);
        wrapper.setSqlSelect("id");
        List<WayBill> wayBills = wayBillService.selectList(wrapper);
        List<Integer> wids = wayBills.stream().map(w -> w.getId()).collect(Collectors.toList());
        Map map = new HashMap<>();
        map.put("wids", wids);

        //把新运单ID换成旧运单ID，否则会查不到
        List<String> newTrackingNumber = wayBillService.getOldTrackingNumber(map);
        Map map1 = new HashMap<>();
        map1.put("trackingNumbers", newTrackingNumber);
        if (newTrackingNumber != null && newTrackingNumber.size() != 0) {
            List<Integer> oldId = wayBillService.getIdByTrackingNumber(map1);
            for (Integer integer : oldId) {
                wids.add(integer);
            }
        }

        List<CustomsList> customsList = customsListService.getCustomsList(map);
        List<CustomsList> customsList2 = new ArrayList<>();
        Set<String> customsListSet = new HashSet<>();
        int count = 0;
        for (CustomsList customsList1 : customsList) {
            String trackingNumber = customsList1.getWayBill().getTrackingNumber();
            count++;
            customsListSet.add(trackingNumber);
            if (customsListSet.size() == count) {
                customsList2.add(customsList1);
                continue;
            }
            customsListSet.add(String.valueOf(count));
        }

        for (CustomsList list : customsList2) {
            Integer newWayBillId = list.getWayBill().getNewWayBillId();
            if (newWayBillId != null && newWayBillId != -1) {
                list.setWayBill(wayBillService.selectById(newWayBillId));
            }
        }

        List<String> headRow = Lists.newArrayList("客户袋号", "麻袋编号", "分单号码", "中文品名", "英文品名", "商品编码", "申报数量", "件数"
                , "毛重(lb)", "净重(lb)", "价值(￥:人民币)", "币值", "收件人", "收件人地址", "发件人", "发件人地址", "收件人证件类型", "收件人证件号码", "发件人省/州"
                , "发件人城市", "经营单位编号", "经营单位名称", "货主代码", "报关类型", "客户代码", "鉴定书号", "仓储", "分单总重(lb)"
                , "标记", "快递单号", "发件人电话", "收件人电话", "收件人省/州", "收件人城市", "收件邮政编码");
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.writeHeadRow(headRow);
        for (int i = 0; i < headRow.size(); i++) {
            writer.autoSizeColumn(i);
            writer.setColumnWidth(i, 30);
        }
        customsList2.forEach(x -> {
            List<Object> dataList = Lists.newArrayList();
            dataList.add("----");
            int shippingSacksId = x.getWayBill().getShippingSacksId();
            ShippingSacks shippingSacks = shippingSacksService.selectById(shippingSacksId);
            if (shippingSacks != null) {
                dataList.add(shippingSacks.getSacksNumber());
            } else {
                dataList.add("---");
            }
            dataList.add(x.getWayBill().getTrackingNumber());
            dataList.add(x.getArticle().getCDescribe());
            dataList.add(x.getArticle().getEDescribe());
            dataList.add(x.getArticle().getHsEncode());
            dataList.add(x.getArticle().getNumber());
            dataList.add(x.getArticle().getNumber());
            dataList.add(x.getWayBill().getWareWeight());
            dataList.add("-----");
            dataList.add(x.getArticle().getPrice());
            dataList.add("502");
            dataList.add(x.getWaybillContact().getReceiveName());
            dataList.add(x.getWaybillContact().getReceiveAddressTwo());
            dataList.add(x.getWaybillContact().getSenderName());
            dataList.add(x.getWaybillContact().getSenderAddressTwo());
            dataList.add("-----");
            dataList.add("-----");
            dataList.add(x.getWaybillContact().getSenderProvince());
            dataList.add(x.getWaybillContact().getSenderCity());
            dataList.add("-----");
            dataList.add("-----");
            dataList.add("-----");
            dataList.add("C");
            dataList.add("-----");
            dataList.add("-----");
            dataList.add("-----");
            dataList.add("-----");
            dataList.add("-----");
            dataList.add(x.getWayBill().getTrackingNumber());
            dataList.add("+" + x.getWaybillContact().getSenderPhonePrefix() + " " + x.getWaybillContact().getSenderPhone());
            dataList.add("+" + x.getWaybillContact().getReceivePhonePrefix() + " " + x.getWaybillContact().getReceivePhone());
            dataList.add(x.getWaybillContact().getReceiveProvince());
            dataList.add(x.getWaybillContact().getReceiveCity());
            dataList.add(x.getWaybillContact().getReceivePostalCode());

            writer.writeRow(dataList);
        });

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "报关清单.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //ResponseEntity
    @ApiOperation("管理员下载后程用户上传附件")
    @GetMapping(value = "/downloadAttachment")
    public ResponseEntity downloadAttachment(@RequestParam Integer id) {
        ShiroUtil.getLoginUserId();
        ShippingBatch shippingBatch = shippingBatchService.selectById(id);
        File file = new File(shippingBatch.getAttachmentPath());
        log.info("文件名------------" + file.getName());
        FileInputStream fileInputStream = null;
        try {

            long fileSize = file.length();
            if (fileSize > Integer.MAX_VALUE) {
                System.out.println("file too big...");
                return null;
            }
            FileInputStream fi = new FileInputStream(file);
            byte[] buffer = new byte[(int) fileSize];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length
                    && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }
            // 确保所有数据均被读取
            if (offset != buffer.length) {
                throw new IOException("Could not completely read file "
                        + file.getName());
            }


            fileInputStream = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                    ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
            String filename = multipartFile.getOriginalFilename();
            //文件后缀名
            String[] fileList = filename.split(".");
            if (fileList.length == 0) {
                return null;
            }
            String suffixName = fileList[fileList.length - 1];
            //文件前缀名
            String caselsh = filename.substring(0, filename.lastIndexOf("."));
            String[] split = caselsh.split("####");
            log.info("下载文件--文件名" + split[0]);
            log.info("下载文件--文件名后缀" + suffixName);
            return MyFileUtil.downloadFile(buffer, split[0] + "." + suffixName, httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiOperation(value = "后程角色上传报关附件")
    @PostMapping("/uploadAttachment")
    public Response uploadAttachment(@RequestParam("file") MultipartFile file, @RequestParam("shippingBatchId") Integer shippingBatchId) {
        long current = System.currentTimeMillis();

        Response response = new Response();
        if (file == null) {
            response.setStatusCode(600);
            response.setMsg("Excel文件丢失");
            return response;
        }
        String filename = file.getOriginalFilename();
        //文件后缀名
        String suffixName = filename.substring(filename.lastIndexOf("."));
        //文件前缀名
        String caselsh = filename.substring(0, filename.lastIndexOf("."));
        FileOutputStream outputStream = null;
        InputStream fileSource = null;
        try {
            fileSource = file.getInputStream();
            String tempFileName = "";
            if (".txt".equalsIgnoreCase(suffixName)) {
                tempFileName = PathUtils.resDir + "customs/" + caselsh + "####" + current + response.hashCode() + ".txt";
            } else if (".xls".equalsIgnoreCase(suffixName)) {
                tempFileName = PathUtils.resDir + "customs/" + caselsh + "####" + current + response.hashCode() + ".xls";
            } else if (".xlsx".equalsIgnoreCase(suffixName)) {
                tempFileName = PathUtils.resDir + "customs/" + caselsh + "####" + current + response.hashCode() + ".xlsx";
            } else if (".pdf".equalsIgnoreCase(suffixName)) {
                tempFileName = PathUtils.resDir + "customs/" + caselsh + "####" + current + response.hashCode() + ".pdf";
            } else if (".doc".equalsIgnoreCase(suffixName)) {
                tempFileName = PathUtils.resDir + "customs/" + caselsh + "####" + current + response.hashCode() + ".doc";
            } else if (".docx".equalsIgnoreCase(suffixName)) {
                tempFileName = PathUtils.resDir + "customs/" + caselsh + "####" + current + response.hashCode() + ".docx";
            } else {
                response.setStatusCode(202);
                response.setMsg("暂不支持该文件格式");
                return response;
            }

            System.out.println("文件地址--======" + tempFileName);
            //tempFile指向临时文件
            File tempFile = new File(tempFileName);
            if (!tempFile.exists()) {
                tempFile.getParentFile().mkdirs();
                tempFile.createNewFile();
            }
            //outputStream文件输出流指向这个临时文件

            outputStream = new FileOutputStream(tempFile);

            byte[] b = new byte[1024];
            int n;
            while ((n = fileSource.read(b)) != -1) {
                outputStream.write(b, 0, n);
            }
            ShippingBatch shippingBatch = shippingBatchService.selectById(shippingBatchId);
            shippingBatch.setAttachmentPath(tempFileName);
            shippingBatchService.updateById(shippingBatch);
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatusCode(203);
            response.setMsg("文件上传出错");
            return response;
        } finally {
            //关闭输入输出流
            if (null != outputStream) {
                try {
                    outputStream.close();
                    fileSource.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        response.setStatusCode(200);
        response.setMsg("上传成功");
        return response;
    }

    //后程角色航运批次报关
    @ApiOperation(value = "申请报关")
    @PostMapping("/applyCustoms")
    @Transactional(rollbackFor = Exception.class)
    public Response applyCustoms(@RequestBody IdParam param) throws MyException {
        Long loginUserId = ShiroUtil.getLoginUserId();
        User user = userService.selectById(loginUserId);
        Response response = new Response();
        ShippingBatch shippingBatch = shippingBatchService.selectById(param.getId());
        shippingBatch.setState("4");
        shippingBatch.setUsername(user.getUsername());
        shippingBatch.setCustomsTime(new Date());
        boolean b = shippingBatchService.updateById(shippingBatch);
        List<ShippingSacks> shippingSacks = shippingSacksService.selectList(new EntityWrapper<ShippingSacks>().eq("shipping_batch_id", param.getId()));
        for (ShippingSacks shippingSack : shippingSacks) {
            // 麻袋报关
            pointScanRecordService.addSysRecord(1, shippingSack.getSacksNumber(), "reportCustoms", null, new Date(), null);
        }
        List<WayBill> wayBills = wayBillService.selectList(new EntityWrapper<WayBill>().eq("shipping_batch_id", param.getId()));
        for (WayBill wayBill : wayBills) {
            // 运单报关
            pointScanRecordService.addSysRecord(1, wayBill.getTrackingNumber(), "reportCustoms", null, new Date(), null);
        }
        if (b) {
            // 批次报关
            pointScanRecordService.addSysRecord(1, shippingBatch.getTrackingNumber(), "reportCustoms", null, new Date(), null);
            response.setStatusCode(200);
            response.setMsg("报关成功");
            return response;
        }
        response.setStatusCode(201);
        response.setMsg("报关失败");
        return response;
    }

    @ApiOperation(value = "申请清关")
    @PostMapping("/applyClearance")
    public Response applyClearance(@RequestBody IdParam param) throws MyException {
        Long loginUserId = ShiroUtil.getLoginUserId();
        User user = userService.selectById(loginUserId);
        Response response = new Response();
        ShippingBatch shippingBatch = shippingBatchService.selectById(param.getId());
        shippingBatch.setState("5");
        shippingBatch.setUsername(user.getUsername());
        shippingBatch.setClearanceTime(new Date());
        boolean b = shippingBatchService.updateById(shippingBatch);
        List<ShippingSacks> shippingSacks = shippingSacksService.selectList(new EntityWrapper<ShippingSacks>().eq("shipping_batch_id", param.getId()));
        for (ShippingSacks shippingSack : shippingSacks) {
            // 麻袋清关
            pointScanRecordService.addSysRecord(1, shippingSack.getSacksNumber(), "clearance", null, new Date(), null);
        }
        List<WayBill> wayBills = wayBillService.selectList(new EntityWrapper<WayBill>().eq("shipping_batch_id", param.getId()));
        for (WayBill wayBill : wayBills) {
            // 运单清关
            pointScanRecordService.addSysRecord(1, wayBill.getTrackingNumber(), "clearance", null, new Date(), null);
        }
        if (b) {
            // 批次报关
            pointScanRecordService.addSysRecord(1, shippingBatch.getTrackingNumber(), "clearance", null, new Date(), null);
            response.setStatusCode(200);
            response.setMsg("清关成功");
            return response;
        }
        response.setStatusCode(201);
        response.setMsg("清关失败");
        return response;
    }


    //后程角色航运批次报关
    @ApiOperation(value = "报关列表")
    @PostMapping("/customsList")
    public Response customsList(@RequestBody PageParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        EntityWrapper<ShippingBatch> wrapper = new EntityWrapper<>();
        int processRole = rightsManagementService.isProcessRole(loginUserId);
        if (processRole == 1) {
            wrapper.eq("user_id", loginUserId);
        }
        wrapper.eq("state", 4);
        Page<ShippingBatch> page = new Page<>(param.getCurrent(), param.getSize());

        Page<ShippingBatch> page1 = shippingBatchService.selectPage(page, wrapper);
        List<ShippingBatch> records = page1.getRecords();

        Map<Object, Object> map = new HashMap<>();
        int total = shippingBatchService.selectCount(wrapper);//总条数
        map.put("current", param.getCurrent());
        map.put("total", total);
        map.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总页数
        map.put("records", records);
        response.setStatusCode(200);
        response.setData(map);
        return response;
    }


    //后程角色航运批次报关
    @ApiOperation(value = "清关列表")
    @PostMapping("/clearanceList")
    public Response clearanceList(@RequestBody PageParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        EntityWrapper<ShippingBatch> wrapper = new EntityWrapper<>();
        int processRole = rightsManagementService.isProcessRole(loginUserId);
        if (processRole == 1) {
            wrapper.eq("user_id", loginUserId);
        }
        wrapper.eq("state", 5);
        Page<ShippingBatch> page = new Page<>(param.getCurrent(), param.getSize());

        Page<ShippingBatch> page1 = shippingBatchService.selectPage(page, wrapper);
        List<ShippingBatch> records = page1.getRecords();

        Map<Object, Object> map = new HashMap<>();
        int total = shippingBatchService.selectCount(wrapper);//总条数
        map.put("current", param.getCurrent());
        map.put("total", total);
        map.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总页数
        map.put("records", records);
        response.setStatusCode(200);
        response.setData(map);
        return response;
    }


}
