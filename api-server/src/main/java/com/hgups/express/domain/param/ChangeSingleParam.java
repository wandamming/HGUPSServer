package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/7/31 0031-13:25
 */
@Data
public class ChangeSingleParam extends PageParam{

    //1:需要根据运单号查询，其他：不需要..
    private String isTrackingNumber;
    @ApiModelProperty(value = "追踪号码")
    private List<String> trackingNumbers;

    private Integer userId;

    @ApiModelProperty(value = "换单日期起始")
    private String changeSingleTimeBegin;
    @ApiModelProperty(value = "换单日期结束")
    private String changeSingleTimeEnd;

    //是否是后程用户ID
    private Long processUserId;

}
