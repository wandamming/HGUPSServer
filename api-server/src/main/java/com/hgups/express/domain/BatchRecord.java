package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanc
 * 2020/7/1 0001-17:06
 */
@Data
@TableName(value = "batch_record")
@ApiModel
public class BatchRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("batch_id")
    @ApiModelProperty(value = "批量历史id")
    private int batchId;
    @ApiModelProperty(value = "批量名称")
    private String batchName;
    @ApiModelProperty(value = "批量打单总数")
    private int batchSum;
    @ApiModelProperty(value = "批量打单成功数")
    private int batchSuccess;
    @ApiModelProperty(value = "批量打单时间")
    private Date batchCreateTime;
    @ApiModelProperty(value = "批量备注")
    private String comment;
    @ApiModelProperty(value = "批量打单用户id")
    private long userId;
    @ApiModelProperty(value = "渠道")
    private String channel;

}
