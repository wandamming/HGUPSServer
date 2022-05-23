package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.WarehouseCost;
import org.apache.ibatis.annotations.Param;

/**
 * @author fanc
 * 2020/6/9 0009-11:03
 */
public interface WarehouseCostMapper extends BaseMapper<WarehouseCost> {

    WarehouseCost getUserWaybillPrice(@Param("weight")double weight);

    WarehouseCost getMaxPrice();

}
