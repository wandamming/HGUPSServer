package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/11/6-14:19
 */
@Data
@TableName(value = "scan_role_middle")
public class ScanRoleMiddle  implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;
    //扫描点ID
    private Long pointScanId;
    //角色ID
    private Long roleId;
    //扫描点类型 1:物流过点扫描 2：海外仓过点扫描
    private Integer scanType;

}
