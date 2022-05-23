package com.hgups.express.mapper;

import com.hgups.express.domain.param.InventoryDetailsVo;
import org.apache.ibatis.annotations.Param;

/**
 * @author fanc
 * 2020/9/28 0028-17:31
 */
public interface InventoryDetailsMapper {

    InventoryDetailsVo getInventoryDetails(@Param("id")long id);

}
