package com.hgups.express.domain;

import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wandaming
 * @since 2021-07-19
 */

@Data
@TableName("system_notice")
public class SystemNotice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "序号")
    private Integer id;
    @TableField("synotice_content")
    @ApiModelProperty(value = "消息类容")
    private String synoticeContent;
    @TableField("synotice_type")
    @ApiModelProperty(value = "消息类型")
    private String synoticeType;
    @TableField(value = "synotice_time",fill = FieldFill.INSERT)
    @ApiModelProperty(value = "发送时间")
    private Date synoticeTime;


}
