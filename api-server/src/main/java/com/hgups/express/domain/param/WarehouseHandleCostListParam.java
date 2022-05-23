package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/12/10-17:44
 */
@Data
public class WarehouseHandleCostListParam extends PageParam {

    //收费类型(1：一件代发 2：非一件代发)
    private Integer chargeType;


}
