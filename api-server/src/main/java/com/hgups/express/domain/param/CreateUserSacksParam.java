package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/7/4 0004-16:06
 */
@Data
public class CreateUserSacksParam {

    private int userBatchId;
    private String service;
    private String comment;

}
