package com.hgups.express.service.usermgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.domain.Menu;
import com.hgups.express.domain.Role;
import com.hgups.express.domain.RoleMenu;
import com.hgups.express.domain.UserRole;
import com.hgups.express.domain.param.*;
import com.hgups.express.mapper.RightsMapper;
import com.hgups.express.util.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fanc
 * 2020/7/25 0025-20:20
 */

@Slf4j
@Service
public class RightsManagementService {

    public static final int ROLE_SUPER_ADMIN = 1;
    @Resource
    private RoleService roleService;
    @Resource
    private RoleMenuService roleMenuService;
    @Resource
    private MenuService menuService;
    @Resource
    private RightsMapper rightsMapper;
    @Resource
    private UserRoleService userRoleService;

    public static final int PROCESS_ID = 4;
    private final int SUPER_ID = 1;

    //获取当前用户的角色信息
    public List<MenuVo> userRoleMenu(Long userId) {
        log.info("获取当前用户角色信息——————");
        //当前用户拥有的一级菜单
        List<Menu> menusOnes = rightsMapper.userRoleMenuOne(userId);
        //当前用户拥有的二级菜单
        List<Menu> menuTwos = rightsMapper.userRoleMenuTwo(userId);
        //当前用户拥有的三级菜单
        List<Menu> menuThrees = rightsMapper.userRoleMenuThree(userId);
        //当前用户拥有的按钮
        List<Menu> menuButtons = rightsMapper.userRoleMenuButton(userId);

        List<MenuVo> menuVoList = new ArrayList<>();
        for (Menu menuOne : menusOnes) {
            MenuVo menuVo = new MenuVo();
            List<MenuTwoParam> child = new ArrayList<>();
            Integer menuOneId = menuOne.getId();
            for (Menu menuTwo : menuTwos) {
                MenuTwoParam menuTwoParam = new MenuTwoParam();
                if (menuOneId.equals(menuTwo.getPid())) {
                    List<MenuThreeParam> menuThreeParams = new ArrayList<>();
                    for (Menu menuThree : menuThrees) {
                        MenuThreeParam menuThreeParam = new MenuThreeParam();
                        if (menuThree.getPid().equals(menuTwo.getId())) {
                            List<Menu> menus = new ArrayList<>();
                            for (Menu menuButton : menuButtons) {
                                Menu menu = new Menu();
                                if (menuThree.getId() == menuButton.getPid()) {
                                    menu.setName(menuButton.getName());
                                    menu.setId(menuButton.getId());
                                    menu.setShow(1);
                                    menus.add(menu);
                                }
                            }
                            menuThreeParam.setName(menuThree.getName());
                            menuThreeParam.setId(menuThree.getId());
                            menuThreeParam.setShow(1);
                            menuThreeParam.setChild(menus);
                            menuThreeParams.add(menuThreeParam);
                        }
                    }
                    menuTwoParam.setChild(menuThreeParams);
                    menuTwoParam.setName(menuTwo.getName());
                    menuTwoParam.setId(menuTwo.getId());
                    menuTwoParam.setShow(1);
                    child.add(menuTwoParam);
                }
            }
            menuVo.setShow(1);
            menuVo.setName(menuOne.getName());
            menuVo.setId(menuOneId);
            menuVo.setChild(child);
            menuVoList.add(menuVo);
        }
        log.info("返回当前用户角色信息——————");
        return menuVoList;
    }

    //获取全部角色信息及权限
    public List<AllRoleParam> allRoleAndMenu(PageParam param) {
        Page<Role> page = new Page<>(param.getCurrent(), param.getSize());
        Page<Role> page1 = roleService.selectPage(page);
        List<Role> roles = page1.getRecords();
        List<AllRoleParam> allRoleParamList = new ArrayList<>();
        List<MenuVo> menuVosTemp = getMenu();//两级菜单
        List<RoleMenu> allRoleMenus = roleMenuService.selectList(new EntityWrapper<RoleMenu>().in("role_id", roles.stream().map(Role::getId).collect(Collectors.toList())));
        for (Role role : roles) {
            int permissionsSum = 0;
            List<MenuVo> menuVos = getMenuCopy(menuVosTemp);//两级菜单
            List<MenuVo> menuVoList = new ArrayList<>();//两级菜单
            AllRoleParam allRoleParam = new AllRoleParam();//角色及菜单
            int rid = role.getId();//角色ID
            //角色所拥有的权限
//            EntityWrapper<RoleMenu> wrapper = new EntityWrapper<>();
//            wrapper.eq("role_id", rid);
//            List<RoleMenu> roleMenus = roleMenuService.selectList(wrapper);
            List<RoleMenu> roleMenus = allRoleMenus.stream().filter(allRoleMenu -> allRoleMenu.getRoleId() == rid).collect(Collectors.toList());
            for (MenuVo menuVo : menuVos) {
                boolean flag = false;
                Integer menuOneId = menuVo.getId();//一级菜单ID
                for (RoleMenu roleMenu : roleMenus) {
                    if (menuOneId == roleMenu.getMenuId()) {
                        permissionsSum++;
                        flag = true;
                    }
                }
                if (flag) {
                    menuVo.setShow(1);
                } else {
                    menuVo.setShow(0);
                }
                List<MenuTwoParam> child = menuVo.getChild();
                boolean flag1 = false;
                for (MenuTwoParam menu3 : child) {
                    for (RoleMenu roleMenu : roleMenus) {
                        if (menu3.getId() == roleMenu.getMenuId()) {
                            permissionsSum++;
                            flag1 = true;
                        }
                    }
                    if (flag1) {
                        menu3.setShow(1);
                    } else {
                        menu3.setShow(0);
                    }
                    flag1 = false;
                    if (menu3.getChild() == null) {
                        continue;
                    }
                    boolean flag2 = false;
                    for (MenuThreeParam menuThreeParam : menu3.getChild()) {
                        for (RoleMenu roleMenu : roleMenus) {
                            if (menuThreeParam.getId() == roleMenu.getMenuId()) {
                                permissionsSum++;
                                flag2 = true;
                            }
                        }
                        if (flag2) {
                            menuThreeParam.setShow(1);
                        } else {
                            menuThreeParam.setShow(0);
                        }
                        flag2 = false;
                        boolean flag3 = false;
                        List<Menu> menuButtons = menuThreeParam.getChild();
                        if (menuButtons == null) {
                            continue;
                        }
                        for (Menu menu : menuButtons) {
                            for (RoleMenu roleMenu : roleMenus) {
                                if (menu.getId() == roleMenu.getMenuId()) {
                                    permissionsSum++;
                                    flag3 = true;
                                }
                            }
                            if (flag3) {
                                menu.setShow(1);
                            } else {
                                menu.setShow(0);
                            }
                            flag3 = false;
                        }
                    }
                }
                menuVoList.add(menuVo);
            }

            allRoleParam.setRoleId(role.getId());
            if (ROLE_SUPER_ADMIN == role.getId()) {
                allRoleParam.setSystemUser(1);
            } else {
                allRoleParam.setSystemUser(0);
            }
            allRoleParam.setRoleName(role.getRoleName());
            allRoleParam.setRoleIntroduction(role.getIntroduction());
            allRoleParam.setMenuOne(menuVoList);
            allRoleParamList.add(allRoleParam);
            allRoleParam.setPermissionsSum(permissionsSum);
        }
        return allRoleParamList;
    }

    public List<MenuVo> getMenuCopy(List<MenuVo> menuVos) {
        List<MenuVo> result = new ArrayList<>();
        for (MenuVo menuVo : menuVos) {
            result.add(menuVo.clone());
        }

        return result;
    }

    //菜单显示
    public List<MenuVo> getMenu() {
        List<MenuVo> menuVos = menuService.getMenuList();
        for (MenuVo menuVo : menuVos) {
            for (MenuTwoParam menuTwoParam : menuVo.getChild()) {
                if (menuTwoParam.getChild().size() == 0) {
                    menuTwoParam.setChild(null);
                }
            }
        }
        return menuVos;

//        ShiroUtil.getLoginUserId();
//        EntityWrapper<Menu> wrapper1 = new EntityWrapper<>();
//        wrapper1.eq("pid",0);
//        List<Menu> menus = menuService.selectList(wrapper1);
//        List<MenuVo> menuVos = new ArrayList<>();
//        for (Menu menu:menus){
//            MenuVo menuVo = new MenuVo();
//            menuVo.setId(menu.getId());
//            menuVo.setName(menu.getName());
//            menuVo.setPid(menu.getPid());
//            menuVo.setShow(menu.getShow());
//            EntityWrapper<Menu> wrapper2 = new EntityWrapper<>();
//            wrapper2.eq("pid",menu.getId());
//            List<Menu> menusLowers = menuService.selectList(wrapper2);
//            List<MenuTwoParam> menuTwoParams = new ArrayList<>();
//            for (Menu menu1:menusLowers){
//                MenuTwoParam menuTwoParam = new MenuTwoParam();
//                menuTwoParam.setId(menu1.getId());
//                menuTwoParam.setName(menu1.getName());
//                menuTwoParam.setShow(menu1.getShow());
//                menuTwoParam.setPid(menu1.getPid());
//                EntityWrapper<Menu> wrapper3 = new EntityWrapper<>();
//                wrapper3.eq("pid",menu1.getId());
//                List<Menu> menusLowers3 = menuService.selectList(wrapper3);
//                if (menusLowers3.size()==0){
//                    menuTwoParams.add(menuTwoParam);
//                    continue;
//                }
//                List<MenuThreeParam> menuThreeParams = new ArrayList<>();
//                for (Menu menu2:menusLowers3){
//                    MenuThreeParam menuThreeParam = new MenuThreeParam();
//                    menuThreeParam.setId(menu2.getId());
//                    menuThreeParam.setName(menu2.getName());
//                    menuThreeParam.setShow(menu2.getShow());
//                    menuThreeParam.setPid(menu2.getPid());
//                    System.out.println("------三级菜单----"+menuThreeParam);
//                    EntityWrapper<Menu> wrapper4 = new EntityWrapper<>();
//                    wrapper4.eq("pid",menu2.getId());
//                    List<Menu> menusLowers4 = menuService.selectList(wrapper4);
//                    if (menusLowers4.size()==0){
//                        menuThreeParams.add(menuThreeParam);
//                        continue;
//                    }
//                    menuThreeParam.setChild(menusLowers4);
//                    menuThreeParams.add(menuThreeParam);
//                    System.out.println("按钮==========="+menusLowers4);
//                }
//                menuTwoParam.setChild(menuThreeParams);
//                menuTwoParams.add(menuTwoParam);
//            }
//            System.out.println("-------二级菜单列表======"+menuTwoParams);
//            menuVo.setChild(menuTwoParams);
//            menuVos.add(menuVo);
//        }
//        System.out.println(menuVos);
//        return menuVos;
    }


    //删除角色
    @Transactional
    public Integer deleteRole(IdParam param) {
        ShiroUtil.getLoginUserId();
        int rid = param.getId();
        Role role = roleService.selectById(rid);
        if (null == role) {
            return 0;
        }
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("role_id", rid);
        boolean deleteRoleMenu = roleMenuService.delete(wrapper);
        boolean deleteRole = roleService.deleteById(rid);
        boolean delete = userRoleService.delete(wrapper);
        if (deleteRole && deleteRoleMenu && delete) {
            return 1;
        }
        return -1;
    }

    //添加修改角色
    @Transactional
    public Integer addRole(AddRoleParam param) {
        ShiroUtil.getLoginUserId();
        List<Integer> menuIds = param.getMenuIds();
        String roleIntroduction = param.getRoleIntroduction();//角色描述
        String roleName = param.getRoleName();//角色名称
        Integer roleId = param.getRoleId();
        Role role = roleService.selectById(roleId);
        boolean flag1 = true;
        boolean flag2 = false;
        try {
            if (null == role) {
                Role ro = new Role();
                ro.setIntroduction(roleIntroduction);
                ro.setRoleName(roleName);
                flag2 = roleService.insert(ro);
                for (Integer menuId : menuIds) {
                    RoleMenu roleMenu = new RoleMenu();
                    roleMenu.setMenuId(menuId);
                    roleMenu.setRoleId(ro.getId());
                    boolean insert = roleMenuService.insert(roleMenu);
                    if (!insert) {
                        flag1 = false;
                        break;
                    }
                }
                if (flag1 && flag2) {
                    return 1;
                }
                return -1;
            } else {

                EntityWrapper<RoleMenu> wrapper = new EntityWrapper();
                wrapper.eq("role_id", roleId);
                roleMenuService.delete(wrapper);

                role.setIntroduction(roleIntroduction);
                role.setRoleName(roleName);
                flag2 = roleService.updateById(role);
                for (Integer menuId : menuIds) {
                    RoleMenu roleMenu = new RoleMenu();
                    roleMenu.setMenuId(menuId);
                    roleMenu.setRoleId(roleId);
                    boolean insert = roleMenuService.insert(roleMenu);
                    if (!insert) {
                        flag1 = false;
                        break;
                    }
                }
                if (flag1 && flag2) {
                    return 2;
                }
                return -2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    //判断当前用户角色是否有后程角色
    public int isProcessRole(long userId) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("user_id", userId);
        wrapper.eq("role_id", PROCESS_ID);
        UserRole userRole = userRoleService.selectOne(wrapper);
        if (null == userRole) {
            return -1;//不是后程用户
        }
        return 1;//是后程用户
    }

    public List<Integer> getLateUserIds() {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("role_id", PROCESS_ID);
        wrapper.setSqlSelect("user_id");
        List<Integer> userIds = userRoleService.selectObjs(wrapper);
        log.info(" getLateUserIds user_id: " + userIds);
        return userIds;
    }

    //判断当前用户角色是否有超级管理员角色
    public int isSuperRole(long userId) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("user_id", userId);
        wrapper.eq("role_id", SUPER_ID);
        UserRole userRole = userRoleService.selectOne(wrapper);
        if (null == userRole) {
            return -1;//不是超级用户
        }
        return 1;//是超级用户
    }


}
