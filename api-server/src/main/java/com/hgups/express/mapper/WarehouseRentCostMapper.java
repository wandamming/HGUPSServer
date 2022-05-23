package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.WarehouseRentCost;
import com.hgups.express.domain.param.WarehouseRentCostListVo;

import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/12/10-16:54
 */
public interface WarehouseRentCostMapper extends BaseMapper<WarehouseRentCost> {
    List<WarehouseRentCostListVo> warehouseRentCostList(Map map);
}
