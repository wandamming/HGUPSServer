package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

/**
 * @Author: LZJ
 * @Date: 2021/3/4 1:08
 */
@Data
@TableName(value = "point_scan_child")
public class PointScanChild {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    // 扫码点名称
    private String scanName;
    // 父扫码点简称
    private String pShortName;
    // 简称
    private String shortName;
}
