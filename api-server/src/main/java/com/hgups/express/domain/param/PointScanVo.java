package com.hgups.express.domain.param;

import com.hgups.express.domain.PointScanRecord;
import com.hgups.express.vo.PointScanRecordVo;
import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/11/5-20:59
 */
@Data
public class PointScanVo {

    //当前状态(当前扫码员的角色名)
    private String scanRoleName;
    //当前状态(当前扫码员的角色ID)
    private Long scanRoleId;
    //实际状态角色名
    private String actualRoleName;
    //实际状态角色ID
    private Long actualRoleId;
    //正常操作员账号
    private String scanUserName;
    //单号
    private String orderTrackingNumber;
    //单号类型
    private Integer pointType;
    //（1：正常扫描 2:异常扫描）
    private Integer scanState;
    //单号状态列表
    private List<PointScanRecordVo> pointScanRecords;
}
