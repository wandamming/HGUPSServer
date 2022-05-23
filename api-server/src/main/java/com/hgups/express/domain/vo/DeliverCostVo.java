package com.hgups.express.domain.vo;

import lombok.Data;

/**
 * @author wandaming
 * 2021/7/24-16:02
 */
@Data
public class DeliverCostVo {
    private Integer id;
    private String platformOrderNum;
    private String customerName;
    private String telephone;
    private String weight;
    private String cost;
}
