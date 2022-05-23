package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/4 0004-15:00
 */
@Data
@TableName(value = "parcel")
@ApiModel(value = "Parcel（包裹）")
public class Parcel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "仓库核重")
    private double wareWeight;
    @ApiModelProperty(value = "打单重量")
    private double billWeight;
    @ApiModelProperty(value = "服务类型")
    private String service;
    @ApiModelProperty(value = "宽")
    private double width;
    @ApiModelProperty(value = "长")
    private double lengths;
    @ApiModelProperty(value = "高")
    private double height;
    @ApiModelProperty(value = "包裹形状")
    private String parcelShape;
    @ApiModelProperty(value = "物品类型")
    private String itmeCategory;
    @ApiModelProperty(value = "物品描述")
    private String aritcleDescribe;
    @ApiModelProperty(value = "面单备注一")
    private String commentOne;
    @ApiModelProperty(value = "面单备注二")
    private String commentTwo;
    @ApiModelProperty(value = "收件人ID")
    private int receivetId;
    @ApiModelProperty(value = "发件人ID")
    private int senderId;
    @ApiModelProperty(value = "是否为长方体")
    private String isCoubid;
    @ApiModelProperty(value = "是否为软包裹")
    private String isSoft;
    @ApiModelProperty(value = "麻袋ID")
    private int sacksId;
    @ApiModelProperty(value = "是否分拣")
    private int isSorting;
    @ApiModelProperty(value = "标签类型")
    private String labelType;
    @ApiModelProperty(value = "批量ID")
    private String moreId;
    @ApiModelProperty(value = "用户ID")
    private long userId;
    @ApiModelProperty(value = "运单ID")
    private int waybillId;

}
