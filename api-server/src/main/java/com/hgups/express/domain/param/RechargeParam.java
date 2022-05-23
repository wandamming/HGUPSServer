package com.hgups.express.domain.param;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/13 0013-18:23
 */
@Data
public class RechargeParam implements Serializable {

    //充值用户id
    private int userId;
    //充值金额
    private double rechargeAmount;

}
