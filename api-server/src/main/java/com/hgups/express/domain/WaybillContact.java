package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/7/2 0002-10:55
 */
@Data
@TableName(value = "waybill_contact")
public class WaybillContact implements Serializable {

    private static final long serialVersionUID = 1L;

    //发件人
    @TableId("id")
    private int id;
    @TableField("sender_name")
    private String senderName;
    @TableField("sender_company")
    private String senderCompany;
    @TableField("sender_countries")
    private String senderCountries;
    @TableField("sender_province")
    private String senderProvince;
    @TableField("sender_city")
    private String senderCity;
    @TableField("sender_postal_code")
    private String senderPostalCode;
    @TableField("sender_postal_codet")
    private String senderPostalCodet;
    @TableField("sender_address_one")
    private String senderAddressOne;
    @TableField("sender_address_two")
    private String senderAddressTwo;
    @TableField("sender_phone")
    private String senderPhone;
    @TableField("sender_phone_prefix")
    private String senderPhonePrefix;
    @TableField("sender_email")
    private String senderEmail;
    @TableField("sender_carrier_route")
    private String senderCarrierRoute;
    @TableField("sender_delivery_point")
    private String senderDeliveryPoint;


    //收件人
    private String receiveName;
    private String receiveCompany;
    private String receiveCountries;
    private String receiveProvince;
    private String receiveCity;
    private String receivePostalCode;
    private String receivePostalCodet;
    private String receiveAddressOne;
    private String receiveAddressTwo;
    private String receivePhone;
    private String receivePhonePrefix;
    private String receiveEmail;
    private String receiveCarrierRoute;
    private String receiveDeliveryPoint;

    private int wayBillId;

    private Integer senderId;
    private Integer receiveId;


}
