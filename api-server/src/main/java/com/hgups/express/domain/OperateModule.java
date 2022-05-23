package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author lyx
 * @since 2021-07-16
 */
@TableName("operate_module")
public class OperateModule implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    /**
     * 模块名称
     */
    @TableField("module_name")
    private String moduleName;
    /**
     * 0:显示 1:不显示
     */
    @TableField("is_show")
    private Integer isShow;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    @Override
    public String toString() {
        return "OperateModule{" +
        ", id=" + id +
        ", moduleName=" + moduleName +
        ", isShow=" + isShow +
        "}";
    }
}
