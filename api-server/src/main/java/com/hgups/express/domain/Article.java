package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/4 0004-15:08
 */
@Data
@TableName(value = "article")
@ApiModel(value = "Article（物品表）")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "物品描述（中文）")
    private String cDescribe;
    @ApiModelProperty(value = "物品描述（英文）")
    private String eDescribe;
    @ApiModelProperty(value = "价格")
    private double price;
    @ApiModelProperty(value = "重量")
    private double weight;
    @ApiModelProperty(value = "数量")
    private int number;
    @ApiModelProperty(value = "产地")
    private String place;
    @ApiModelProperty(value = "HS编码")
    private String hsEncode;
    @ApiModelProperty(value = "HTS编码")
    private String htsEncode;
    @ApiModelProperty(value = "申报要素")
    private String declaration;
    @ApiModelProperty(value = "包裹ID")
    private int parcleId;
    @ApiModelProperty(value = "物品类型")
    private String articleType;
    @ApiModelProperty(value = "运单ID")
    private int waybillId;

}
