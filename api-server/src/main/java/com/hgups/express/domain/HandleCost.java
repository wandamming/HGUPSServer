package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/8/8 0008-14:36
 */
@Data
@TableName(value = "handle_cost")
@ApiModel(value = "后程用户运单操作价格表")
public class HandleCost implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "等级名称")
    private String level;
    @ApiModelProperty(value = "操作价格")
    private double handlePrice;




}
