package com.hgups.express.domain;

import com.baomidou.mybatisplus.enums.IdType;
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
 * @author lyx
 * @since 2021-07-27
 */
@TableName("banner")
public class Banner implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 对banner的描述
     */
    private String descript;
    /**
     * 图片
     */
    @TableField("image_url")
    private String imageUrl;
    /**
     * 类型
     */
    @TableField("action_type")
    private String actionType;
    /**
     * 跳转链接
     */
    @TableField("action_value")
    private String actionValue;
    /**
     * 是否隐藏：1表示不隐藏；0表示隐藏
     */
    @TableField("is_show")
    private Boolean isShow;
    /**
     * 权重：用于排序
     */
    private Integer weight;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 类型
     */
    private String type;
    /**
     * 模块：banner，公告，帮助
     */
    private String module;
    /**
     * 标题：用于公告，用户帮助
     */
    private String title;
    /**
     * 所处首页的位置
     */
    @TableField("location_id")
    private Integer locationId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionValue() {
        return actionValue;
    }

    public void setActionValue(String actionValue) {
        this.actionValue = actionValue;
    }

    public Boolean getShow() {
        return isShow;
    }

    public void setShow(Boolean isShow) {
        this.isShow = isShow;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @Override
    public String toString() {
        return "Banner{" +
        ", id=" + id +
        ", descript=" + descript +
        ", imageUrl=" + imageUrl +
        ", actionType=" + actionType +
        ", actionValue=" + actionValue +
        ", isShow=" + isShow +
        ", weight=" + weight +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", type=" + type +
        ", module=" + module +
        ", title=" + title +
        ", locationId=" + locationId +
        "}";
    }
}
