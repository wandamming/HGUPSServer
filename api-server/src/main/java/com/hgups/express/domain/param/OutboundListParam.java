package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/9/27 0027-11:39
 */
@Data
public class OutboundListParam extends PageParam{

    //状态
    private Integer state;
    //创建入库单--开始时间
    private String createBeginTime;
    //出库运单单号
    private List<String> warehouseWaybillNumbers;
    //出库单号
    private List<String> receiptOrders;
    //发货人
    private String sendName;
    //sku/产品编码
    private String skuCode;
    //出库时间
    private String outboundTime;
    //处理时间
    private String manageTime;
    // 1:一件代发 2：非一件代发
    private Integer replaceSend;

}
