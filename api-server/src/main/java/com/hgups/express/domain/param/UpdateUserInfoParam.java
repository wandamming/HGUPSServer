package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/7/18 0018-16:41
 */
@Data
public class UpdateUserInfoParam {

    public String username;
    public String phone;
    public String company;
    public String email;
    public String phonePrefix;
}
