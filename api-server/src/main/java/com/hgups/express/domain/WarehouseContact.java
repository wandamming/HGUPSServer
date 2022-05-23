package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/24 0024-16:36
 */
@Data
@TableName(value = "warehouse_contact")
public class WarehouseContact  implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;
    //发件人姓名
    private String receiveName;
    //发件人公司
    private String receiveCompany;
    //发件人国家
    private String receiveCountries    ;
    //发件人省/州
    private String receiveProvince;
    //发件人城市
    private String receiveCity;
    //发件人邮政编码一
    private String receivePostalCode;
    //发件人邮政编码二
    private String receivePostalCodet;
    //发件人地址一
    private String receiveAddressOne;
    //发件人地址二
    private String receiveAddressTwo;
    //发件人电话前缀
    private String receivePhonePrefix;
    //发件人电话
    private String receivePhone;
    //发件人邮箱
    private String receiveEmail;
    //收件人姓名
    private String senderName;
    //收件人公司
    private String senderCompany;
    //收件人国家
    private String senderCountries;
    //收件人省/州
    private String senderProvince;
    //收件人城市
    private String senderCity;
    //收件人邮政编码一
    private String senderPostalCode;
    //收件人邮政编码二
    private String senderPostalCodet;
    //收件人地址一
    private String senderAddressOne;
    //收件人地址二
    private String senderAddressTwo;
    //收件人电话前缀
    private String senderPhonePrefix;
    //收件人电话
    private String senderPhone;
    //收件人邮箱
    private String senderEmail;
    //收件人ID
    private Integer receiveId;
    //发件人ID
    private Integer senderId;


}
