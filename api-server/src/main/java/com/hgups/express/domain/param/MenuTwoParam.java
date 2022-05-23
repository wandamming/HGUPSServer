package com.hgups.express.domain.param;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fanc
 * 2020/8/14 0014-21:28
 */
@Data
public class MenuTwoParam implements Cloneable {
    private Integer id;
    private String name;
    private Integer pid;
    private Integer show;
    List<MenuThreeParam> child;
    @Override
    public MenuTwoParam clone(){
        MenuTwoParam menuTwoParam = new MenuTwoParam();
        try {
            menuTwoParam = (MenuTwoParam) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assert menuTwoParam != null;
        if(menuTwoParam.getChild() != null){
            List<MenuThreeParam> children = new ArrayList<>();
            for (MenuThreeParam menuThreeParam : menuTwoParam.getChild()) {
                children.add(menuThreeParam.clone());
            }
            menuTwoParam.setChild(children);
        }
        return menuTwoParam;
    }
}
