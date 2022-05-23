package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/11/5-22:23
 */
@Data
public class NotarizePointScanParam{

    //（1：正常扫描 2:异常扫描）
    private Integer scanState;
    //单号
    private String orderTrackingNumber;
    //单号类型
    private Integer pointType;
    //当前状态(当前扫码员的角色名)
    private String scanRoleName;
    //当前状态(当前扫码员的角色Id)
    private Long scanRoleId;
    //实际状态角色姓名
    private String actualRoleName;
    //实际状态角色ID
    private Long actualRoleId;

}
