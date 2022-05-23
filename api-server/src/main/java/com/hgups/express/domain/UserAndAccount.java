package com.hgups.express.domain;

import com.hgups.express.domain.param.UserRoleParam;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author fanc
 * 2020/6/15 0015-17:33
 */
@Data
public class UserAndAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String phonePrefix;
    private String phone;
    private String company;
    private String email;
    private double balance;
    private int state;
    private Date createTime;
    private Integer handleId;
    private boolean customsPrice;

    private List<UserRoleParam> roleParam;

}
