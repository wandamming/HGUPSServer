package com.hgups.express.vo;


import lombok.Data;

import java.util.Date;

/**
 * @author lyx
 * 2021/7/20
 */
@Data
public class OperateLogVo {

    //id
    private Integer id;
    //操作时间
    private Date operateTime;
    //账号
    private String username;
    //操作模块
    private String moduleName;
    //操作记录
    private String operateRecord;
}
