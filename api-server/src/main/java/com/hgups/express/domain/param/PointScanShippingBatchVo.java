package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author fanc
 * 2020/11/6-21:21
 */
@Data
public class PointScanShippingBatchVo {

    private Date createTime;
    private String trackingNumber;
    @ApiModelProperty(value = "批次名")
    private String name;
    @ApiModelProperty(value = "麻袋数量")
    private int sacksNumber;
    @ApiModelProperty(value = "运单数量")
    private int parcelNumber;
    @ApiModelProperty(value = "核重总金额")
    private double warePrice;
    @ApiModelProperty(value = "核重总重量")
    private double wareWeight;
    @ApiModelProperty(value = "入境地点")
    private String entrySite;
    //麻袋信息
    List<PointScanShippingSacksVo> shippingSacks;


}
