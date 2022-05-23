package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/7/14 0014-11:58
 */
@Data
@TableName(value = "port_contact")
public class PortContact implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private int id;
    private String portTitle;
    private int portState;
    private int portId;
    private String portCode;

    private String conName;
    private String conCompany;
    private String conCountries;
    private String conEprovince;
    private String conEcity;
    private String conCode;
    private String conAddressOne="";
    private String conAddressTwo;
    private String conPhone;
    private String conEmail="";
    private long userId;
    private String conPhonePrefix;
    private String conCprovince;
    private String conCcity;
    private String conCodet;

    //usps分配中心联系人
    private String uspsName;
    private String uspsCompany;
    private String uspsCountries;
    private String uspsEprovince;
    private String uspsEcity;
    private String uspsCode;
    private String uspsAddressOne="";
    private String uspsAddressTwo;
    private String uspsPhone;
    private String uspsEmail="";
    private String uspsPhonePrefix;
    private String uspsCprovince;
    private String uspsCcity;
    private String uspsCodet;

    @ApiModelProperty(value = "mid")
    @TableField(value = "mid")
    private String mid;
    @ApiModelProperty(value = "crid")
    @TableField(value = "crid")
    private String crid;

}
