package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/9/19 0019-16:21
 */
@Data
public class BatchAddOutBoundParam {

    private Long id;
    //物流方式（1：国内 2：国外）
    private Integer logisticsMode;
    //联系人及产品参数
    private List<OutBoundParam> outBoundParams;

}
