package com.hgups.express.vo;

import com.hgups.express.domain.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * @author fanc
 * 2020/6/8 0008-20:14
 */
@Data
public class WayBillVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private WaybillContact waybillContact;
    private Receive receive;
    private Sender sender;
    private Parcel parcel;
    private WayBill wayBill;
    private List<Article> articleList;
    private String isMoreId;
    private String MoreId;
    private PortContact portContact;
    private DealDetail dealDetail;
    private Boolean checkAddress=true;
    private Integer wid;
    public WayBillVo(){

    }
    public WayBillVo(Receive receive, Sender sender, Parcel parcel, List<Article> articleList,WayBill wayBill) {
        this.receive = receive;
        this.sender = sender;
        this.parcel = parcel;
        this.articleList = articleList;
        this.wayBill = wayBill;
    }
}
