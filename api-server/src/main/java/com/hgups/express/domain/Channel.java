package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/11/11-10:37
 */
@Data
@TableName(value = "channel")
public class Channel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Integer id;
    //渠道名称
    private String channelName;
    //0:显示 1：不显示
    private Integer isShow;

}
