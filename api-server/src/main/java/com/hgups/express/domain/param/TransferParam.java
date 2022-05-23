package com.hgups.express.domain.param;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lyx
 * 2021/7/21
 */
@Data
public class TransferParam {
    //汇款银行
    private String transferBank;
    //汇款金额
    private BigDecimal transferAmount;
    //汇款时间
    private Date transfer;
    //汇款说明（备注）
    private String remarks;

}
