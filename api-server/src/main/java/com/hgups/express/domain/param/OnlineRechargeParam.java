package com.hgups.express.domain.param;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lyx
 * 2021/7/21
 */
@Data
public class OnlineRechargeParam {
    //充值金额
    private BigDecimal amount;
    //付款方式
    private String paymentMethod;
    //备注
    private String remarks;
}
