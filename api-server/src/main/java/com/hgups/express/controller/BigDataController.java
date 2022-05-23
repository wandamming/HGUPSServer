package com.hgups.express.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.WayBill;
import com.hgups.express.domain.WaybillContact;
import com.hgups.express.domain.dto.CityWaybillSum;
import com.hgups.express.domain.param.AllProvinceWayBillParam;
import com.hgups.express.domain.param.ItemsWayBillParam;
import com.hgups.express.domain.param.WayBillBigDataProvinceParam;
import com.hgups.express.domain.param.WayBillKeyValueDataVo;
import com.hgups.express.service.waybillmgi.*;
import com.hgups.express.util.MyTransUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@Api(description = "大数据")
@Slf4j
@RestController
@RequestMapping("/bigdata")
public class BigDataController {

    @Resource
    private BigDataService bigDataService;
    @Resource
    private WayBillContactService wayBillContactService;
    @Resource
    private WayBillService wayBillService;


    @ApiOperation(value = "运单统计-今日运单饼状图")
    @GetMapping("/getTodayBillPieChart")
    public Response getTodayBillPieChart() {
        Response response = new Response();
        List<WayBillKeyValueDataVo> todayWayBillDataVoList = bigDataService.todayWayBillData();
        response.setData(todayWayBillDataVoList);
        return response;
    }

    @ApiOperation(value = "城市数据[城市名,数量，百分比]")
    @GetMapping("/getCityData")
    public Response getCityData() {
        Response response = new Response();
        //运单数量
//        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
//        wrapper.eq("new_way_bill_id",-1);
//        wrapper.setSqlSelect("id");
//        List<WayBill> wayBills = wayBillService.selectList(wrapper);
//        double size = wayBills.size();
//        //有运单省州数量
//        EntityWrapper<WaybillContact> wrapper1 = new EntityWrapper<>();
//        wrapper1.groupBy("receive_city");
//        wrapper1.setSqlSelect("receive_province");
//        List<WaybillContact> waybillContactProvince = wayBillContactService.selectList(wrapper1);
//        log.info(" test waybillContactProvince: " + String.valueOf(waybillContactProvince));


        List<CityWaybillSum> cityWaybillSumList = wayBillContactService.getCityWayBill();
        int total = wayBillContactService.countCityWayBill();
        for (CityWaybillSum sum : cityWaybillSumList) {
            double percent = sum.getTotal() * 1.0 / total * 100;
            sum.setPercent(String.valueOf(String.format("%.5f", percent) + "%"));
        }

//        int sum = 0;
//        for (WaybillContact waybillContact : waybillContactProvince) {
//            //因为数据采集问题，有部分省份是空数据，目前先暂时跳过
//            if(waybillContact == null) {
//                continue;
//            }
//            //省、州名
//            String proEname = waybillContact.getReceiveProvince();
//            //省对应各个城市运单数量、城市名称
//            List<AllProvinceWayBillParam> allProvinceWayBillParams = wayBillContactService.provinceGetCityWayBill(proEname);
//            for (AllProvinceWayBillParam allProvinceWayBillParam : allProvinceWayBillParams) {
//                sum++;
//            }
//        }
//        String[][] results = new String[sum][3];
//        sum=0;
//        for (int i = 0; i < waybillContactProvince.size(); i++) {
//            //因为数据采集问题，有部分省份是空数据，目前先暂时跳过
//            WaybillContact waybillContact = waybillContactProvince.get(i);
//            if(waybillContact == null) {
//                continue;
//            }
//            //省、州名
//            String proEname = waybillContact.getReceiveProvince();
//            //省对应各个城市运单数量、城市名称
//            List<AllProvinceWayBillParam> allProvinceWayBillParams = wayBillContactService.provinceGetCityWayBill(proEname);
//            for (int j = 0; j < allProvinceWayBillParams.size(); j++) {
//                results[sum][0] = allProvinceWayBillParams.get(j).getName();
//                results[sum][1] = String.valueOf(allProvinceWayBillParams.get(j).getValue());
//                double cityWayBillNumber = allProvinceWayBillParams.get(j).getValue();
//                double percent = cityWayBillNumber/size*100;
//                results[sum][2] = String.valueOf(String.format("%.4f", percent)+"%");
//                sum++;
//            }
//        }
        response.setData(cityWaybillSumList);
        return response;
    }


    @ApiOperation(value = "入境口岸运单数量")
    @GetMapping("/getPOEData")
    public Response getPOEData() {
        Response response = new Response();
        List<AllProvinceWayBillParam> portEntryWaybill = bigDataService.getPortEntryWaybill();
        response.setData(portEntryWaybill);
        return response;

    }

    @ApiOperation(value = "运单整体统计")
    @GetMapping("/getTotalBillStatistics")
    public Response getTotalBillPieChart() {
        Response response = new Response();
        List<WayBillKeyValueDataVo> totalBillPieChart = bigDataService.getTotalBillPieChart();
        response.setData(totalBillPieChart);
        return response;
    }

    @ApiOperation(value = "快递排行榜")
    @PostMapping("/wayBillRanking")
    public Response wayBillRanking(@RequestBody WayBillBigDataProvinceParam param) {
        Response response = new Response();
        List<AllProvinceWayBillParam> allProvinceWayBillParams = bigDataService.wayBillRanking(param.getName());
        for (AllProvinceWayBillParam allProvinceWayBillParam : allProvinceWayBillParams) {
            String name = allProvinceWayBillParam.getName();
            String newName = MyTransUtil.FirstLetterCapital(name);
            allProvinceWayBillParam.setName(newName);
        }
        Collections.sort(allProvinceWayBillParams);
        response.setData(allProvinceWayBillParams);
        return response;
    }

    @ApiOperation(value = "今日订单状态分析")
    @GetMapping("/todayWayBillState")
    public Response todayWayBillState() {
        Response response = new Response();
        List<WayBillKeyValueDataVo> wayBillKeyValueDataVos = bigDataService.todayWayBillState();
        response.setData(wayBillKeyValueDataVos);
        return response;
    }

    @ApiOperation(value = "快递类型分析")
    @GetMapping("/wayBillTypeAnalyze")
    public Response wayBillTypeAnalyze() {
        Response response = new Response();
        List<ItemsWayBillParam> itemsWayBillParams = bigDataService.wayBillTypeAnalyze();
        response.setData(itemsWayBillParams);
        return response;
    }

    @ApiOperation(value = "物流信息统计")
    @PostMapping("/getCityByProvince")
    public Response getCityByProvince(@RequestBody WayBillBigDataProvinceParam param) {
        Response response = new Response();
        List<AllProvinceWayBillParam> cityByProvince = bigDataService.getCityByProvince(param.getName());
        response.setData(cityByProvince);
        return response;
    }

    @ApiOperation(value = "物品运单类型")
    @GetMapping("/wayBillItemData")
    public Response wayBillItemData() {
        Response response = new Response();
        List<WayBillKeyValueDataVo> wayBillKeyValueDataVos = bigDataService.wayBillItemData();
        response.setData(wayBillKeyValueDataVos);
        return response;
    }

    @ApiOperation(value = "一周内运单数统计(包括当天)")
    @GetMapping("/getWeekBillStatistics")
    public Response getWeekBillStatistics() {
        Response response = new Response();
        String[] weekBillStatistics = bigDataService.getWeekBillStatistics();
        response.setData(weekBillStatistics);
        return response;
    }

    @ApiOperation(value = "金牌客户发货量排行榜")
    @GetMapping("/getCustomerRankingList")
    public Response getCustomerRankingList() {
        Response response = new Response();
        List<AllProvinceWayBillParam> customerRankingList = bigDataService.getCustomerRankingList();
        response.setData(customerRankingList);
        return response;
    }


    @ApiOperation(value = "运单指标率")
    @GetMapping("/getBillIndexRate")
    public Response getBillIndexRate() {
        List<Map<String, Object>> results = new ArrayList<>();

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "今日运单完成指标率");
        data1.put("value", 90);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "今日问题单占有率");
        data2.put("value", 4);

        results.add(data1);
        results.add(data2);

        return new Response(results);
    }

    @ApiOperation(value = "年度优秀分拣员排行榜")
    @GetMapping("/getWorkerRankingList")
    public Response getWorkerRankingList() {
        List<Map<String, Object>> results = new ArrayList<>();


        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "张工");
        data1.put("value", 7999);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "薛工");
        data2.put("value", 6543);

        Map<String, Object> data3 = new HashMap<>();
        data3.put("name", "陈工");
        data3.put("value", 5550);

        Map<String, Object> data4 = new HashMap<>();
        data4.put("name", "黄工");
        data4.put("value", 5000);

        Map<String, Object> data5 = new HashMap<>();
        data5.put("name", "李工");
        data5.put("value", 3400);

        results.add(data1);
        results.add(data2);
        results.add(data3);
        results.add(data4);
        results.add(data5);

        return new Response(results);
    }

    @ApiOperation(value = "运单收件地区域统计")
    @GetMapping("/getReceiveAddressStatistics")
    public Response getReceiveAddressStatistics() {
        List<Map<String, Object>> results = new ArrayList<>();

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "东北");
        data1.put("value", 70000);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "西北");
        data2.put("value", 59888);

        Map<String, Object> data3 = new HashMap<>();
        data3.put("name", "东部");
        data3.put("value", 49988);

        Map<String, Object> data4 = new HashMap<>();
        data4.put("name", "西部");
        data4.put("value", 47433);

        Map<String, Object> data5 = new HashMap<>();
        data5.put("name", "南部");
        data5.put("value", 45342);

        results.add(data1);
        results.add(data2);
        results.add(data3);
        results.add(data4);
        results.add(data5);

        return new Response(results);
    }


    @ApiOperation(value = "运单物品种类统计图")
    @GetMapping("/getBillProductTypeStatistics")
    public Response getBillProductTypeStatistics() {
        List<Map<String, Object>> results = new ArrayList<>();

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "奢侈品");
        data1.put("num1", 70000);
        data1.put("num2", 70000);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "液体");
        data2.put("num1", 59888);
        data2.put("num2", 59888);

        Map<String, Object> data3 = new HashMap<>();
        data3.put("name", "护肤品");
        data3.put("num1", 49988);
        data3.put("num2", 49988);

        Map<String, Object> data4 = new HashMap<>();
        data4.put("name", "食品");
        data4.put("num1", 47433);
        data4.put("num2", 47433);

        Map<String, Object> data5 = new HashMap<>();
        data5.put("name", "药品");
        data5.put("num1", 45342);
        data5.put("num2", 45342);

        Map<String, Object> data6 = new HashMap<>();
        data6.put("name", "文具");
        data6.put("num1", 45342);
        data6.put("num2", 45342);

        results.add(data1);
        results.add(data2);
        results.add(data3);
        results.add(data4);
        results.add(data5);
        results.add(data6);

        return new Response(results);
    }


}
