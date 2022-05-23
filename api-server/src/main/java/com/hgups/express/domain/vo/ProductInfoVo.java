package com.hgups.express.domain.vo;

import lombok.Data;

/**
 * @author wandaming
 * 2021/7/23-17:18
 */
@Data
public class ProductInfoVo {
    private Integer id;
    private String sku;
    private String image;
    private String chineseName;
    private String englishName;
    private Integer inventoryNumber;
}
