package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/13 0013-15:08
 */
@Data
@TableName(value = "cost_budget")
@ApiModel(value = "价格表")
public class CostBudget implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "克数")
    private float weight;
    @ApiModelProperty(value = "美后派送费")
    private float americaSendPrice;
    @ApiModelProperty(value = "美国清关机场费")
    private float americaPrice;


}
