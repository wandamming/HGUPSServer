package com.hgups.express.domain.param;

import com.hgups.express.domain.Receive;
import com.hgups.express.domain.Sender;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author fanc
 * 2020/9/29 0029-14:15
 */
@Data
public class UpdateOutboundDetailsVo {

    //出库单号
    private String outboundOrder;
    //出库运单号
    private String warehouseWaybillNumber;
    //创建时间
    private Date createTime;
    //物流方式（1：国内 2：国外）
    private Integer logisticsMode;
    //3：已创建 4：待出库 5：处理中 6：已出库 7：问题订单
    private Integer state;
    //出口城市
    private String exportCity;
    // 1:一件代发 2：非一件代发
    private Integer replaceSend;

    private Receive receive;
    private Sender sender;

    List<UpdateOutboundProductInfo> productInfoList;
}
