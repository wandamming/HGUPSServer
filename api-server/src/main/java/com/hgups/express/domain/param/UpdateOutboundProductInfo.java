package com.hgups.express.domain.param;

import lombok.Data;

import java.util.Date;

/**
 * @author fanc
 * 2020/9/30 0030-13:23
 */
@Data
public class UpdateOutboundProductInfo {

    private Long id;
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
    //重量
    private double weight;
    //长度
    private double length;
    //宽
    private double width;
    //高
    private double height;
    //物品品名
    private String articleDescribe;
    //库存数量
    private Integer inventoryNumber;
    //库存预警数量
    private Integer inventoryWarnNumber;
    //图片URL
    private String imageUrl;
    //物品描述中文
    private String cDescribe;
    //物品描述英文
    private String eDescribe;
    //单价
    private double price;
    //单位重量
    private double unitWeight;
    //状态
    private Integer state=1;
    //创建时间
    private Date createTime;
    //申报价值（单个）
    private double declareCost;

    //产品数量
    private Integer productNumber;
    private int number;
    private int opId;
}
