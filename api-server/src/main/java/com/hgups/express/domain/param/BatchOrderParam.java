package com.hgups.express.domain.param;

import lombok.Data;

import java.util.Date;

/**
 * @author wandaming
 * 2021/7/29-13:09
 */
@Data
public class BatchOrderParam {

    private int nid;

    private String platformOrderNum ;
    private String platform ;
    private String storeName ;
    private String commerceOrderNum ;
    private String customerName ;
    private String telephone ;
    private String orderStateValue ;
    private String orderTime ;


    //发件人
    private String sender;
    private String senderTelephone;
    private String deliverCity;
    private String specificAddress;
    private String postalCode;

    //渠道路线
    private String channel;
    private String deliverRoute;


}
