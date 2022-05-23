package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/20 0020-9:41
 */
@Data
@TableName(value = "port_entry")
public class PortEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "入境口岸名称")
    private String title;
    @ApiModelProperty(value = "邮政编码")
    private String zipCode;
    @ApiModelProperty(value = "入境口岸状态")
    private int state;
    @ApiModelProperty(value = "入境口岸联系人")
    private int senderId;
    @ApiModelProperty(value = "mid")
    @TableField(value = "mid")
    private String mid;
    @ApiModelProperty(value = "crid")
    @TableField(value = "crid")
    private String crid;
    @ApiModelProperty(value = "usps渠道的入境口岸，all：全程（默认）；tail：后程", required = false)
    private String type;

}
