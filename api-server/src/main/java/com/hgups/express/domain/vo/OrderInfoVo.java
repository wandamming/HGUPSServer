package com.hgups.express.domain.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author wandaming
 * 2021/7/21-18:08
 */
@Data
public class OrderInfoVo {

    private Integer Id ;
    private String platformOrderNum ;
    private String platform ;
    private String storeName ;
    private String commerceOrderNum ;
    private String customerName ;
    private String telephone ;
    private String orderStateValue ;
    private Date orderTime ;
}