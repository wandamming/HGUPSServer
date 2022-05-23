package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/12/8-18:17
 */
@Data
public class OutBoundParam{
    //出口城市
    private String exportCity;
    //收件人ID
    private Integer receiveId;
    //发件人ID
    private Integer senderId;
    //入库产品ID及数量
    private List<InventoryProducerParam> producerList;
}