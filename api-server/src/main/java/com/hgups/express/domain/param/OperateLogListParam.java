package com.hgups.express.domain.param;

import lombok.Data;

import java.util.Date;

/**
 * @author lyx
 * 2021/7/20
 */
@Data
public class OperateLogListParam extends PageParam{

    //用户账号
    private int userId;

    //操作模块
    private String moduleName;
    //创建时间开始
    private Date beginTime;
    //创建时间结束
    private Date endTime;
    //操作时间
    //private Date operateTime;
}
