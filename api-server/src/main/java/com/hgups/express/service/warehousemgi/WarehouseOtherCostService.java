package com.hgups.express.service.warehousemgi;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.WarehouseOtherCost;
import com.hgups.express.mapper.WarehouseOtherCostMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2020/9/25 0025-16:43
 */
@Service
public class WarehouseOtherCostService extends ServiceImpl<WarehouseOtherCostMapper,WarehouseOtherCost> {

    @Resource
    private WarehouseOtherCostMapper warehouseOtherCostMapper;

    //获取日租、操作费用
    public WarehouseOtherCost getWarehouseOtherCost() {
        List<WarehouseOtherCost> incidentalList = warehouseOtherCostMapper.selectList(null);
        if (incidentalList == null || incidentalList.size()<=0) {
            WarehouseOtherCost warehouseOtherCost = new WarehouseOtherCost();
            warehouseOtherCost.setDayPrice(0);
            warehouseOtherCost.setFreeDay(0);
            warehouseOtherCost.setHandleOrderPrice(0);
            incidentalList.add(warehouseOtherCost);
        }
        return incidentalList.get(0);
    }

}
