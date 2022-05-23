package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.DhlCost;
import org.apache.ibatis.annotations.Param;

/**
 * @author fanc
 * 2020/6/9 0009-11:03
 */
public interface DhlCostMapper extends BaseMapper<DhlCost> {

    DhlCost getUserWaybillPrice(@Param("weight") double weight);

    DhlCost getMaxPrice();

}
