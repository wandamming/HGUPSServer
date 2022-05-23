package com.hgups.express.mapper;

import com.hgups.express.domain.param.AllProvinceWayBillParam;
import com.hgups.express.domain.param.WeekBillStatisticsVo;

import java.util.List;

/**
 * @author fanc
 * 2020/11/25-11:04
 */
public interface BigDataMapper {

    //今日总运单数
    Integer todayWayBillSumData();
    //今日问题件数
    Integer todayWayBillProblemData();
    //今日运单统计----已发件数
    Integer todayWayBillSendData();
    //今日运单统计----待发件数
    Integer todayWayBillUnSendData();
    //入境口岸运单数量
    List<AllProvinceWayBillParam> getPortEntryWaybill();
    //运单整体统计----今年累计运单数
    Integer yearWayBillData();
    //运单整体统计----本月累计运单数
    Integer monthWayBillData();
    //运单整体统计----今年累计问题单数
    Integer yearWayBillProblemData();
    //运单整体统计----本月累计问题单数
    Integer monthWayBillProblemData();
    //今日订单状态分析----已签收
    Integer todayWayBillSignData();
    //今日订单状态分析----待签收
    Integer todayWayBillAwaitSignData();
    //今日订单状态分析----已退货
    Integer todayWayBillSalesReturnData();

    //一周内运单数统计
    List<WeekBillStatisticsVo> getWeekBillStatistics();
}
