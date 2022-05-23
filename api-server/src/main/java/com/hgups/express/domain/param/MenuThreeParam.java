package com.hgups.express.domain.param;

import com.hgups.express.domain.Menu;
import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/8/14 0014-21:27
 */
@Data
public class MenuThreeParam implements Cloneable{
    private Integer id;
    private String name;
    private Integer pid;
    private Integer show;
    List<Menu> child;
    @Override
    public MenuThreeParam clone(){
        MenuThreeParam menuThreeParam = new MenuThreeParam();
        try {
            menuThreeParam = (MenuThreeParam) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return menuThreeParam;
    }
}
