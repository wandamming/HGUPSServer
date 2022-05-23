package com.hgups.express.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * @author fanc
 * 2020/6/8 0008-14:27
 */

@Data
@TableName(value = "countries")
@ApiModel(value = "国家表")
public class Countries implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId("id")
    private int id;
    @TableField("cname")
    @ApiModelProperty(value = "国名(中)")
    private String cname;
    @TableField("ename")
    @ApiModelProperty(value = "国名(英)")
    private String ename;

    @TableField(exist = false)
    private List<Province> children;


}
