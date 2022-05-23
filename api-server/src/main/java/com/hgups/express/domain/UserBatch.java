package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanc
 * 2020/7/4 0004-17:48
 */
@Data
@TableName(value = "user_batch")
public class UserBatch  implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "批次名")
    private String name;
    @ApiModelProperty(value = "备注")
    private String comment;
    @ApiModelProperty(value = "麻袋数量")
    private int sacksNumber;
    @ApiModelProperty(value = "运单数量")
    private int waybillNumber;
    @ApiModelProperty(value = "总金额")
    private float totalAmount;
    @ApiModelProperty(value = "批次状态")
    private String state;
    @ApiModelProperty(value = "是否分拣")
    private String isSorting;
    @ApiModelProperty(value = "出口城市")
    private String exportCity;
    @ApiModelProperty(value = "入境地点")
    private String entrySite;
    @ApiModelProperty(value = "批次创建时间")
    private Date createTime;
    @ApiModelProperty(value = "用户ID")
    private long userId;
    @ApiModelProperty(value = "批次单号")
    private String trackingNumber;
    @ApiModelProperty(value = "批次面单base64")
    private String coding;


}
