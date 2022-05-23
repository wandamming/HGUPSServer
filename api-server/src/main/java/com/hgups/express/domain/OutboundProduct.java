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
@TableName(value = "outbound_product")
public class OutboundProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;
    //产品ID
    private Long productId;
    //出库单ID
    private Long outboundId;
    //产品数量
    private Integer productNumber;

}
