package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/7/8 0008-11:06
 */
@Data
public class ShippingSacksParam {
    private int id;
    @ApiModelProperty(value = "航运批次ID")
    private int shippingBatchId;
    @ApiModelProperty(value = "麻袋服务")
    private String service;
    @ApiModelProperty(value = "麻袋备注")
    private String comment;
    @ApiModelProperty(value = "麻袋入境口岸")
    private String entrySite;
    //渠道
    private String channel;
}
