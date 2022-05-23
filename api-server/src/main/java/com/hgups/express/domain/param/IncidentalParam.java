package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/9/23 0023-10:33
 */
@Data
public class IncidentalParam {

    @ApiModelProperty(value = "清关报关费用")
    private double customsPrice;
    @ApiModelProperty(value = "预留费用一")
    private double reservedOne;
    @ApiModelProperty(value = "预留费用二")
    private double reservedTwo;
    @ApiModelProperty(value = "预留费用三")
    private double reservedThree;

}
