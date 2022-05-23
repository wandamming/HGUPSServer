package com.hgups.express.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/18 0018-16:57
 */
@Data
public class CustomsList implements Serializable {

    private static final long serialVersionUID = 1L;
    /*
    *
    *  HGUPS清关_清单
    *
    */

    //主运单（航空主运单）
    private String mawbNumber;
    //分单 / 货运代理运单
    private String hawbNumber;
    //航空公司代码
    private String airCarrierCode;
    //航班号
    private String departingFlightNumbers;
    //出发机场
    private String departureAirport;
    //航班出发日期
    private String flightDepartureDate;
    //到达机场
    private String airportOfArrival;
    //到达日期
    private String scheduledDateOfArrival;
    //联系人
    private WaybillContact waybillContact;
    //包裹
    private Parcel parcel;
    //运单
    private WayBill wayBill;
    //物品
    private Article article;

    //件数
    private String hawbPieceCount;
    //重量
    private String hawbWeight;
    //
    private String pieceuom;
    //货物
    private String hawbDescription;
    //生产地
    private String countryOfOrigin;
    //价值
    private String hawbValue;
    //货币代码
    private String currencyCode;
    //分单单号
    private String hawbTrackingNumber;
    //
    private String bagContainerTrackingNumber;
    //最后承载人
    private String lastMileCarrier;
    //货品网址
    private String productURL;

     /*//发件人姓名
    private String senderName;
    //发件人地址1
    private String senderAddress1;
    //发件人地址2
    private String senderAddress2;
    //发件人城市
    private String senderCity;
    //发件人邮箱
    private String sednerEmail;
    //发件人省/州
    private String senderStateProvinceCode;
    //发件人国家
    private String senderCountry;*/


   /* //收件人姓名
    private String receiveName;
    //收件人地址1
    private String receiveAddress1;
    //收件人地址2
    private String receiveAddress2;
    //收件人城市
    private String receiveCity;
    //收件人邮箱
    private String receiveEmail;
    //收件人省/州
    private String receiveStateProvinceCode;
    //收件人电话
    private String receivePhoneNumber;
    //收件人国家
    private String receiveCountry;*/

}
