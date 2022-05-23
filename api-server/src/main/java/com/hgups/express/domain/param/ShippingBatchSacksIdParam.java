package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/7/17 0017-17:58
 */
@Data
public class ShippingBatchSacksIdParam {

    private int batchId;
    private List sacksIdsInto;
    private List sacksIdsOut;


}
