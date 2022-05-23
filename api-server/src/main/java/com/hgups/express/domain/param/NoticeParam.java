package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/11/3-20:18
 */
@Data
public class NoticeParam{

    private Integer noticeType;//消息类型
    private Integer current=1;
    private Integer size=10;

}
