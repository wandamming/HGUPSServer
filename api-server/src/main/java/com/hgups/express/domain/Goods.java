package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fanc
 * 2020/6/11 0011-20:49
 */
@Data
@TableName("goods")
public class Goods implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    private Integer total;
    private String name;
}
