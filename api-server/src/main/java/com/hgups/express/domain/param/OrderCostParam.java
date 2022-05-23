package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author wandaming
 * 2021/7/23-17:13
 */

@Data
public class OrderCostParam extends PageParam{
    private List<Integer> id;
    private String sku;
    private String productName;
    private String city;
}
