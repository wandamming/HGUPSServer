package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.UserCost;
import org.apache.ibatis.annotations.Param;

/**
 * @author fanc
 * 2020/8/8 0008-14:39
 */
public interface UserCostMapper extends BaseMapper<UserCost> {
    UserCost getUserWaybillPrice(@Param("weight")double weight);

    UserCost getMaxPrice();
}
