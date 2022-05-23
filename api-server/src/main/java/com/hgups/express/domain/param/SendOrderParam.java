package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/9/28 0028-20:43
 */
@Data
public class SendOrderParam extends PageParam{

    //收货人
    private String sendName;
    //发货人
    private String receiveName;
    //寄送时间
    private String sendTime;
    //签收时间
    private String signTime;
    //创建时间
    private String createTime;
    //创建时间
    private Integer state;

}
