package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/6/29 0029-16:14
 */
@Data
public class ConfigParamTo {

    //报关单价
    private String declarePrice;
    //航空单价
    private String aviationPrice;
    //汇率
    private String exchangeRate;

}
