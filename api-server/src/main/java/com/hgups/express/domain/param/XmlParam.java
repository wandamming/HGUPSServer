package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/8/5 0005-18:32
 */
@Data
public class XmlParam {

    private List<TrackDetailParam> trackDetails;//物流信息
    private String trackSummary="";//预计到达信息
    private String Description="";//错误信息
    private String wayBillState;//运单状态
    private String trackingNumber;//运单号
    private String englishTitle;//标题

}
