package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.LateCost;
import org.apache.ibatis.annotations.Param;

/**
 * @author fanc
 * 2020/6/9 0009-11:03
 */
public interface LateCostMapper extends BaseMapper<LateCost> {

    LateCost getLateUserWaybillPrice(@Param("weight")double weight);

    LateCost getMaxPrice();
}
