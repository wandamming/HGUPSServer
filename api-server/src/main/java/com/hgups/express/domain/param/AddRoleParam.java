package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/7/25 0025-21:36
 */
@Data
public class AddRoleParam {

    private Integer roleId;
    private String roleName;
    private String roleIntroduction;
    private List<Integer> menuIds;

}
