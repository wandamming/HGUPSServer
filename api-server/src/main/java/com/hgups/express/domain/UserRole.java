package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/11 0011-17:21
 */
@Data
@TableName("user_role")
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("user_id")
    private Long userId;
    @TableField("role_id")
    private int roleId;

}
