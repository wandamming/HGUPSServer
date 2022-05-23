package com.hgups.express.domain;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wdm
 * @since 2021-07-26
 */
@TableName("order_info")
@Data
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 平台订单号（客户单号）
     */
    @TableField("platform_order_num")
    private String platformOrderNum;
    /**
     * 电商订单号
     */
    @TableField("commerce_order_num")
    private String commerceOrderNum;
    /**
     * 客户名称
     */
    @TableField("customer_name")
    private String customerName;
    /**
     * 联系电话
     */
    private String telephone;
    /**
     * 1：未发送，2:已发送，3：已取消
     */
    @TableField("order_state")
    private Integer orderState;
    /**
     * 下单时间（创建订单时间）
     */
    @TableField("order_time")
    private Date orderTime;
    /**
     * 绑定订单类型id
     */
    @TableField("ordertype_id")
    private Integer orderTypeId;
    /**
     * 平台id
     */
    @TableField("platform_id")
    private Integer platformId;
    @TableField("product_id")
    private Integer productId;
    /**
     * sku编号
     */
    private String sku;
    /**
     * 店铺
     */
    @TableField("store_id")
    private Integer storeId;
    /**
     * 发货区域
     */
    @TableField("city_id")
    private Integer cityId;
    /**
     * 发货渠道
     */
    @TableField("channel_id")
    private Integer channelId;
    /**
     * 发货路线
     */
    @TableField("deliverRoute_id")
    private Integer deliverRouteId;
    /**
     * 配送方式
     */
    @TableField("deliverymode_id")
    private Integer deliveryModeId;
    /**
     * 订单状态
     */
    @TableField("orderstate_value")
    private String orderStateValue;

}
