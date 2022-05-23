package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/13 0013-16:02
 */
@Data
@TableName(value = "user_account")
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    private double balance;
    private long userId;
}
