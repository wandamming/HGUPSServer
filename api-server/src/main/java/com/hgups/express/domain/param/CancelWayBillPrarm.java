package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/7/2 0002-15:47
 */
@Data
public class  CancelWayBillPrarm{
    @ApiModelProperty("运单ID")
    private int wayBillId;
/*    @ApiModelProperty("订单号")
    private String trackingNumber;*/
}
