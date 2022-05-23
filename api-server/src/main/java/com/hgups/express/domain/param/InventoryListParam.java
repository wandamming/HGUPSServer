package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/9/18 0018-10:28
 */
@Data
public class InventoryListParam extends PageParam{

    //状态
    private Integer state;
    //创建入库单--开始时间
    private String createBeginTime;
    //创建入库单--结束时间
    private String createEndTime;
    //入库单号
    private List<String> receiptOrders;
    //发货人
    private String receiveName;
    //sku/产品编码
    private String skuCode;


}
