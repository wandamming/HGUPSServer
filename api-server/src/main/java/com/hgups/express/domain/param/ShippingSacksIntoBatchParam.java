package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/7/18 0018-10:23
 */
@Data
public class ShippingSacksIntoBatchParam {

    @ApiModelProperty(value = "麻袋单号")
    private String sacksNumber;
    @ApiModelProperty(value = "航运批次ID")
    private int shippingBatchId;

}
