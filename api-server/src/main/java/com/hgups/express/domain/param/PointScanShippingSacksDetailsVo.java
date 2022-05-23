package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author fanc
 * 2020/11/6-21:52
 */
@Data
public class PointScanShippingSacksDetailsVo {

    @ApiModelProperty(value = "麻袋核重重量")
    private double wareWeight;
    @ApiModelProperty(value = "麻袋创建时间")
    private Date createTime;
    @ApiModelProperty(value = "包裹数量")
    private int parcelNumber;
    @ApiModelProperty(value = "核重总金额")
    private double warePrice;
    List<PointScanSacksWayBillVo> wayBillVos;
}
