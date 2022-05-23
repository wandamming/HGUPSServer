package com.hgups.express.mapper;

import com.hgups.express.domain.CustomsList;

import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/6/18 0018-17:16
 */
public interface CustomsListMapper {

    List<CustomsList> getCustomsList(Map map);

}
