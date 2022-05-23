package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/10/9 0009-14:41
 */
@Data
public class RefuseInventoryReasonParam {

    //入库ID
    private Long id;
    //拒绝入库原因
    private String refuseReason;

}
