package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.CostBudget;
import com.hgups.express.mapper.CostBudgetMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author fanc
 * 2020/6/13 0013-15:15
 */
@Service
public class CostBudgetService extends ServiceImpl<CostBudgetMapper,CostBudget> {

    @Resource
    private CostBudgetMapper costBudgetMapper;


    public CostBudget getPriceBill(double weight){
        CostBudget costBudget = costBudgetMapper.getPriceBill(weight);
        if(null!=costBudget){
            return costBudget;
        }
        return null;
    }

    public CostBudget getMaxWeight(){
        return  costBudgetMapper.getMaxWeight();
    }
}
