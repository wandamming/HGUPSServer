package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/12/10-20:00
 *
 *      操作费ID 及  数量
 */
@Data
public class AdminOutboundHandleParam {

    //操作费用ID
    private Long handleId;
    //数量
    private Integer handleNumber;


}
