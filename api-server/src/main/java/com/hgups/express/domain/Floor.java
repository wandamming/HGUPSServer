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
@TableName(value = "floor")
public class Floor implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;
    //楼层名
    private String floorName;
    //楼号ID
    private Long buildingNoId;


}
