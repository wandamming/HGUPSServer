package com.hgups.express.domain.param;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author fanc
 * 2020/12/11-16:49
 */
@Data
public class NotReplaceSendOutboundDetailsVo {

    //出库单号
    private String outboundOrder;
    //出库运单号
    private String warehouseWaybillNumber;
    //创建时间
    private Date createTime;
    //处理时间
    private Date manageTime;
    //出库时间
    private Date outboundTime;
    //物流方式（1：国内 2：国外）
    private Integer logisticsMode;
    //3：已创建 4：待出库 5：处理中 6：已出库 7：问题订单
    private Integer state;
    // 1:一件代发 2：非一件代发
    private Integer replaceSend;

    List<OutboundDetailsSubsetVo> productInfoList;
}
