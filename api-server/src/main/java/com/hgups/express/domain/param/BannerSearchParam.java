package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author yangjb
 * @since 2019-10-03 16:07
 * <p>
 * 未注释
 */
@Data
public class BannerSearchParam extends PageParam {

    //banner类型 类型 0:课程 1商品
    private String type;
    //关联类型 0课包 1商品 2外链
    private String actionType;
    //是否可见
    private Boolean isShow;
    //banner位置
    private Integer locationId; //fromType  toType visible locationId
    //属于哪个模块(banner,公告，帮助)
    private String module;


}
