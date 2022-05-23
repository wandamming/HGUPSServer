package com.hgups.express.domain.vo;

import lombok.Data;

/**
 * @author wandaming
 * 2021/7/24-15:56
 */
@Data
public class FailOrderVo{
    private Integer id;
    private String platformOrderNum ;
    private String customerName ;
    private String telephone ;
    private String failReason;
}
