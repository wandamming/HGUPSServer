package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.WayBill;
import com.hgups.express.domain.param.*;
import com.hgups.express.vo.WayBillVo;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import sun.util.resources.ga.LocaleNames_ga;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * @author fanc
 * 2020/6/9 0009-10:31
 */

public interface WayBillMapper extends BaseMapper<WayBill> {

    List<ShippingWayBillNumberVo> getShippingWayBillNumber(@Param("sid")Integer sid);

    WayBillVo getWayBillDetails(@Param("wid")Integer wid);

    Float getSumPrice(Map map);

    Float getShippingSumPrice(Map map);

   List<WayBillVo> getWayBillDetailsList(Map map);

   //航运批次运单查询
   List<ShippingWayBillListParam> allWayBill(ShippingWayBillParam param);
    //航运批次运单查询运单总数
   Integer countWayBill(ShippingWayBillParam param);

   List<WayBillVo> getWayBillDetailsByBatchId(@Param("batchId")Integer batchId);

   Integer updateSSF(@Param("ids") List<Integer> ids, @Param("ssf") boolean ssf);

   Integer updateSpEventState(@Param("ids") List<Integer> ids, @Param("state") String state);

   List<WayBill> getProblemWayBill(ProblemWayBillParam param);

   Integer getProblemWayBillCount(ProblemWayBillParam param);

    List<WayBillAndUserParam> getChangeSingle(ChangeSingleParam param);

    Integer getChangeSingleCount(ChangeSingleParam param);

    WayBillVo getChangeSingleInfo(@Param("wid") Integer wid);

    List<String> getTimingModifyTrackNumbers();

    //根据物品类型获取运单核重价格总和
    Double getCateGoryWarePrice(@Param("wIds") List<Integer> wIds);
    //根据物品类型获取运单核重重量总和
    Double getCateGoryWareWeight(@Param("wIds") List<Integer> wIds);

    //根据批次ID运单核重重量总和
    Double getWareWeightByBatchId(@Param("trackNumber") String trackNumber);

    //根据运单ID获取运单编码Coding
    List<String> getWayBillCoding(Map map);

    List<String> getOldTrackingNumber(Map map);

    List<Integer> getIdByTrackingNumber(Map map);

    //获取航运批次跟换过面单的运单列表
    List<WayBill> selectTrackingList();

    String getCodingById(@Param("wid")Integer id);


    //获取用户运单信息
    List<WayBill> getUserWayBillList(Map map);
    //获取用户运单信息总数
    Integer getUserWayBillListCount(Map map);


    //获取未加入用户批次运单信息列表
    List<UserBatchWayBill> getNotIntoBatchWayBillList(@Param("userId")Long loginUserId);

    //获取未加入用户批次运单信息列表总数
    Integer getNotIntoBatchWayBillListCount(@Param("userId")Long loginUserId);

    //获取未加入航运批次运单信息
    List<WayBill> getShippingBatchWayBillList();

    //获取未加入航运批次运单总数
    Integer getShippingBatchWayBillListCount();

    //获取未加入麻袋的运单
    List<WayBill> getNotJoinWayBill();
    //获取未加入麻袋的运单总数
    Integer getNotJoinWayBillCount();
    /*
     * author lyx
     */
    Integer getAllWayBills(@Param(value = "userId")Long id);
    Integer getPendingWaybills(@Param(value = "userId")Long id);
    Integer getShippedWaybills(@Param(value = "userId")Long id);
    Integer getSignedWaybills(@Param(value = "userId")Long id);
    Integer getProblemWaybills(@Param(value = "userId")Long id);
    BigDecimal getForecastPrice(@Param(value = "userId")Long id);
    BigDecimal getWarePrice(@Param(value = "userId")Long id);
}
