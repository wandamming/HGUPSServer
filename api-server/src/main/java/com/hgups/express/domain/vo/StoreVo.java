package com.hgups.express.domain.vo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author wandaming
 * 2021/7/23-17:33
 */
@Data
public class StoreVo {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String platformName;
    private String director;
    private String telephone;
    private Integer state;
    private Date createTime;

}
