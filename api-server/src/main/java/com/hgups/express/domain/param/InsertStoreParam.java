package com.hgups.express.domain.param;

import lombok.Data;

import javax.annotation.Resource;


/**
 * @author wandaming
 * 2021/7/23-18:12
 */
@Data
public class InsertStoreParam {
    private Integer platformId;
    private String storeName;
    private String vat;
    private String storeDirector;
    private String platformAccount;
    private String amazonURL;
    private String storeType;
    private String subDomain;

}
