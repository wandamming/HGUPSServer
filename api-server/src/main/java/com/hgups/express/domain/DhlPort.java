package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/11/14-17:58
 */
@Data
@TableName(value = "dhl_port")
@ApiModel(value = "DHL入境口岸表")
public class DhlPort  implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("id")
    private long id;

    @ApiModelProperty(value = "入境口岸名称")
    private String portName;
    @ApiModelProperty(value = "入境口岸邮政编码")
    private String zipCode;
    @ApiModelProperty(value = "0：关闭 1：开启")
    private Integer state;
    @ApiModelProperty(value = "联系人姓名")
    private String contactName;
    @ApiModelProperty(value = "公司")
    private String contactCompany;
    @ApiModelProperty(value = "省")
    private String contactProvince;
    @ApiModelProperty(value = "市")
    private String contactCity;
    @ApiModelProperty(value = "邮编5")
    private String contactCode;
    @ApiModelProperty(value = "邮编4")
    private String contactCodet;
    @ApiModelProperty(value = "地址一")
    private String contactAddressOne;
    @ApiModelProperty(value = "地址二")
    private String contactAddressTwo;
    @ApiModelProperty(value = "电话")
    private String contactPhone;
    @ApiModelProperty(value = "电话前缀")
    private String phonePrefix;
    @ApiModelProperty(value = "route")
    private String senderCarrierRoute;
    @ApiModelProperty(value = "point")
    private String senderDeliveryPoint;
    //0：未删除 1：已删除
    private Integer fakeDelete;
    private Integer sendId;
    //DHL--POCKUP
    private String dhlPickup;
    //DHL--ACCOUNT
    private String dhlAccount;
    //HGUPS入境口岸ID
    private Integer hgupsPortId;
    //配送中心
    private String distributionCenterCode;



}
