package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/11/6-21:42
 */
@Data
public class PointScanSacksWayBillVo {

    private String trackingNumber;
    @ApiModelProperty(value = "仓库核重")
    private double wareWeight;
    @ApiModelProperty(value = "仓库核重价格")
    private double warePrice;

}
