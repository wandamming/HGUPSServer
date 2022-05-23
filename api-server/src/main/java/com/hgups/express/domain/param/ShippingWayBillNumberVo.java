package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/10/7 0007-23:36
 */
@Data
public class ShippingWayBillNumberVo {

    private int id;
    @ApiModelProperty(value = "追踪号码")
    private String trackingNumber;
    private int newWayBillId;



}
