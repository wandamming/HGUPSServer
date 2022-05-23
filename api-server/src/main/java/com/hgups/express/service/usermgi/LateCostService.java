package com.hgups.express.service.usermgi;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.LateCost;
import com.hgups.express.mapper.LateCostMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author fanc
 * 2020/6/24 0024-16:38
 */
@Service
public class LateCostService extends ServiceImpl<LateCostMapper,LateCost> {


    @Resource
    private LateCostMapper lateCostMapper;

    //传入重量获取用户运单价格
    public LateCost getUserWaybillPrice(double weight){
        return  lateCostMapper.getLateUserWaybillPrice(weight);
    }


    public LateCost getMaxPrice(){
        return  lateCostMapper.getMaxPrice();
    }
}
