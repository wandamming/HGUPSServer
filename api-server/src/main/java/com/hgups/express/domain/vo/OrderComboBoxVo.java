package com.hgups.express.domain.vo;

import com.hgups.express.domain.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author wandaming
 * 2021/7/23-17:18
 */
@Data
public class OrderComboBoxVo {
    private List<String> orderStatus;
    private List<Map<String, Object>> platform;
    private List<Map<String, Object>> city;
    private List<Map<String, Object>> store;
    private List<Channel> channel;
    private List<String> deliverRoute;
    private List<DeliverMode> deliverMode;
    private List<String> inventory;
    private List<OrderType> orderType;
}
