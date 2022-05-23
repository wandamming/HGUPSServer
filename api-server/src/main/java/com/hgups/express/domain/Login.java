package com.hgups.express.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/6/4 0004-10:15
 */
@ApiModel(value = "登录认证接口参数",description = "登录认证接口参数")
public class Login implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="用户名",name = "username",example = "admin",required = true)
    private String username;
    @ApiModelProperty(value="密码",name = "password",example = "123456",required = true)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
