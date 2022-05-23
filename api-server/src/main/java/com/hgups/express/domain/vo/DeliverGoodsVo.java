package com.hgups.express.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2021/7/23-16:35
 */
@Data
public class DeliverGoodsVo {
    private String sku;
    private String goodsName;
    private String receivingCity;
    private Integer status;
    private String deliveryCity;
    private String channel;
    private String deliveryRoute;
    private Integer successOrderNum;
    private Integer failOrderNum;
    private Integer totalWeight;
    private Integer totalCost;
    private List<SuccessOrderVo> successOrder;
    private List<FailOrderVo> failOrders;

}



