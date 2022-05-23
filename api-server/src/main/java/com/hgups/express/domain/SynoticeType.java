package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hgups.express.domain.param.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author fanc
 * 2021/7/20-18:09
 */
@Data
@TableName(value = "synotice_type")
@ApiModel(value = "通知类型表")
public class SynoticeType extends PageParam {
    @TableField("synotice_type")
    @ApiModelProperty(value = "通知类型")
    private String synoticeType;
}
