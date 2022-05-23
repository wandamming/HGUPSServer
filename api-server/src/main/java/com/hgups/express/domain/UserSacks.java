package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanc
 * 2020/7/4 0004-15:46
 */
@Data
@TableName(value = "user_sacks")
public class UserSacks implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "用户批次ID")
    private int userBatchId;
    @ApiModelProperty(value = "包裹数量")
    private int parcelNumber;
    @ApiModelProperty(value = "麻袋状态")
    private String state;
    @ApiModelProperty(value = "麻袋打单重量")
    private double billWeight;
    @ApiModelProperty(value = "麻袋核重重量")
    private double wareWeight;
    @ApiModelProperty(value = "麻袋服务")
    private String service;
    @ApiModelProperty(value = "入境口岸")
    private String entrySite;
    @ApiModelProperty(value = "客户")
    private String customer;
    @ApiModelProperty(value = "创建人")
    private String creator;
    @ApiModelProperty(value = "麻袋面单编码")
    private String coding;
    @ApiModelProperty(value = "麻袋备注")
    private String comment;
    @ApiModelProperty(value = "用户ID")
    private long userId;
    @ApiModelProperty(value = "麻袋运单总金额")
    private float sumPrice;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "麻袋电子单号")
    private String sacksNumber;



}
