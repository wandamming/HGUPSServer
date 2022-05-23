package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fanc
 * 2020/6/6 0006-10:55
 */
@Data
@TableName(value = "city")
public class City implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "城市名(中)")
    private String cityCname;
    @ApiModelProperty(value = "城市名(英)")
    private String cityEname;
    @ApiModelProperty(value = "国家ID")
    private int countriesId;
    @ApiModelProperty(value = "省份ID")
    private int provinceId;

    @TableField(exist = false)
    private List<Prefecture> children;

}
