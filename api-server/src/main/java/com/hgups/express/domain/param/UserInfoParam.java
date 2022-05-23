package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/7/15 0015-12:15
 */
@Data
public class UserInfoParam {

    private Long id;
    private String username;
    private String phone;
    private String phonePrefix;
    private String company;
    private String email;

}
