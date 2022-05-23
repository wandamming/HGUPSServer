package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.Inventory;

import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/9/16 0016-16:02
 */
public interface InventoryMapper extends BaseMapper<Inventory> {

    List<Inventory> createInventoryList(Map map);

    Integer createInventoryCount(Map map);
}
