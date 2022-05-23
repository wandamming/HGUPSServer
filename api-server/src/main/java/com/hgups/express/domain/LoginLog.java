package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanc
 * 2020/6/11 0011-20:56
 */
@Data
@TableName(value = "login_log")
public class LoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    private String loginName;
    private String loginIp;
    private Date loginTime;

}
