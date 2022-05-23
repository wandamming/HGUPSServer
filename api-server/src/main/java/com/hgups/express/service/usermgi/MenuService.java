package com.hgups.express.service.usermgi;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.Menu;
import com.hgups.express.domain.param.MenuVo;
import com.hgups.express.mapper.MenuMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2020/7/25 0025-21:46
 */
@Service
public class MenuService extends ServiceImpl<MenuMapper,Menu> {

    @Resource
    private MenuMapper menuMapper;

    //获取二级菜单
    public List<Menu> getMenuTwo(){
        return menuMapper.getMenuTwo();
    }

    public List<MenuVo> getMenuList() {
        return baseMapper.getMenuList();
    }
}
