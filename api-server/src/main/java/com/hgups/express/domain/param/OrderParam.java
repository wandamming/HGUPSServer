package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author fanc
 * 2021/7/21-17:57
 */
@Data
public class OrderParam extends PageParam {
    @ApiModelProperty(value = "客户单号")
    private String commerceOrderNum;
    @ApiModelProperty(value = "批次单号")
    private Integer batchNumber;
    @ApiModelProperty(value = "sku编号")
    private String sku;
    @ApiModelProperty(value = "客户名称")
    private String customerName;
    @ApiModelProperty(value = "订单状态")
    private Integer orderState;
    @ApiModelProperty(value = "所属平台")
    private Integer platformId;
    @ApiModelProperty(value = "店铺名称")
    private Integer storeId;
    @ApiModelProperty(value = "发货区域")
    private Integer cityId;
    @ApiModelProperty(value = "发货渠道")
    private Integer channelId;
    @ApiModelProperty(value = "发货路线")
    private Integer deliveryRouteId;
    @ApiModelProperty(value = "开始时间筛选")
    private Date orderStartTime;
    @ApiModelProperty(value = "结束时间筛选")
    private Date orderEndTime;
    @ApiModelProperty(value = "配送方式")
    private Integer deliveryModeId;
    @ApiModelProperty(value = "有无库存")
    private boolean inventory;
    @ApiModelProperty(value = "订单类型")
    private Integer orderTypeId;


}
