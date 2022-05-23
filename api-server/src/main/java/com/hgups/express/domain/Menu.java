package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/11 0011-17:30
 */
@Data
@TableName(value = "menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Integer id;
    private String name;
    private String url;
    private Integer pid;
    private Integer show;
}
