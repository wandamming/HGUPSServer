package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/7/31 0031-13:25
 */
@Data
public class ProblemWayBillParam extends PageParam{

    //1:需要根据运单号查询，其他：不需要..
    private String isTrackingNumber;
    @ApiModelProperty(value = "追踪号码")
    private List<String> trackingNumbers;

    private Integer isProblemParcel=-1;

    @ApiModelProperty(value = "日期起始")
    private String createTimeBegin;
    @ApiModelProperty(value = "日期结束")
    private String createTimeEnd;

    //是否是后程用户ID
    private Long processUserId;

}
