package com.hgups.express.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author fanc
 * 2020/11/2-21:02
 */
@Data
public class NoticeVo {

    private Long fromUserId;//发送消息用户ID
    private Integer toUserId;//接收消息用户ID
    private Integer noticeType;//发送消息类型
    private String title;//通知标题
    private String orderTitle;//单号标题
    private String content;//通知内容
    private String trackNumber;//单号
    private Date createTime;//发送消息时间
    //产品通知参数
    private Integer inventoryNumber;//产品库存数量
    private Integer inventoryWarnNumber;//产品预警数量
    private String skuCode;//产品SKU编码
    private String productName;//产品名称

}
