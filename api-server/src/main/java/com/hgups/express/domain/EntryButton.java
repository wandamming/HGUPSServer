package com.hgups.express.domain;

import com.baomidou.mybatisplus.enums.IdType;
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
 * @since 2021-07-26
 */
@TableName("entrybutton")
public class EntryButton implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 按钮名称
     */
    private String name;
    /**
     * 图标
     */
    private String icon;
    /**
     * 描述
     */
    private String description;
    /**
     * 跳转链接
     */
    private String path;
    /**
     * 是否隐藏：0：隐藏；1：不隐藏
     */
    @TableField("is_show")
    private Boolean isShow;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getShow() {
        return isShow;
    }

    public void setShow(Boolean isShow) {
        this.isShow = isShow;
    }



    @Override
    public String toString() {
        return "Entrybutton{" +
                ", id=" + id +
                ", name=" + name +
                ", icon=" + icon +
                ", desc=" + description +
                ", path=" + path +
                ", isShow=" + isShow +
                "}";
    }
}
