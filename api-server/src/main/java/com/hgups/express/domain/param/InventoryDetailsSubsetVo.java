package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/9/28 0028-17:53
 */
@Data
public class InventoryDetailsSubsetVo {

    //sku/产品编码
    private String skuCode;
    //英文名称
    private String eName;
    //中文名称
    private String cName;
    //用户编码1
    private String codingOne;
    //用户编码2
    private String codingTwo;
    //产品数量
    private Integer productNumber;
    //已到数量
    private int arrive;
    //未到数量
    private int noArrive;
    //合格数量
    private int qualified;
    //不合格数量
    private int noQualified;
    private int id;


}
