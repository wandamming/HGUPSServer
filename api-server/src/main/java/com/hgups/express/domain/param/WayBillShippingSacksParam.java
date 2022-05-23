package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/7/15 0015-14:45
 */
@Data
public class WayBillShippingSacksParam {

    private String trackingNumber;
    private int sacksId;
    private double wareWeight;

}
