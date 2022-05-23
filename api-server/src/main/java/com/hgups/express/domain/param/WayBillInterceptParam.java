package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/7/22 0022-15:02
 */
@Data
public class WayBillInterceptParam extends PageParam{

    private List<String> trackingNumbers;
    private int userId;
    private String interceptBeginTime;
    private String interceptEndTime;

    //是否是后程用户ID
    private Long processUserId;

}
