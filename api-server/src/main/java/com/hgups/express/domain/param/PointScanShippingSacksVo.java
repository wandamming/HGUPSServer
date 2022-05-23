package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/11/6-21:21
 */
@Data
public class PointScanShippingSacksVo {

    @ApiModelProperty(value = "麻袋追踪号码")
    private String sacksNumber;
    @ApiModelProperty(value = "包裹数量")
    private int parcelNumber;
    @ApiModelProperty(value = "麻袋核重重量")
    private double wareWeight;

}
