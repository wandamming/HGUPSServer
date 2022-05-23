package com.hgups.express.domain.param;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author fanc
 * 2020/9/17 0017-10:08
 */
@Data
public class InventoryParam {


    //入库单描述
    private String describe;
    //预到仓时间
    private Date expectTime;
    //入库产品ID及数量
    List<InventoryProducerParam> producerList;


}
