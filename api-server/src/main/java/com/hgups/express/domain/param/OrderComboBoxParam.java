package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;
/**
 * @author wandaming
 * 2021/7/23-17:18
 */
@Data
public class OrderComboBoxParam {
    private List<Integer> orderStatusId;
    private List<Integer> platformId;
    private List<Integer> storeId;
    private List<Integer> deliverAreaId;
    private List<Integer> channelId;
    private List<Integer> deliverRouteId;
    private List<Integer> deliverModeId;
    private List<Integer> inventoryId;
    private List<Integer> orderTypeId;
}
