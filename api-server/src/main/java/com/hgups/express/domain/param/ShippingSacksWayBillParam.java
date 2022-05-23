package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/7/17 0017-16:06
 */
@Data
public class ShippingSacksWayBillParam {

    private int sacksId;
    private List wayBillNumbersInto;
    private List wayBillNumbersOut;
    private double closeWeight;

}
