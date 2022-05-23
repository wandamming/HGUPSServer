package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/7/14 0014-16:43
 */
@Data
public class PortContactVo {

    private String portTitle;
    private int portState;
    private int portId;
    private String portCode;
    //仓库联系人
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

    //1：显示仓库联系人  0：不显示仓库联系人
    private int senderId;
    //1：显示usps分配联系人  0：不显示usps分配联系人
    private int uspsId;

    private String mid;
    private String crid;
}
