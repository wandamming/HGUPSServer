package com.hgups.express.service.usermgi;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.UserCost;
import com.hgups.express.mapper.UserCostMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author fanc
 * 2020/8/8 0008-14:40
 */
@Service
public class UserCostService extends ServiceImpl<UserCostMapper,UserCost> {

    @Resource
    private UserCostMapper userCostMapper;

    //传入重量获取用户运单价格
    public UserCost getUserWaybillPrice(double weight){
        return  userCostMapper.getUserWaybillPrice(weight);
    }


    public UserCost getMaxPrice(){
        return  userCostMapper.getMaxPrice();
    }

}
