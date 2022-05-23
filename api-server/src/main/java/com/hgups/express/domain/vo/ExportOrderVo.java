package com.hgups.express.domain.vo;

import com.hgups.express.business.ec.shopify.entity.Order;
import com.hgups.express.domain.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fanc
 * 2021/7/29-15:31
 */

@Data
public class ExportOrderVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private OrderInfo orderInfo;
    private Sender sender;
    private List<ProductInfo> productInfoList;
    private Store store;
    private Platform platform;
    private Channel channel;
    private DeliverRoute deliverRoute;

    public ExportOrderVo(){

    }

    public  ExportOrderVo(OrderInfo orderInfo,Sender sender,List<ProductInfo> productInfoList,Store store,Platform platform,Channel channel,DeliverRoute deliverRoute){
        this.orderInfo = orderInfo;
        this.sender = sender;
        this.productInfoList = productInfoList;
        this.store = store;
        this.platform = platform;
        this.channel = channel;
        this.deliverRoute = deliverRoute;
    }
}
