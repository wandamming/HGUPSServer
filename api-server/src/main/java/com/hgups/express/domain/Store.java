package com.hgups.express.domain;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
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
@TableName("store")
public class Store implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    /**
     * 平台id
     */
    @TableField("platform_id")
    private Integer platformId;
    /**
     * 店长
     */
    private String director;
    /**
     * 联系电话
     */
    private String telephone;
    /**
     * 状态 1启用 ， 2停用
     */
    private Integer state;
    /**
     * 授权状态  3成功，4失败
     */
    @TableField("authorization_state")
    private Integer authorizationState;
    /**
     * 授权时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * VAT税号
     */
    private String vat;
    /**
     * 平台账号
     */
    @TableField("platform_account")
    private String platformAccount;
    /**
     * 亚马逊地址
     */
    @TableField("amazonURL")
    private String amazonURL;
    /**
     * 店铺类型
     */
    @TableField("store_type")
    private String storeType;
    /**
     * 平台后台二级域名
     */
    @TableField("subDomain")
    private String subDomain;

}
