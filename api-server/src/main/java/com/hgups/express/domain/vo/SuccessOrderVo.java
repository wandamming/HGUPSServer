package com.hgups.express.domain.vo;

import lombok.Data;

/**
 * @author wandaming
 * 2021/7/24-15:54
 */
@Data
public class SuccessOrderVo{
    private Integer id;
    private String platformWaybillNum ;
    private String platformOrderNum ;
    private String customerName ;
    private String telephone ;
    private Integer weight;
    private Integer cost;
}