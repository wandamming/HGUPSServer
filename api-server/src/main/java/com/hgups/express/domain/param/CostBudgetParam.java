package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/6/20 0020-19:10
 */
@Data
public class CostBudgetParam {

    private int id;
    @ApiModelProperty(value = "克数")
    private float weight;
    @ApiModelProperty(value = "中国出口报关费")
    private float chainPrice;
    @ApiModelProperty(value = "航空费")
    private float aviationPrice;
    @ApiModelProperty(value = "美后派送费")
    private float americaSendPrice;
    @ApiModelProperty(value = "美国清关机场费")
    private float americaPrice;
    @ApiModelProperty(value = "全程总价")
    private float sumPrice;

}
