package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/8/8 0008-10:50
 */
@Data
public class ForgotPasswordParam{

    private String email;
    private String checkCode;
    private String newPwd;


}
