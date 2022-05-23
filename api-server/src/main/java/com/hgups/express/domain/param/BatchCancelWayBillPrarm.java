package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/8/4 0004-20:35
 */
@Data
public class BatchCancelWayBillPrarm {


    List<CancelWayBillPrarm> cancelWayBillParams;

}
