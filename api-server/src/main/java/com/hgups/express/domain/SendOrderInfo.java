package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanc
 * 2020/6/11 0011-20:49
 */
@Data
@TableName(value = "send_order_info")
public class SendOrderInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;
    //中文名称
    private String cName;
    //英文名称
    private String eName;
    //数量
    private String number;
    //价值
    private String worth;
    //体积重（磅）
    private String volume;
    //重量
    private String weight;
    //保险金额
    private String insurancePrice;
    //发货方式
    private String deliveryType;
    //运费
    private String freight;
    //备注
    private String comment;

    //寄送状态（1：待寄送 2：寄送中 3：已签收）
    private Integer state;

    //收货人
    private String receiveName;
    //收货人电话
    private String receivePhone;
    //收货人电话二
    private String receivePhoneTwo;
    //收货人地址
    private String receiveAddress;
    //发货人
    private String sendName;
    //发货人电话一
    private String sendPhoneOne;
    //发货人地址
    private String sendAddress;


    //图片地址
    private String imgUrl;
    //寄送时间
    private Date sendTime;
    //签收时间
    private Date signTime;
    //创建时间
    private Date createTime;

}
