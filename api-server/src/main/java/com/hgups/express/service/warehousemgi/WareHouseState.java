package com.hgups.express.service.warehousemgi;

/**
 * @author fanc
 * 2020/9/17 0017-12:03
 */
public class WareHouseState {

    public static final Integer WAIT_INVENTORY=1;//待入库
    public static final Integer ALREADY_INVENTORY=2;//已入库
    public static final Integer ALREADY_CREATE_OUTBOUND=3;//已创建出库单
    public static final Integer WAIT_OUTBOUND=4;//待出库
    public static final Integer IN_HAND_OUTBOUND=5;//出库处理中
    public static final Integer ALREADY_OUTBOUND=6;//已出库
    public static final Integer PROBLEMS_SINGLE=7;//问题单

}
