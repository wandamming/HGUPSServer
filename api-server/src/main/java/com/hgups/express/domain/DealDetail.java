package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanc
 * 2020/6/14 0014-12:20
 */
@Data
@ApiModel(value = "交易明细表")
@TableName(value = "deal_detail")
public class DealDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int TYPE_CHARGING = 1;  //扣费
    public static final int TYPE_RECHARGE = 2;  //充值
    public static final int TYPE_REFUND = 3;  //退费
    public static final int STATE_OK = 1;

    @TableId("id")
    private int id;
    private long userId;
    private double dealAmount;
    private String comment;
    private int state;
    private Date dealTime;
    private double balance;
    private int dealType;
    private String serialNumber;
    private int wayBillId;
    @TableField(exist = false)
    private String trackingNumber;
    private Integer flag=1;
}
