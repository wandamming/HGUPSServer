package com.hgups.express.vo;

import lombok.Data;
import java.util.Date;

/**
 * @author lyx
 * 2021/7/21
 */
@Data
public class AnnouncementVo {

    //类型
    private String type;
    //标题
    private String title;
    //更新时间
    private  Date updateTime;
    //链接
    private String url;
}
