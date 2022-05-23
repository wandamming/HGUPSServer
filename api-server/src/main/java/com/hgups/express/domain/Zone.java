package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/7/13 0013-15:51
 */
@Data
@TableName(value = "zone")
public class Zone implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "zone邮编")
    private String zipCode;
    @ApiModelProperty(value = "zone区域")
    private String zone;
    @ApiModelProperty(value = "入境口岸ID")
    private int portEntryId;


}
