package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanc
 * 2020/6/8 0008-17:54
 */
@Data
@TableName(value = "waybill")
@ApiModel(value = "Waybill（运单表）")
public class WayBill implements Serializable  {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "渠道")
    private String channel="hgups";
    @ApiModelProperty(value = "追踪号码")
    private String trackingNumber;
    @ApiModelProperty(value = "状态")
    private int state;
    @ApiModelProperty(value = "用户ID")
    private long userId;
    @ApiModelProperty(value = "服务类型")
    private String service;
    @ApiModelProperty(value = "出口城市")
    private String exportCity;
    @ApiModelProperty(value = "入境地点")
    private String entrySite;
    @ApiModelProperty(value = "价格")
    private double price;
    @ApiModelProperty(value = "发件人ID")
    private int senderId;
    @ApiModelProperty(value = "收件人ID")
    private int receiveId;
    @ApiModelProperty(value = "区域")
    private String zone;
    @ApiModelProperty(value = "备注一")
    private String commentOne;
    @ApiModelProperty(value = "备注二")
    private String commentTwo;
    @ApiModelProperty(value = "服务点名称")
    private String servicePointName;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "批量ID")
    private String moreId;
    @ApiModelProperty(value = "打单重量")
    private double billWeight;
    @ApiModelProperty(value = "仓库核重")
    private double wareWeight;
    @ApiModelProperty(value = "仓库核重时间")
    private Date wareWeightTime;
    @ApiModelProperty(value = "航空批次ID")
    private int shippingBatchId;
    @ApiModelProperty(value = "用户批次ID")
    private int userBatchId;
    @ApiModelProperty(value = "面单编码")
    private String coding;
    @ApiModelProperty(value = "面单是否被更改")
    private int isCoding;
    @ApiModelProperty(value = "发件人姓名")
    private String senderName;
    @ApiModelProperty(value = "收件人姓名")
    private String receiveName;
    @ApiModelProperty(value = "用户麻袋ID")
    private int userSacksId;
    @ApiModelProperty(value = "航运麻袋ID")
    private int shippingSacksId;
    @ApiModelProperty(value = "入境口岸ID")
    private int portId;
    @ApiModelProperty(value = "收件人地址的运送路由")
    private String carrierRoute;
    @ApiModelProperty(value = "发货点")
    private String deliveryPoint;
    @ApiModelProperty(value = "是否拦截")//0:未拦截 1:已拦截
    private int isIntercept;
    @ApiModelProperty(value = "是否是问题包裹")
    //0:不是问题包裹  1：打单重量与仓库核重不匹配 2：包裹类型不匹配 3：入境口岸不匹配
    private int isProblemParcel;
    @ApiModelProperty(value = "仓库核重价格")
    private double warePrice;
    @ApiModelProperty(value = "拦截时间")
    private Date interceptTime;

    @ApiModelProperty(value = "预上线状态")
    private String spEventState;
    @ApiModelProperty(value = "是否进行收费的标志")
    @TableField("has_ssf")
    private boolean hasSSF;

    @ApiModelProperty(value = "新单号")
    private String newTrackingNumber;

    @ApiModelProperty(value = "换单时间")
    private Date changeSingleTime;

    @ApiModelProperty(value = "旧单ID")
    private Integer newWayBillId;

    @ApiModelProperty(value = "问题单是否已处理：1未处理，2已处理")
    private Integer isProblemSolve;

    @ApiModelProperty(value = "用户运单价格，美元")
    private double userWaybillPrice;

    @ApiModelProperty(value = "运单追踪（已发货后状态）")
    private String wayBillTrace;
    //DHL面单地址
    private String dhlCodingUrl;
    //packageID
    private String dhlPackageId;

}
