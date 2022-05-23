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
 * 2020/12/10-16:40
 */
@Data
@TableName(value = "warehouse_rent_cost")
@ApiModel(value = "海外仓仓租费用表")
public class WarehouseRentCost  implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;

    //收费项目
    @TableField("charge_project_id")
    private String chargeProjectId;
    //收费内容
    @TableField("charge_content")
    private String chargeContent;
    //收费价格
    @TableField("charge_price")
    private Double chargePrice;
    //收费备注
    @TableField("charge_comment")
    private String chargeComment;
    //创建时间
    @TableField("create_time")
    private Date createTime;
    //单位
    @TableField("unit")
    private String unit;


}
