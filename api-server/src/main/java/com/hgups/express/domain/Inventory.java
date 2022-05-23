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
@TableName(value = "inventory")
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;
    //入库单号
    private String receiptOrder;
    //预约入库总数(本次库单产品种类 * 对应产品件数)
    private Integer receiptNumber;
    //用户ID
    private Integer userId;
    //1:待入库2:已入库
    private Integer state;
    //SKU数量(本次库单产品种类)
    private Integer skuNumber;
    //创建时间
    private Date createTime;
    //预到仓时间
    private Date expectTime;
    //到仓时间（审核时间、入库时间）
    private Date arriveTime;
    //已到数量
    private Integer arrive;
    //未到数量
    private Integer noArrive;
    //合格数量
    private Integer qualified;
    //不合格数量
    private Integer noQualified;
    //收发件人中间表ID
    private Long contactId;
    //发件人地址
    private String receiveAddress;
    //收件人地址
    private String senderAddress;
    //发件人姓名
    private String receiveName;
    //收件人姓名
    private String senderName;
    //入库操作用户ID
    private Integer receiptUserId ;
    //库单运单号
    private String warehouseWaybillNumber;
    //入库单描述
    private String describe;
    //入库单base64编码
    private String receiptCoding;
    //入库仓库地址
    private String inventoryAddress;
    //入库重量
    private double inventoryWeight;
    //拒绝入库原因
    private String refuseReason;

}
