package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/9/26 0026-21:28
 *
 *
 *      管理员确定出库参数类
 */
@Data
public class AdminOutboundParam {

    //出库单ID
    private Long outboundId;
    //海外仓仓储项目ID
    private Long warehouseProjectId;
    //海外仓仓储项目数量
    private Integer warehouseProjectNumber;
    //海外仓操作费ID 、操作数量
    List<AdminOutboundHandleParam> handleParams;
    //海外仓包装费ID 、海外仓包装费数量
    List<WarehouseMaterialCostParam> warehouseMaterialCostParams;

}
