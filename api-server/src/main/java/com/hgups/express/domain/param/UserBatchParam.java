package com.hgups.express.domain.param;

import lombok.Data;

import java.util.Date;

/**
 * @author fanc
 * 2020/7/4 0004-18:34
 */
@Data
public class UserBatchParam {

    private int id;
    private String name;
    private String comment;
    private String state;
    private String isSorting;
    private String exportCity;
    private String entrySite;
    private Date createTime;

}
