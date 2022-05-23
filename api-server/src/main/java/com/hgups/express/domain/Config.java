package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/24 0024-16:36
 */
@Data
@TableName(value = "config")
public class Config implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField(value = "k")
    private String k;
    @TableField(value = "v")
    private String v;

}
