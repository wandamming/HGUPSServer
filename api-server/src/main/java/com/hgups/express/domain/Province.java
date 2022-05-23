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
 * 2020/6/6 0006-10:55
 */
@Data
@TableName(value = "province")
@ApiModel(value = "省份表")
public class Province implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "省名(中)")
    private String proCname;
    @ApiModelProperty(value = "省名(英文简写)")
    private String proEname;
    @ApiModelProperty(value = "省名(英)")
    private String proEnglish;
    @ApiModelProperty(value = "国家ID")
    private int countriesId;

    @TableField(exist = false)
    private List<City> children;

}
