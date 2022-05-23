package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/10/22 0022-11:22
 */
@Data
public class ItemsWayBillParam {

    //物品类型
    private String name;

    //运单核重总价格
    private Double cost;

    //运单核重总重量
    private Double weight;

}
