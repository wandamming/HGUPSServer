package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/9/27 0027-16:55
 */
@Data
public class WarehouseHandleCostParam {

    @ApiModelProperty(value = "订单操作费")
    private double handleOrderPrice;
}
