package com.hgups.express.business.ec.pay.fb.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayParameter implements Serializable {

    /**
     * biz_content : {"store_id":95002525,"merchant_order_sn":"dcvnly6cpm0v2hm4y55x9i4cgyve3e","total_amount":0.01,"merchant_id":3204325,"auth_code":"283599641632104885"}
     * method : fbpay.order.pay
     * vendor_sn : 2017030911401982254a
     * format : json
     * sign_method : md5
     * sign : 34F77F211F5E65B666B22B6CADA3EE02
     * nonce : x68k5sz122z2
     * version : 1.0
     */

    private String biz_content;
    private String method;
    private String vendor_sn;
    private String format;
    private String sign_method;
    private String sign;
    private String nonce;
    private String version;
    private String app_id;
}
