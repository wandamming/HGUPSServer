package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanc
 * 2020/11/5-11:02
 */
@Data
@TableName(value = "point_scan_record")
public class PointScanRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 扫描类型(1：运单2：麻袋3：批次 4:入库单 5：出库单)
     */
    @TableField("point_type")
    private Integer pointType;
    /**
     * 扫描点ID
     */
    @TableField("point_scan_id")
    private Long pointScanId;
    /**
     * 扫描用户的姓名
     */
    @TableField("scan_user_name")
    private String scanUserName;
    /**
     * 扫描时间
     */
    @TableField("scan_time")
    private Date scanTime;
    /**
     * 是否是系统产生的状态（1：是 0：否）
     */
    @TableField("sys_record")
    private Integer sysRecord;
    /**
     * 单号
     */
    @TableField("order_tracking_number")
    private String orderTrackingNumber;
    /**
     * 扫描点名称
     */
    @TableField("point_scan_name")
    private String pointScanName;
    /**
     * 扫描点内容
     */
    private String content;
    /**
     * 是否是异常扫描 0否，1是 2 补扫
     */
    private Integer isError;
    /**
     * 排序
     */
    private Integer rank;
}
