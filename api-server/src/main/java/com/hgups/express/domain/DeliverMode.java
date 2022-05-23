package com.hgups.express.domain;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wdm
 * @since 2021-07-29
 */
@TableName("deliver_mode")
public class DeliverMode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 配送方式
     */
    private byte[] Mode;
    /**
     * 0隐藏1显示
     */
    private Boolean isshow;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getMode() {
        return Mode;
    }

    public void setMode(byte[] Mode) {
        this.Mode = Mode;
    }

    public Boolean getIsshow() {
        return isshow;
    }

    public void setIsshow(Boolean isshow) {
        this.isshow = isshow;
    }

    @Override
    public String toString() {
        return "DeliverMode{" +
        ", id=" + id +
        ", Mode=" + Mode +
        ", isshow=" + isshow +
        "}";
    }
}
