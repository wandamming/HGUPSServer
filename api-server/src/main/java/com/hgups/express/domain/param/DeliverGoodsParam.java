package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;
/**
 * @author wandaming
 * 2021/7/21-18:08
 */

@Data
public class DeliverGoodsParam {
    private List<Integer> orderId;
    private Integer channel;

}
