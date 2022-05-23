package com.hgups.express.domain.param;

import com.hgups.express.vo.PageParameters;
import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/7/1 0001-19:00
 */
@Data
public class BatchRecordWayBillParam extends PageParameters {

    private int state;
    private long moreId;
    private String channel;
    private List<String> trackingNumbers;
    private String userBatchNumber;

    private String createTimeBegin;
    private String createTimeEnd;

}
