package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author fanc
 * 2020/7/6 0006-14:52
 */
@Data
public class BatchUserSacksParam {

    @ApiModelProperty(value = "麻袋ID")
    private int id;
    @ApiModelProperty(value = "包裹数量")
    private int parcelNumber;
    @ApiModelProperty(value = "麻袋服务")
    private String service;
    @ApiModelProperty(value = "麻袋备注")
    private String comment;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "麻袋面单编码")
    private String coding;

}
