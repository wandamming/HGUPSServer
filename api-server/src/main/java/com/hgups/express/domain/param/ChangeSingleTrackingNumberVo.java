package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/8/1 0001-17:57
 */
@Data
public class ChangeSingleTrackingNumberVo {

    private String trackingNumber;
    private String newTrackingNumber;
    private String service;
    private double wareWeight;
    private int state;
    private String entrySite;
    private String isCoding;
    private int id;
    private String coding;//跟换过单号的运单：1 没跟换过：0
    private String flag;//前台传入的是客户使用的单号：0  跟换之后的单号是：1

}
