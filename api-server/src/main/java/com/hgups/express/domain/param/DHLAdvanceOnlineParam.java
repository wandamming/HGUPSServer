package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/11/12-11:11
 */
@Data
public class DHLAdvanceOnlineParam {

    //批次id
    private List<Integer> ids;
    //运单号
    private List<String> trackingNumbers;
    //位置
    private String location;
    //描述
    private String Description;
    //省份
    private String province;

}
