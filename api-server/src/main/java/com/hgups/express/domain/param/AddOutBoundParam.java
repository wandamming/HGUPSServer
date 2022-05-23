package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/9/19 0019-16:21
 */
@Data
public class AddOutBoundParam {

    //出库单ID
    private Long id;
    //物流方式（1：国内 2：国外）
    private Integer logisticsMode;
    //出口城市
    private String exportCity;
    //发件人ID
    private Integer receiveId;
    //收件人ID
    private Integer senderId;
    //入库产品ID及数量
    private List<InventoryProducerParam> producerList;
    // 1:一件代发 2：非一件代发
    private Integer replaceSend=1;

}
