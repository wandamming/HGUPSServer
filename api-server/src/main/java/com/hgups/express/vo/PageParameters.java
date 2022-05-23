package com.hgups.express.vo;

import com.hgups.express.constant.Constant;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fanc
 * 2020/6/11 0011-9:27
 */
@Data
public class PageParameters implements Serializable {

    private Integer current;
    private Integer size;
    private String likes;
    private long userId;
    private String createTimeBegin;
    private String createTimeEnd;
    //交易类型（1：扣费 2：充值 3：退款）
    private Integer dealType;
    //运单单号
    private List<String> trackingNumbers;
    //入境口岸
    private String type = Constant.USPS_PORT_ALL;
}
