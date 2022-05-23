package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/7/27 0027-21:50
 */
@Data
public class AllRoleParam{

    private Integer roleId;//角色ID
    private String roleName;//角色名称
    private String roleIntroduction;//角色描述
    private Integer systemUser;//是否是系统用户
    private Integer permissionsSum;
    private List<MenuVo> menuOne;//一级菜单拥有的二级菜单集合
}
