package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/7/17 0017-13:09
 */
@Data
public class SubmitUserBatchListParam {

    List<SubmitUserBatchParam> sacksWayBillIds;
    private int batchId;

}
