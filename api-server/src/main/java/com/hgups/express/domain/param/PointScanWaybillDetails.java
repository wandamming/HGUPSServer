package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author fanc
 * 2020/11/6-21:59
 */
@Data
public class PointScanWaybillDetails {

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "发件人姓名")
    private String senderName;
    @ApiModelProperty(value = "仓库核重")
    private double wareWeight;
    @ApiModelProperty(value = "仓库核重价格")
    private double warePrice;
    @ApiModelProperty(value = "追踪号码")
    private String trackingNumber;
    private  String phone;


}
