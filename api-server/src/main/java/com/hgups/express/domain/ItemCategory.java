package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/10 0010-14:34
 */
@Data
@ApiModel(value = "物品类型表")
@TableName(value = "item_category")
public class ItemCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "物品类型名称")
    private String name;
    @ApiModelProperty(value = "HS")
    private String hs;
    @ApiModelProperty(value = "HTS")
    private String hts;

}
