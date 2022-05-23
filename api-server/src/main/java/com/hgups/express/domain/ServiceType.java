package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/8 0008-14:30
 */
@Data
@TableName(value = "service_type")
public class ServiceType implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    private String serviceName;
}
