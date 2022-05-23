package com.hgups.express.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lyx
 * 2021/7/21
 */
@Data
public class DataModuleVo {

    //总运单数
    private int allWaybills;
    //待发运单数
    private int pendingWaybills;
    //已发运单数
    private int shippedWaybills;
    //已签收运单数
    private int signedWaybills;
    //问题单数
    private int problemWaybills;
    //预报金额
    private BigDecimal forecastPrice;
    //核重价格
    private BigDecimal warePrice;

}
