package com.hgups.express.domain.param;

import com.hgups.express.domain.Notice;
import lombok.Data;

/**
 * @author fanc
 * 2020/11/3-21:37
 */
@Data
public class NewNoticeVo {
    //1：物流通知 2：任务通知 3：产品通知 4：系统通知
    private Integer noticeType;
    private Integer noRead;//未读条数
    private Notice notice;//通知消息类
}
