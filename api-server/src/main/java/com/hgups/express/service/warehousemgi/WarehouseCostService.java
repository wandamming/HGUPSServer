package com.hgups.express.service.warehousemgi;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.WarehouseCost;
import com.hgups.express.mapper.WarehouseCostMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author fanc
 * 2020/9/25 0025-16:43
 */
@Service
public class WarehouseCostService extends ServiceImpl<WarehouseCostMapper,WarehouseCost> {

    @Resource
    private WarehouseCostMapper warehouseCostMapper;

    public WarehouseCost getUserWaybillPrice(double weight){
       return warehouseCostMapper.getUserWaybillPrice(weight);
    }

    public WarehouseCost getMaxPrice(){
        return warehouseCostMapper.getMaxPrice();
    }

    //获取用户zone区价格
    public double getWarehouseCostPrice(WarehouseCost warehouseCost, String zone){
        double price = 0;
        switch (zone){
            case "1":
                price=warehouseCost.getZoneOne();
                break;
            case "2":
                price=warehouseCost.getZoneTwo();
                break;
            case "3":
                price=warehouseCost.getZoneThree();
                break;
            case "4":
                price=warehouseCost.getZoneFour();
                break;
            case "5":
                price=warehouseCost.getZoneFive();
                break;
            case "6":
                price=warehouseCost.getZoneSix();
                break;
            case "7":
                price=warehouseCost.getZoneSeven();
                break;
            case "8":
                price=warehouseCost.getZoneEight();
                break;
            case "9":
                price=warehouseCost.getZoneNine();
        }
        return price;
    }
}
