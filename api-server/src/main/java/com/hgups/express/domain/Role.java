package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/11 0011-17:10
 */
@Data
@TableName(value = "role")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    private String roleName;//角色名称
    private String Introduction;//角色描述
}
