package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanc
 * 2020/11/6-14:19
 */
@Data
@TableName(value = "point_scan")
public class PointScan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    // 扫码点名称
    private String scanName;
    // 状态提示
    private String desc;
    // 扫描点类型 (1前程，2后程，3海外仓)
    private Integer scanType;
    // (1系统,2过点)
    private Integer systemType;
    // (0关闭，1开启)
    private Integer isOpen;
    // 排序
    private Integer rank;
    // (1前程，2后程)
    private Integer flag;
    // 1:未删除 2：已删除
    private Integer fakeDelete;
    // 创建时间
    private Date createTime;
    // 绑定的id
    private String shortName;
}
