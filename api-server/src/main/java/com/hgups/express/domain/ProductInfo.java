package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanc
 * 2020/6/24 0024-16:36
 */
@Data
@TableName(value = "product_info")
public class ProductInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
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
    //用户ID
    private Integer userId;
    //1:正常产品 2：被删除的产品
    private Integer flag;

}
