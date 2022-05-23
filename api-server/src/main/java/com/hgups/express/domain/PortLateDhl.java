package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/11/27-17:43
 */
@Data
@TableName(value = "port_late_dhl")
public class PortLateDhl implements Serializable {


    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;
    //入境口岸名称
    @TableField("port_late_dhl_name")
    private String portLateDhlName;
    //后程DHL入境口岸状态
    @TableField("late_dhl_state")
    private Integer lateDhlState;
    //管理员入境口岸id
    @TableField("port_dhl_id")
    private Long portId;
    //管理员DHL入境口岸状态
    @TableField("port_dhl_state")
    private Integer portDhlState;

}
