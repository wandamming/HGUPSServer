package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/9/25 0025-15:59
 */

@Data
@TableName(value = "warehouse_material_cost")
@ApiModel(value = "海外仓包装材料费用表")
public class WarehouseMaterialCost implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Integer id;
    @ApiModelProperty(value = "包装材料类型")
    private String packName;
    @ApiModelProperty(value = "包装费用（件）")
    private double packPrice;
    @ApiModelProperty(value = "备注")
    private String comment;


}
