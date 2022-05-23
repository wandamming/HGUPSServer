package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author wandaming
 * @since 2021-07-16
 */
@Data
@TableName("apiaccount")
@ApiModel(value = "apiAccount（api账户列表）")
public class ApiAccount implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId("id")
    @ApiModelProperty(value = "序号")
    private Integer id;

    @TableField("userid")
    @ApiModelProperty(value = "用户账号")
    private String userId;

    @TableId("uname")
    @TableField("uname")
    @ApiModelProperty(value = "用户名称")
    private String uname;

    @TableField("appToken")
    @ApiModelProperty(value = "appToken")
    private String appToken;

    @TableField("appKey")
    @ApiModelProperty(value = "appKey")
    private String appKey;

    @TableField("remarks")
    @ApiModelProperty(value = "备注")
    private  String remarks;
}