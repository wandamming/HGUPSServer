package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/7/16 0016-19:57
 */
@Data
public class SubmitUserBatchParam {

    private  List<Integer> WayBillIds;
    private int sacksId;
    private int batchId;


}
