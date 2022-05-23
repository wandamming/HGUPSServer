package com.hgups.express.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author Seman
 * @since 2020-12-21
 */
@Data
@TableName("web_news")
public class WebNews implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 类型 0活动中心 1新闻资讯
     */
    private Integer type;
    /**
     * 排序
     */
    private Integer rank;
    /**
     * 是否可见 0不可见 1可见
     */
    private Integer visible;
    /**
     * 富文本内容
     */
    private String desc;
    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;
    /**
     * 修改时间
     */
    @TableField("modified_time")
    private Date modifiedTime;
    //标题
    @TableField("title")
    private String title;
    //图片
    @TableField("picture")
    private String picture;
}
