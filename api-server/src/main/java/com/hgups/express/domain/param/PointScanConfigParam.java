package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/11/6-14:41
 */
@Data
public class PointScanConfigParam {
//    @ApiModelProperty("角色ID")
//    private List<Long> roleIds;
    @ApiModelProperty("扫描点ID")
    private Long pointScanId;
    @ApiModelProperty("扫描点名称")
    private String scanName;
    @ApiModelProperty("扫描点类型(1全程，2后程，3海外仓)")
    private Integer scanType;
    @ApiModelProperty("状态提示")
    private String desc;
}
