package com.hgups.express.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author wandaming
 * 2021/7/23-17:18
 */
@Data
public class OrderDetailsVo {
    private String orderState;
    private String platformOrderNum;
    private String commerceOrderNum;
    private Date orderTime;
    private String platform;
    private String sender;
    private String telephone;
    private String deliverCity;
    private String specificAddress;
    private String postalCode;
    private String channel;
    private String deliverRoute;
}
