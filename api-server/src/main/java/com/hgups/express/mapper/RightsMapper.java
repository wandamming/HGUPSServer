package com.hgups.express.mapper;

import com.hgups.express.domain.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author fanc
 * 2020/7/29 0029-17:53
 */
public interface RightsMapper {


    List<Menu> userRoleMenuTwo(@Param("userId")Long userId);

    List<Menu> userRoleMenuOne(@Param("userId")Long userId);

    List<Menu> userRoleMenuThree(@Param("userId")Long userId);

    List<Menu> userRoleMenuButton(@Param("userId")Long userId);

}
