package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanc
 * 2020/6/24 0024-16:36
 */
@Data
@TableName(value = "inventory_product")
public class InventoryProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;
    //产品ID
    private Long productId;
    //入库单ID
    private Long inventoryId;
    //预期到货数量
    private Integer productNumber;
    //已到数量
    private Integer arrive;
    //未到数量
    private Integer noArrive;
    //合格数量
    private Integer qualified;
    //不合格数量
    private Integer noQualified;


}
