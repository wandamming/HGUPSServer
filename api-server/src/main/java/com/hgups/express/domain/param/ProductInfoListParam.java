package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/9/16 0016-17:54
 */
@Data
public class ProductInfoListParam extends PageParam{

    //sku/产品编码
    private String skuCode;
    //英文名称
    private String productName;
}
