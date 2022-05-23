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
@TableName(value = "outbound")
public class Outbound implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;
    //出库单号
    private String outboundOrder;
    //创建时间
    private Date createTime;
    //处理时间
    private Date manageTime;
    //出库时间
    private Date outboundTime;
    //出库运单号
    private String warehouseWaybillNumber;
    //3：已创建 4：待出库 5：处理中 6：已出库 7：问题订单
    private Integer state;
    //1：正常订单2：退货订单
    private Integer outboundState;
    //出库数量
    private Integer outboundNumber;
    //收发件人中间表ID
    private Integer contactId;
    //收件人地址
    private String receiveAddress;
    //发件人地址
    private String sendAddress;
    //收件人姓名
    private String receiveName;
    //发件人姓名
    private String sendName;
    //出库操作用户ID
    private Integer outboundUserId;
    //物流方式（1：国内 2：国外）
    private Integer logisticsMode;
    //出口城市
    private String exportCity;
    //zone
    private String zone;
    //入境口岸
    private String portEntry;
    //出库产品种类总数
    private Integer skuOutboundNumber;
    //出库单运单编码
    private String waybillCode;
    //出库总重量
    private double outboundWeight;
    //出库单编码
    private String outboundOrderCode;
    //出库用户ID
    private Integer userId;
    //DHL 面单URL
    private String dhlUrl;
    // 渠道
    private String channel;
    // 1:一件代发 2：非一件代发
    private Integer replaceSend;

}
