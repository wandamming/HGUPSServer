package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/7/20 0020-11:24
 */

@Data
public class UpdateUserPasswordParam {

    private String oldPwd;
    private String newPwd;


}
