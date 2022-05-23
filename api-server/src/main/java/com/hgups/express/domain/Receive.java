package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/4 0004-14:32
 */

@Data
@TableName(value = "receive")
@ApiModel(value = "Receive（收件人）")
public class Receive implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @TableField("name")
    @ApiModelProperty(value = "姓名")
    private String name;
    @TableField("company")
    @ApiModelProperty(value = "公司")
    private String company;
    @TableField("countries")
    @ApiModelProperty(value = "国家")
    private String countries;
    @ApiModelProperty(value = "省份(英)")
    private String provinceEname;
    @ApiModelProperty(value = "省份（中）")
    private String provinceCname;
    @ApiModelProperty(value = "城市(英)")
    private String cityEname;
    @ApiModelProperty(value = "城市(中)")
    private String cityCname;
    @TableField("prefecture")
    @ApiModelProperty(value = "县")
    private String prefecture;
    @TableField("postal_code")
    @ApiModelProperty(value = "邮政编码")
    private String postalCode;
    @TableField("address_one")
    @ApiModelProperty(value = "地址一")
    private String addressOne;
    @TableField("address_two")
    @ApiModelProperty(value = "地址二")
    private String addressTwo;
    @TableField("phone")
    @ApiModelProperty(value = "电话")
    private String phone;
    @ApiModelProperty(value = "电话前缀")
    private String phonePrefix;
    @TableField("email")
    @ApiModelProperty(value = "邮箱")
    private String email;
    @TableField("user_id")
    @ApiModelProperty(value = "用户ID")
    private long userId;
    @ApiModelProperty(value = "是否保存到地址簿")
    private String isSave;
    @ApiModelProperty(value = "邮政编码后4位")
    private String postalCodet;
    @ApiModelProperty(value = "航空公司路线")
    private String receiveCarrierRoute;
    @ApiModelProperty(value = "交货地点")
    private String receiveDeliveryPoint;


}
