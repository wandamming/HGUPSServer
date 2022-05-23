package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hgups.express.domain.User;
import com.hgups.express.domain.WayBill;
import com.hgups.express.domain.param.AllProvinceWayBillParam;
import com.hgups.express.domain.param.ItemsWayBillParam;
import com.hgups.express.domain.param.WayBillKeyValueDataVo;
import com.hgups.express.domain.param.WeekBillStatisticsVo;
import com.hgups.express.mapper.BigDataMapper;
import com.hgups.express.service.usermgi.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author fanc
 * 2020/11/25-11:02
 */
@Service
public class BigDataService {

    @Resource
    private BigDataMapper bigDataMapper;
    @Resource
    private WayBillContactService wayBillContactService;
    @Resource
    private UserService userService;
    @Resource
    private WayBillService wayBillService;



    //今日运单统计
    public List<WayBillKeyValueDataVo> todayWayBillData(){
        WayBillKeyValueDataVo todayWayBillDataVo1 = new WayBillKeyValueDataVo();
        Integer integer1 = bigDataMapper.todayWayBillProblemData();
        todayWayBillDataVo1.setName("问题件数");
        todayWayBillDataVo1.setValue(integer1);
        WayBillKeyValueDataVo todayWayBillDataVo2 = new WayBillKeyValueDataVo();
        Integer integer2 = bigDataMapper.todayWayBillSendData();
        todayWayBillDataVo2.setName("已发件数");
        todayWayBillDataVo2.setValue(integer2);
        WayBillKeyValueDataVo todayWayBillDataVo3 = new WayBillKeyValueDataVo();
        Integer integer3 = bigDataMapper.todayWayBillSumData();
        todayWayBillDataVo3.setName("总运单数");
        todayWayBillDataVo3.setValue(integer3);
        WayBillKeyValueDataVo todayWayBillDataVo4 = new WayBillKeyValueDataVo();
        Integer integer4 = bigDataMapper.todayWayBillUnSendData();
        todayWayBillDataVo4.setName("待发件数");
        todayWayBillDataVo4.setValue(integer4);
        List<WayBillKeyValueDataVo> todayWayBillDataVoList = new ArrayList<>();
        todayWayBillDataVoList.add(todayWayBillDataVo1);
        todayWayBillDataVoList.add(todayWayBillDataVo2);
        todayWayBillDataVoList.add(todayWayBillDataVo3);
        todayWayBillDataVoList.add(todayWayBillDataVo4);
        return todayWayBillDataVoList;
    }

    //入境口岸运单数量
    public List<AllProvinceWayBillParam> getPortEntryWaybill(){
        return bigDataMapper.getPortEntryWaybill();
    }


    //运单整体统计
    public List<WayBillKeyValueDataVo> getTotalBillPieChart(){
        WayBillKeyValueDataVo todayWayBillDataVo1 = new WayBillKeyValueDataVo();
        todayWayBillDataVo1.setName("今年累计运单数");
        Integer integer1 = bigDataMapper.yearWayBillData();
        todayWayBillDataVo1.setValue(integer1);
        WayBillKeyValueDataVo todayWayBillDataVo2 = new WayBillKeyValueDataVo();
        todayWayBillDataVo2.setName("本月累计运单数");
        Integer integer2 = bigDataMapper.monthWayBillData();
        todayWayBillDataVo2.setValue(integer2);
        WayBillKeyValueDataVo todayWayBillDataVo3 = new WayBillKeyValueDataVo();
        todayWayBillDataVo3.setName("今日累计运单数");
        Integer integer3 = bigDataMapper.todayWayBillSumData();
        todayWayBillDataVo3.setValue(integer3);
        WayBillKeyValueDataVo todayWayBillDataVo4 = new WayBillKeyValueDataVo();
        todayWayBillDataVo4.setName("今年累计问题单数");
        Integer integer4 = bigDataMapper.yearWayBillProblemData();
        todayWayBillDataVo4.setValue(integer4);
        WayBillKeyValueDataVo todayWayBillDataVo5 = new WayBillKeyValueDataVo();
        todayWayBillDataVo5.setName("本月累计问题单数");
        Integer integer5 = bigDataMapper.monthWayBillProblemData();
        todayWayBillDataVo5.setValue(integer5);
        WayBillKeyValueDataVo todayWayBillDataVo6 = new WayBillKeyValueDataVo();
        todayWayBillDataVo6.setName("今日累计问题单数");
        Integer integer6 = bigDataMapper.todayWayBillProblemData();
        todayWayBillDataVo6.setValue(integer6);

        List<WayBillKeyValueDataVo> wayBillDataVoList = new ArrayList<>();
        wayBillDataVoList.add(todayWayBillDataVo1);
        wayBillDataVoList.add(todayWayBillDataVo2);
        wayBillDataVoList.add(todayWayBillDataVo3);
        wayBillDataVoList.add(todayWayBillDataVo4);
        wayBillDataVoList.add(todayWayBillDataVo5);
        wayBillDataVoList.add(todayWayBillDataVo6);
        return wayBillDataVoList;
    }

    //快递排行榜
    public List<AllProvinceWayBillParam> wayBillRanking(String proName){
        return wayBillContactService.provinceGetCityWayBill(proName);
    }

    //今日订单状态分析
    public List<WayBillKeyValueDataVo> todayWayBillState(){
        WayBillKeyValueDataVo todayWayBillDataVo1 = new WayBillKeyValueDataVo();
        todayWayBillDataVo1.setName("已签收");
        Integer integer1 = bigDataMapper.todayWayBillSignData();
        todayWayBillDataVo1.setValue(integer1);

        WayBillKeyValueDataVo todayWayBillDataVo2 = new WayBillKeyValueDataVo();
        todayWayBillDataVo2.setName("待签收");
        Integer integer2 = bigDataMapper.todayWayBillAwaitSignData();
        todayWayBillDataVo2.setValue(integer2);

        WayBillKeyValueDataVo todayWayBillDataVo3 = new WayBillKeyValueDataVo();
        todayWayBillDataVo3.setName("已退货");
        Integer integer3 = bigDataMapper.todayWayBillSalesReturnData();
        todayWayBillDataVo3.setValue(integer3);

        WayBillKeyValueDataVo todayWayBillDataVo4 = new WayBillKeyValueDataVo();
        todayWayBillDataVo4.setName("已创建");
        Integer integer4 = bigDataMapper.todayWayBillSumData();
        todayWayBillDataVo4.setValue(integer4);

        WayBillKeyValueDataVo todayWayBillDataVo5 = new WayBillKeyValueDataVo();
        todayWayBillDataVo5.setName("问题单");
        Integer integer5 = bigDataMapper.todayWayBillProblemData();
        todayWayBillDataVo5.setValue(integer5);

        List<WayBillKeyValueDataVo> wayBillDataVoList = new ArrayList<>();
        wayBillDataVoList.add(todayWayBillDataVo1);
        wayBillDataVoList.add(todayWayBillDataVo2);
        wayBillDataVoList.add(todayWayBillDataVo3);
        wayBillDataVoList.add(todayWayBillDataVo4);
        wayBillDataVoList.add(todayWayBillDataVo5);
        return wayBillDataVoList;
    }

    //快递类型分析
    public List<ItemsWayBillParam> wayBillTypeAnalyze(){
        return wayBillContactService.itemsWayBill();
    }

    //物流信息统计
    public List<AllProvinceWayBillParam> getCityByProvince(String provinceName){
        return wayBillContactService.provinceGetCityWayBill(provinceName);
    }

    //物流信息统计
    public List<WayBillKeyValueDataVo> wayBillItemData(){
        return wayBillContactService.wayBillItemData();
    }
    //一周内运单数统计
    public String[] getWeekBillStatistics(){
        String[] week = new String[]{"0","0","0","0","0","0","0"};
        List<WeekBillStatisticsVo> weekBillStatistics = bigDataMapper.getWeekBillStatistics();
        for (WeekBillStatisticsVo weekBillStatistic : weekBillStatistics) {
            Date time = weekBillStatistic.getTime();
            long beforeDay = getBeforeDay(time);
            week[(int)beforeDay]=String.valueOf(weekBillStatistic.getValue());
        }
        for (int i = 0; i < week.length/2; i++) {
            //定义中间变量实现互换
            String temp = week[i];
            week[i] = week[week.length - i - 1];
            week[week.length - i - 1] = temp;
        }
            return week;
    }


    //传入时间返回几天前
    public long getBeforeDay(Date time){
        //获取当前实际按戳
        long toDay = System.currentTimeMillis();
        //运单时间
        long time1 = time.getTime();
        //距离当前天数
        long storageTime = (toDay - time1) / (3600 * 24 * 1000);
        return storageTime;
    }

    //金牌客户发货量排行榜
    public List<AllProvinceWayBillParam> getCustomerRankingList(){
        List<AllProvinceWayBillParam> allProvinceWayBillParams = new ArrayList<>();
        List<User> users = userService.selectList(null);
        for (User user : users) {
            AllProvinceWayBillParam allProvinceWayBillParam = new AllProvinceWayBillParam();
            String username = user.getUsername();
            Long id =  user.getId();
            EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
            wrapper.eq("user_id",id);
            wrapper.eq("new_way_bill_id",-1);
            int count = wayBillService.selectCount(wrapper);
            allProvinceWayBillParam.setName(username);
            allProvinceWayBillParam.setValue(count);
            allProvinceWayBillParams.add(allProvinceWayBillParam);
        }
        Collections.sort(allProvinceWayBillParams);
        return allProvinceWayBillParams;
    }

}
