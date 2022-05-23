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
@TableName(value = "warehouse_project")
@ApiModel(value = "海外仓仓租项目表")
public class WarehouseProject  implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;

    //仓租项目名
    @TableField("project_name")
    private String projectName;
    //创建时间
    @TableField("create_time")
    private Date createTime;

}
