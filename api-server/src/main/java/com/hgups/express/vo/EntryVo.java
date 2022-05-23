package com.hgups.express.vo;

import lombok.Data;
/**
 * @author lyx
 * 2021/7/21
 */
@Data
public class EntryVo {

    //入口名称
    private String name;
    //图标
    private String icon;
    //跳转链接
    private String path;
}
