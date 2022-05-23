package com.hgups.express.constant;

public class Constant {

    //USPS入境口岸类型
    public static final String USPS_PORT_ALL= "all";
    public static final String USPS_PORT_TAIL= "tail";

    /**
     * 一件代发
      */
    public static final int OUTBOUND_TYPE_WAYBILL = 1;
    /**
     * 自提
     */
    public static final int OUTBOUND_TYPE_MANUAL = 2;

    //入库单状态
    public static final int INVENTORY_STATE_WAITING = 1;
    public static final int INVENTORY_STATE_STORED = 2;
    public static final int INVENTORY_STATE_REJECTED = 8;

    //订单状态
    public static final int ORDER_STATUS_HASSENT = 1;  //已发送
    public static final int ORDER_STATUS_NOTSENT = 2;  //待发货
    public static final int ORDER_STATUS_ALREADCANCELED = 3; //已取消


    // 店铺状态 和 授权状态
    public static final int STATUS_USED = 1;
    public static final int STATUS_UNUSED = 2;

    public static final int AUTHORIZESTATUS_SUCCESS = 3;
    public static final int AUTHORIZESTATUS_FAIL = 4;




}
