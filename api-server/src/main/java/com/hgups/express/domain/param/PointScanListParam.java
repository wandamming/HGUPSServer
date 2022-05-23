package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author fanc
 * 2020/11/6-15:35
 */
@Data
public class PointScanListParam{
    //扫描点类型 1:物流过点扫描 2：海外仓过点扫描 2021-03-03 历史版本
    @NotNull(message = "扫描点类型不能为空")
    @ApiModelProperty(value = "扫描点类型 1:全程用户 2:后程用户 3:海外仓",required = true)
    private Integer scanType;
}
