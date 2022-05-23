package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/7/20 0020-14:34
 */
@Data
public class ShippingWayBillParam{


    //1:需要根据运单号查询，其他：不需要..
    private String isTrackingNumber;
    @ApiModelProperty(value = "追踪号码")
    private List<String> trackingNumbers;

    //1:需要根据航运麻袋ID查询，其他：不需要..
    private String isShippingSacksNumber;
    @ApiModelProperty(value = "麻袋单号列表")
    private String[] shippingSacksNumbers;

    private String shippingSacksNumberString;

    private String channel;//渠道

    @ApiModelProperty(value = "服务类型")
    private String service;
    @ApiModelProperty(value = "入境地点")
    private String entrySite;
    @ApiModelProperty(value = "收件人信息(姓名、地址二)")
    private String receiveInfo;
    @ApiModelProperty(value = "面单备注(一、二)")
    private String singleComment;

    @ApiModelProperty(value = "用户ID")
    private long userId;
    @ApiModelProperty(value = "航空批次单号")
    private String shippingBatchNumber;
    @ApiModelProperty(value = "预报批次ID")
    private Integer forecastBatchId;

    @ApiModelProperty(value = "是否已装麻袋")
    private String isIntoSacks;
    @ApiModelProperty(value = "是否仓库核重")
    private Integer isWareWeight;
    @ApiModelProperty(value = "是否跟换过面单")
    private String isCoding;

    @ApiModelProperty(value = "运单创建日期起始")
    private String createTimeBegin;
    @ApiModelProperty(value = "运单创建日期结束")
    private String createTimeEnd;

    @ApiModelProperty(value = "运单状态")
    private Integer state;
    @ApiModelProperty(value = "仓库核重日期起始")
    private String wareTimeBegin;
    @ApiModelProperty(value = "仓库核重日期结束")
    private String  wareTimeEnd;

    @ApiModelProperty(value = "当前页")
    private Integer current;
    @ApiModelProperty(value = "每页大小")
    private Integer size;

    /*
     *  问题包裹列表参数
     */
    //0:不是问题包裹  1：打单重量与仓库核重不匹配 2：包裹类型不匹配 3：入境口岸不匹配
    private Integer isProblemParcel;

    @ApiModelProperty(value = "用户批次单号")
    private String userBatchNumber;

}
