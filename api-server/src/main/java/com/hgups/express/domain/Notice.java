package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fanc
 * 2020/11/2-20:10
 */
@Data
@TableName(value = "notice")
public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;

    //通知内容
    @TableField("notice_content")
    private String noticeContent;

    //接收者ID
    @TableField("to_user_id")
    private Long toUserId;

    //通知时间
    @TableField("create_time")
    private Date createTime;

    //是否已接收（1：已读 2：未读）
    @TableField("look_over")
    private Integer lookOver;
    //通知类型（1：物流通知 2：任务通知 3：产品通知 4：系统通知）
    @TableField("notice_type")
    private Integer noticeType;
    //发送者ID
    @TableField("from_user_id")
    private Long fromUserId;


}
