package com.hgups.express.domain.param;

import lombok.Data;
/**
 * @author wandaming
 * 2021/7/23-17:33
 */
@Data
public class StoreParam extends PageParam{
    private String storeName;
    private Integer platformId;
    private int state;
    private int authorizeState;

}
