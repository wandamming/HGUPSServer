package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanc
 * 2020/7/7 0007-16:30
 */
@Data
public class ShippingBatch implements Serializable {

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
    private int parcelNumber;
    @ApiModelProperty(value = "打单总金额")
    private double totalAmount;
    @ApiModelProperty(value = "核重总金额")
    private double warePrice;
    @ApiModelProperty(value = "批次状态")
    private String state;
    @ApiModelProperty(value = "入境地点")
    private String entrySite;
    @ApiModelProperty(value = "航班号")
    private String flightNo;
    @ApiModelProperty(value = "出发时间")
    private Date beginTime;
    @ApiModelProperty(value = "达到时间")
    private Date endTime;
    @ApiModelProperty(value = "MAWB（航空主运单号）")
    private String mawb;
    @ApiModelProperty(value = "批次创建时间")
    private Date createTime;
    @ApiModelProperty(value = "用户ID")
    private long userId;
    @ApiModelProperty(value = "批次单号")
    private String trackingNumber;
    @ApiModelProperty(value = "批次面单base64")
    private String coding;

    @ApiModelProperty(value = "预上线状态")
    private String spEventState;
    @ApiModelProperty(value = "是否进行收费的标志")
    @TableField("has_ssf")
    private boolean hasSSF;
    @ApiModelProperty(value = "后程、管理员用户创建")
    private Integer isProcess;
    @ApiModelProperty(value = "后程附件文件地址")
    private String attachmentPath;

    @ApiModelProperty(value = "客户名")
    private String username;
    @ApiModelProperty(value = "清关时间")
    private Date clearanceTime;
    @ApiModelProperty(value = "报关时间")
    private Date customsTime;
    @ApiModelProperty(value = "后程用户是否发起申请预上线")//0：否 1：是
    private Integer isApplyEvent;
    //渠道
    private String channel;
    //DHL面单地址
    private String dhlCodingUrl;
    //DHL位置
    private String dhlLocation;
    //DHL描述
    private String dhlDesc;
    //DHL省/州
    private String dhlProvince;

}
