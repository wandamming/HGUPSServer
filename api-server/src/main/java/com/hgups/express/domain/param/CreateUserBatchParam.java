package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/7/4 0004-18:21
 */
@Data
public class CreateUserBatchParam {

    private String name;
    private String comment;
    private String exportCity;

}
