package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/11/6-17:23
 */
@Data
public class PointScanListVo {
    @ApiModelProperty("扫描点ID")
    private Long id;
    @ApiModelProperty("扫描点名称")
    private String scanName;
    @ApiModelProperty("状态提示")
    private String desc;
    @ApiModelProperty("扫描点类型 (1全程，2后程，3海外仓)")
    private Integer scanType;
    @ApiModelProperty("1系统,2过点")
    private Integer systemType;
    @ApiModelProperty("0关闭，1开启")
    private Integer isOpen;
    @ApiModelProperty("排序")
    private Integer rank;
    @ApiModelProperty("1前程，2后程")
    private Integer flag;
    @ApiModelProperty("1:未删除 2：已删除")
    private Integer fakeDelete;
    @ApiModelProperty("创建时间")
    private Long createTime;
    @ApiModelProperty("角色ID")
    private List<Long> roleIds;
}
