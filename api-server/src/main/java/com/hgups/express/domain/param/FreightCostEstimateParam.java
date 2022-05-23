package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/7/2 0002-21:42
 */
@Data
public class FreightCostEstimateParam {

    private double billWeight;
    private double lengths;
    private double height;
    private double width;
    private String senderAddressTwo;
    private String senderPostalCode;
    private String receiveAddressTwo;
    private String receivePostalCode;


}
