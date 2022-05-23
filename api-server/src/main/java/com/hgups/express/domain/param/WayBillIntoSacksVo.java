package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/8/19 0019-16:38
 */
@Data
public class WayBillIntoSacksVo {

    private int id;
    @ApiModelProperty(value = "追踪号码")
    private String trackingNumber;
    @ApiModelProperty(value = "打单重量")
    private double billWeight;
    @ApiModelProperty(value = "仓库核重")
    private double wareWeight;
    @ApiModelProperty(value = "价格")
    private double price;
    @ApiModelProperty(value = "仓库核重价格")
    private double warePrice;
    @ApiModelProperty(value = "渠道")
    private String channel;
}
