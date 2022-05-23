package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/11 0011-10:29
 */
@Data
@ApiModel(value = "用户表")
@TableName(value = "user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String phonePrefix;
    private String company;
    private String email;
    private String salt;
    private int state;
    private Integer handleId;
    private boolean customsPrice=false;
}
