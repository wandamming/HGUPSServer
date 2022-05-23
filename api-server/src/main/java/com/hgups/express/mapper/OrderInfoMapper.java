package com.hgups.express.mapper;

import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hgups.express.domain.OrderInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.param.*;
import com.hgups.express.domain.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wdm
 * @since 2021-07-26
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    List<OrderInfoVo> getOrderInfo(Pagination pagination, OrderParam param);

    Integer getOrderInfoCount(OrderParam param);

    OrderDetailsVo getOrderDetails(IdParam param);

    void deleteBatchOrder(IdsParam id);

    //导出订单Excel
    List<ExportOrderVo> exportOrderExcel(Map map);

    //费用确认

    List<OrderCostVo> getOrderCost(OrderCostParam param);
    Integer getOrderCostCount(OrderCostParam param);



}
