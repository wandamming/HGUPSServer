package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

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
@TableName("operate_log")
public class OperateLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("operate_time")
    private Date operateTime;
    @TableField("user_id")
    private Integer userId;
    @TableField("module_id")
    private String moduleId;
    @TableField("operate_record")
    private String operateRecord;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getOperateRecord() {
        return operateRecord;
    }

    public void setOperateRecord(String operateRecord) {
        this.operateRecord = operateRecord;
    }

    @Override
    public String toString() {
        return "OperateLog{" +
                "id=" + id +
                ", operateTime=" + operateTime +
                ", userId=" + userId +
                ", moduleId='" + moduleId + '\'' +
                ", operateRecord='" + operateRecord + '\'' +
                '}';
    }
}
