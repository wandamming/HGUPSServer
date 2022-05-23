package com.hgups.express.service.usermgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.Role;
import com.hgups.express.domain.RoleMenu;
import com.hgups.express.domain.param.PointScanConfigParam;
import com.hgups.express.mapper.RoleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fanc
 * 2020/7/25 0025-21:45
 */
@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RoleMenuService roleMenuService;

    //获取有物流过点扫描权限角色
    public List<Role> getLogisticsPointScanRoles() {
        EntityWrapper<RoleMenu> wrapper = new EntityWrapper<>();
        wrapper.eq("menu_id", 89);//获取有物流过点扫描菜单的角色
        wrapper.groupBy("role_id");
        List<RoleMenu> roleMenus = roleMenuService.selectList(wrapper);
        List<Integer> ids = new ArrayList<>();
        for (RoleMenu roleMenu : roleMenus) {
            ids.add(roleMenu.getRoleId());
        }
        return roleMapper.selectBatchIds(ids);
    }

    //获取有海外仓过点扫描权限角色
    public List<Role> getWarehousePointScanRoles() {
        EntityWrapper<RoleMenu> wrapper = new EntityWrapper<>();
        wrapper.eq("menu_id", 90);//获取有物流过点扫描菜单的角色
        wrapper.groupBy("role_id");
        List<RoleMenu> roleMenus = roleMenuService.selectList(wrapper);
        List<Integer> ids = new ArrayList<>();
        for (RoleMenu roleMenu : roleMenus) {
            ids.add(roleMenu.getRoleId());
        }
        return roleMapper.selectBatchIds(ids);
    }

    /**
     * 创建过点扫描角色
     *
     * @param param
     * @return
     */
    public Role addRoleForPointScan(PointScanConfigParam param) {
        Role role = new Role();
        role.setRoleName("过点-" + param.getScanName());
        role.setIntroduction(param.getDesc());
        insert(role);
        RoleMenu roleMenu = new RoleMenu();
        roleMenu.setRoleId(role.getId());
        if (param.getScanType() != 3) {
            roleMenu.setMenuId(89);
        } else {
            roleMenu.setMenuId(90);
        }
        roleMenuService.insert(roleMenu);
        return role;
    }
}
