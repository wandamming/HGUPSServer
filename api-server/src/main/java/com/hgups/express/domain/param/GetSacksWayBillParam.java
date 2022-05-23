package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/7/6 0006-16:57
 */
@Data
public class GetSacksWayBillParam extends PageParam {

    private int sacksId;
    private int batchId;


}
