package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author fanc
 * 2020/7/7 0007-16:56
 */
@Data
public class CreateShippingBatchParam {

    private int id;
    @ApiModelProperty(value = "批次名")
    private String name;
    @ApiModelProperty(value = "MAWB（航空主运单号）")
    private String mawb;
    @ApiModelProperty(value = "备注")
    private String comment;
    @ApiModelProperty(value = "入境地点")
    private String entrySite;
    @ApiModelProperty(value = "航班号")
    private String flightNo;
    @ApiModelProperty(value = "出发时间")
    private Date beginTime;
    @ApiModelProperty(value = "达到时间")
    private Date endTime;
    //渠道
    private String channel;


}
