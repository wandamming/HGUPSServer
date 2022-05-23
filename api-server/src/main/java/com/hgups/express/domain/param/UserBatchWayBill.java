package com.hgups.express.domain.param;

import com.baomidou.mybatisplus.annotations.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanc
 * 2020/8/7 0007-14:13
 */
@Data
public class UserBatchWayBill implements Serializable {

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "追踪号码")
    private String trackingNumber;
    @ApiModelProperty(value = "价格")
    private double price;
    @ApiModelProperty(value = "备注一")
    private String commentOne;
    @ApiModelProperty(value = "备注二")
    private String commentTwo;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;


}
