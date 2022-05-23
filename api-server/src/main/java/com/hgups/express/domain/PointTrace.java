package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanc
 * 2020/6/24 0024-16:36
 * 过点扫描记录类
 */
@Data
@TableName(value = "point_trace")
public class PointTrace implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;
    //运单ID
    private Integer waybillId;
    //过点扫描用户用户
    private String username;
    //备注
    private String comment;
    //过点扫描时间
    private Date createTime;

}
