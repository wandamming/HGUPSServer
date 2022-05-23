package com.hgups.express.domain.param;

import lombok.Data;

@Data
public class WebBannerParam extends PageParam{
    //banner类型 类型 0:课程 1商品
    private String type;
    //关联类型 0课包 1商品 2外链
    private String actionType;
    //banner位置
    private Integer locationId; //fromType  toType visible locationId
    //属于哪个模块(banner,公告，帮助)
    private String module;
}
