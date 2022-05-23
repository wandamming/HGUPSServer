package com.hgups.express.domain.param;

import lombok.Data;

import java.util.Date;

/**
 * @author fanc
 * 2021/7/23-15:51
 */
@Data
public class ImportOrderParam {
    private Integer Id ;
    private Integer platformType;
    private Integer storeId ;
    private Date orderTime ;
    private Integer payStatus;
    private Integer platformName;
    private Integer storeName;
    private String commerceOrderNum ;
    private String customerName ;
    private String telephone ;

}
