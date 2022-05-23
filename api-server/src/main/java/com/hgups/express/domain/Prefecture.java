package com.hgups.express.domain;


import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * @author fanc
 * 2020/6/9 0009-17:29
 */

@Data
@TableName(value = "prefecture")
public class Prefecture implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "区/县名(英)")
    private String preEname;
    @ApiModelProperty(value = "区/县名(中)")
    private String preCname;
    @ApiModelProperty(value = "国家ID")
    private int countriesId;
    @ApiModelProperty(value = "省份ID")
    private int provinceId;
    @ApiModelProperty(value = "城市ID")
    private int cityId;
}
