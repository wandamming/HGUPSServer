package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/10 0010-13:55
 */
@Data
@ApiModel(value = "包裹形状表")
@TableName(value = "parcel_shape")
public class ParcelShape implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "包裹形状名称")
    private String parcelShapeName;
}
