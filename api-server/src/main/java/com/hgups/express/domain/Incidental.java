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
@TableName(value = "incidental")
@ApiModel(value = "后程用户运单杂费价格表")
public class Incidental implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "清关报关费用")
    private double customsPrice;
    @ApiModelProperty(value = "预留费用一")
    private double reservedOne;
    @ApiModelProperty(value = "预留费用二")
    private double reservedTwo;
    @ApiModelProperty(value = "预留费用三")
    private double reservedThree;



}
