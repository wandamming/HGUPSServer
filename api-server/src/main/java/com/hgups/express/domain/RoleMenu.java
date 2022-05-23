package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/11 0011-17:34
 */
@Data
@TableName(value = "role_menu")
public class RoleMenu implements Serializable {


    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    private int roleId;
    private int menuId;

}
