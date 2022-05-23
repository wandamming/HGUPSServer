package com.hgups.express.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author wandaming
 * 2021/7/23-17:18
 */
@Data
public class OrderCostVo {

//    private String sku;
//    private String goodsName;
//    private String receivingCity;

    private Integer totalCost;
    private String customerAccount;
    private List<DeliverCostVo> deliverCostVo;
}

