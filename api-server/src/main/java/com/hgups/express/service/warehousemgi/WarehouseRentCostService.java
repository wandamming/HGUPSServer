package com.hgups.express.service.warehousemgi;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.WarehouseRentCost;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.domain.param.WarehouseRentCostListVo;
import com.hgups.express.mapper.WarehouseRentCostMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/12/10-16:55
 */
@Service
public class WarehouseRentCostService extends ServiceImpl<WarehouseRentCostMapper,WarehouseRentCost> {

    @Resource
    private WarehouseRentCostMapper warehouseRentCostMapper;


    //仓租收费列表
    public List<WarehouseRentCostListVo> warehouseRentCostList(PageParam pageParam){
        Map<String,Object> map = new HashMap<>();
        map.put("current",(pageParam.getCurrent()-1)*pageParam.getSize());
        map.put("size",pageParam.getSize());
        return warehouseRentCostMapper.warehouseRentCostList(map);
    }
}
