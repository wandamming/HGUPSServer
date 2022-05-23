package com.hgups.express.domain;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wdm
 * @since 2021-07-26
 */
@Data
@TableName("platform")
public class Platform implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("name")
    private String name;
    @TableField("platform_url")
    private String platformUrl;
    /**
     * 平台地址
     */
    @TableField("platform_address")
    private String platformAddress;
    /**
     * 0隐藏1显示
     */
    @TableField("is_show")
    private Boolean isShow;


}
