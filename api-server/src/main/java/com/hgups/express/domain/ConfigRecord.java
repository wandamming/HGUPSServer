package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author lyx
 * @since 2021-07-16
 */
@TableName("config_record")
public class ConfigRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String config;
    @TableField("modified_id")
    private String modifiedId;
    @TableField("modified_name")
    private String modifiedName;
    @TableField("modified_time")
    private Date modifiedTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(String modifiedId) {
        this.modifiedId = modifiedId;
    }

    public String getModifiedName() {
        return modifiedName;
    }

    public void setModifiedName(String modifiedName) {
        this.modifiedName = modifiedName;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Override
    public String toString() {
        return "ConfigRecord{" +
        ", id=" + id +
        ", config=" + config +
        ", modifiedId=" + modifiedId +
        ", modifiedName=" + modifiedName +
        ", modifiedTime=" + modifiedTime +
        "}";
    }
}
