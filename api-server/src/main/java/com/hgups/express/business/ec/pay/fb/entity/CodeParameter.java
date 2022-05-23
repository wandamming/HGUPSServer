package com.hgups.express.business.ec.pay.fb.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CodeParameter implements Serializable {

    /**
     * store_id : 95002525
     * merchant_order_sn : dcvnly6cpm0v2hm4y55x9i4cgyve3e
     * total_amount : 0.01
     * merchant_id : 3204325
     * auth_code : 283599641632104885
     */

    private int store_id;
    private String merchant_order_sn;
    private double total_amount;
    private int merchant_id;
    private String auth_code;
}
