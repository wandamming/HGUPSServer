package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/24 0024-16:36
 */
@Data
@TableName(value = "port_late")
public class PortLate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;
    //入境口岸名称
    private String portLateName;
    //入境口岸状态
    private Integer lateState;
    //管理员入境口岸id
    private Integer portId;
    //管理员入境口岸id
    private Integer portState;
    //用户ID
    private Long userId;

}
