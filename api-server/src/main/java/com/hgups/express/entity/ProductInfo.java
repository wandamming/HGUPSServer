package com.hgups.express.entity;

import com.baomidou.mybatisplus.enums.IdType;
import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wdm
 * @since 2021-07-27
 */
@TableName("product_info")
public class ProductInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * sku/产品编码
     */
    @TableField("sku_code")
    private String skuCode;
    /**
     * 英文名称
     */
    @TableField("e_name")
    private String eName;
    /**
     * 中文名称
     */
    @TableField("c_name")
    private String cName;
    /**
     * 用户编码1
     */
    @TableField("coding_one")
    private String codingOne;
    /**
     * 用户编码2
     */
    @TableField("coding_two")
    private String codingTwo;
    /**
     * 重量
     */
    private BigDecimal weight;
    /**
     * 长度
     */
    private BigDecimal length;
    /**
     * 宽
     */
    private BigDecimal width;
    /**
     * 高
     */
    private BigDecimal height;
    /**
     * 物品品名
     */
    @TableField("article_describe")
    private String articleDescribe;
    /**
     * 库存数量
     */
    @TableField("inventory_number")
    private Integer inventoryNumber;
    /**
     * 库存预警数量
     */
    @TableField("inventory_warn_number")
    private Integer inventoryWarnNumber;
    /**
     * 图片URL
     */
    @TableField("image_url")
    private String imageUrl;
    /**
     * 物品描述中文
     */
    @TableField("c_describe")
    private String cDescribe;
    /**
     * 物品描述英文
     */
    @TableField("e_describe")
    private String eDescribe;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * 单位重量
     */
    @TableField("unit_weight")
    private BigDecimal unitWeight;
    /**
     * 1：开放0：禁用
     */
    private Integer state;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 申报价值（单个）
     */
    @TableField("declare_cost")
    private BigDecimal declareCost;
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Integer userId;
    /**
     * 1：正常产品 2：被删除的产品
     */
    private Integer flag;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getCodingOne() {
        return codingOne;
    }

    public void setCodingOne(String codingOne) {
        this.codingOne = codingOne;
    }

    public String getCodingTwo() {
        return codingTwo;
    }

    public void setCodingTwo(String codingTwo) {
        this.codingTwo = codingTwo;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public String getArticleDescribe() {
        return articleDescribe;
    }

    public void setArticleDescribe(String articleDescribe) {
        this.articleDescribe = articleDescribe;
    }

    public Integer getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(Integer inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public Integer getInventoryWarnNumber() {
        return inventoryWarnNumber;
    }

    public void setInventoryWarnNumber(Integer inventoryWarnNumber) {
        this.inventoryWarnNumber = inventoryWarnNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getcDescribe() {
        return cDescribe;
    }

    public void setcDescribe(String cDescribe) {
        this.cDescribe = cDescribe;
    }

    public String geteDescribe() {
        return eDescribe;
    }

    public void seteDescribe(String eDescribe) {
        this.eDescribe = eDescribe;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getUnitWeight() {
        return unitWeight;
    }

    public void setUnitWeight(BigDecimal unitWeight) {
        this.unitWeight = unitWeight;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public BigDecimal getDeclareCost() {
        return declareCost;
    }

    public void setDeclareCost(BigDecimal declareCost) {
        this.declareCost = declareCost;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "ProductInfo{" +
        ", id=" + id +
        ", skuCode=" + skuCode +
        ", eName=" + eName +
        ", cName=" + cName +
        ", codingOne=" + codingOne +
        ", codingTwo=" + codingTwo +
        ", weight=" + weight +
        ", length=" + length +
        ", width=" + width +
        ", height=" + height +
        ", articleDescribe=" + articleDescribe +
        ", inventoryNumber=" + inventoryNumber +
        ", inventoryWarnNumber=" + inventoryWarnNumber +
        ", imageUrl=" + imageUrl +
        ", cDescribe=" + cDescribe +
        ", eDescribe=" + eDescribe +
        ", price=" + price +
        ", unitWeight=" + unitWeight +
        ", state=" + state +
        ", createTime=" + createTime +
        ", declareCost=" + declareCost +
        ", userId=" + userId +
        ", flag=" + flag +
        "}";
    }
}
