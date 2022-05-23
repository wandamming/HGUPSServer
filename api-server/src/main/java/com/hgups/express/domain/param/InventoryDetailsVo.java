package com.hgups.express.domain.param;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author fanc
 * 2020/9/28 0028-17:31
 */
@Data
public class InventoryDetailsVo {

    //入库单号
    private String receiptOrder;
    //创建时间
    private Date createTime;
    //到仓时间（审核时间、入库时间）
    private Date arriveTime;
    //预到仓时间
    private Date expectTime;
    //SKU数量(本次库单产品种类)
    private Integer skuNumber;
    //预约入库总数(本次库单产品种类 * 对应产品件数)
    private Integer receiptNumber;
    //已到数量
    private Integer arrive;
    //未到数量
    private Integer noArrive;
    //合格数量
    private Integer qualified;
    //不合格数量
    private Integer noQualified;
    //拒绝入库原因
    private String refuseReason;

    List<InventoryDetailsSubsetVo> productInfoList;

    private String customer;



}
