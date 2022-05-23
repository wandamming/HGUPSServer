package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/6/23 0023-18:12
 */
@Data
public class BatchWayBillParam {

    private int nid;
    //服务类型
    private String service;
    //出口城市
    private String entrySite;
    //入境地点
    private String exportCity;
    //收件人姓名
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
    private String receiveCarrierRoute;
    private String receiveDeliveryPoint;

    private double parcelBillWeight;
    private float parcelWidth;
    private float parcelLengths;
    private float parcelHeight;
    private String parcelAritcleDescribe;
    private String parcelIsCoubid;
    private String parcelIsSoft;
    private String parcelCommentOne;
    private String parcelCommentTwo;
    private String parcelShape;
    private String otherDescription;
    private String parcelItmeCategory;

    private String articleEDescribe;
    private String articleCDescribe;
    private float articlePrice;
    private float articleWeight;
    private Integer articleNumber;
    private String articlePlace;
    private String articleHsEncode;
    private String articleDeclaration;

    private int moreId;

    //是否忽略地址校验
    private Boolean checkAddress=true;

}
