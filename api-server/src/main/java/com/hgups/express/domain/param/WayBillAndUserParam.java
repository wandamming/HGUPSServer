package com.hgups.express.domain.param;

import lombok.Data;

import java.util.Date;

/**
 * @author fanc
 * 2020/7/31 0031-20:00
 */
@Data
public class WayBillAndUserParam {
    private int id;

    private String username;

    private String newTrackingNumber;

    private String trackingNumber;

    private String service;

    private double wareWeight;

    private Date changeSingleTime;

    private Integer state;
}
