package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/9/25 0025-16:02
 */
@Data
@TableName(value = "warehouse_other_cost")
@ApiModel(value = "海外仓操作费用及日租费用表")
public class WarehouseOtherCost implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Integer id;
    @ApiModelProperty(value = "订单操作费")
    private double handleOrderPrice;
    @ApiModelProperty(value = "日租免费天数")
    private Integer freeDay;
    @ApiModelProperty(value = "日租费用")
    private double dayPrice;


}
