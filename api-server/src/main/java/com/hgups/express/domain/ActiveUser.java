package com.hgups.express.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fanc
 * 2020/6/11 0011-20:49
 */
@Data
public class ActiveUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private User user;
    private List<String> roles;
    private List<String> menus;

    public ActiveUser(User user, List<String> roles) {
        this.user = user;
        this.roles = roles;
    }
}
