package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/9/27 0027-16:55
 */
@Data
public class WarehouseRendCostParam {

    @ApiModelProperty(value = "日租免费天数")
    private Integer freeDay;
    @ApiModelProperty(value = "日租费用")
    private double dayPrice;
}
