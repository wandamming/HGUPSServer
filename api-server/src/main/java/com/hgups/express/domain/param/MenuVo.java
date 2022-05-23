package com.hgups.express.domain.param;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fanc
 * 2020/7/27 0027-17:16
 */
@Data
public class MenuVo implements Cloneable {

    private Integer id;
    private String name;
    private Integer pid;
    private Integer show;
    private List<MenuTwoParam> child;//二级菜单集合
    @Override
    public MenuVo clone(){
        MenuVo menuVo = new MenuVo();
        try {
            menuVo = (MenuVo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assert  menuVo != null;
        List<MenuTwoParam> children = new ArrayList<>();
        for (MenuTwoParam menuTwoParam : menuVo.getChild()) {
            children.add(menuTwoParam.clone());
        }
        menuVo.setChild(children);
        return menuVo;
    }
}
