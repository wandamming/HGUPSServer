package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.CostBudget;
import org.apache.ibatis.annotations.Param;

/**
 * @author fanc
 * 2020/6/13 0013-15:07
 */
public interface CostBudgetMapper extends BaseMapper<CostBudget> {

    CostBudget getPriceBill(@Param("weight")double weight);

    CostBudget getMaxWeight();
}
