package com.hgups.express.service.warehousemgi;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.DhlCost;
import com.hgups.express.mapper.DhlCostMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author fanc
 * 2020/9/25 0025-16:43
 */
@Service
public class DhlCostService extends ServiceImpl<DhlCostMapper,DhlCost> {

    @Resource
    private DhlCostMapper dhlCostMapper;

    public DhlCost getUserWaybillPrice(double weight){
       return dhlCostMapper.getUserWaybillPrice(weight);
    }

    public DhlCost getMaxPrice(){
        return dhlCostMapper.getMaxPrice();
    }

    //获取用户zone区价格
    public double getDhlCostPrice(DhlCost dhlCost, String zone){
        double price = 0;
        switch (zone){
            case "1":
                price=dhlCost.getZoneOne();
                break;
            case "2":
                price=dhlCost.getZoneTwo();
                break;
            case "3":
                price=dhlCost.getZoneThree();
                break;
            case "4":
                price=dhlCost.getZoneFour();
                break;
            case "5":
                price=dhlCost.getZoneFive();
                break;
            case "6":
                price=dhlCost.getZoneSix();
                break;
            case "7":
                price=dhlCost.getZoneSeven();
                break;
            case "8":
                price=dhlCost.getZoneEight();
                break;
            case "9":
                price=dhlCost.getZoneNine();
        }
        return price;
    }
}
