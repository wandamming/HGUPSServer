package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * @author fanc
 * 2020/6/14 0014-12:31
 */
@Data
@ApiModel(value = "交易明细参数")
public class DealDetailParam {

    private double dealAmount;
    private String comment;
    private int state;
    private Date dealTime;
    private double balance;
    private int dealType;
}
