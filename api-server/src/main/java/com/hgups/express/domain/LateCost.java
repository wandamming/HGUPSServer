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
@TableName(value = "late_cost")
@ApiModel(value = "后程用户运单价格表")
public class LateCost implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "重量")
    private double weight;
    @ApiModelProperty(value = "区域一")
    private double zoneOne;
    @ApiModelProperty(value = "区域二")
    private double zoneTwo;
    @ApiModelProperty(value = "区域三")
    private double zoneThree;
    @ApiModelProperty(value = "区域四")
    private double zoneFour;
    @ApiModelProperty(value = "区域五")
    private double zoneFive;
    @ApiModelProperty(value = "区域六")
    private double zoneSix;
    @ApiModelProperty(value = "区域七")
    private double zoneSeven;
    @ApiModelProperty(value = "区域八")
    private double zoneEight;
    @ApiModelProperty(value = "区域九")
    private double zoneNine;


}
