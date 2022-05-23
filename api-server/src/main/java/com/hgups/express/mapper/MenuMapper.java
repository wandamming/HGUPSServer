package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.Menu;
import com.hgups.express.domain.param.MenuVo;

import java.util.List;

/**
 * @author fanc
 * 2020/7/25 0025-20:55
 */
public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> getMenuTwo();

    List<MenuVo> getMenuList();
}
