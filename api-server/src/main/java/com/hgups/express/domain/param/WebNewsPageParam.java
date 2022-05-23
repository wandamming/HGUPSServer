package com.hgups.express.domain.param;

import lombok.Data;

@Data
public class WebNewsPageParam extends PageParam {
    //类型 0活动中心 1新闻资讯
    private Integer type;
}
