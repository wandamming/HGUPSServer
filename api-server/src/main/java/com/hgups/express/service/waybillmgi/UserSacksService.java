package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.UserSacks;
import com.hgups.express.domain.WayBill;
import com.hgups.express.domain.param.CreateUserSacksParam;
import com.hgups.express.domain.param.GetSacksWayBillParam;
import com.hgups.express.domain.param.ParamId;
import com.hgups.express.mapper.UserSacksMapper;
import com.hgups.express.mapper.WayBillMapper;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.ShiroUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2020/7/4 0004-16:02
 */
@Service
public class UserSacksService extends ServiceImpl<UserSacksMapper,UserSacks> {

    @Resource
    private UserSacksMapper userSacksMapper;
    @Resource
    private WayBillMapper wayBillMapper;


    //创建麻袋
    @Transactional
    public UserSacks setUserSacks(CreateUserSacksParam param, long loginId){
        UserSacks userSacks = DomainCopyUtil.map(param, UserSacks.class);
        String sacksNumber = userSacksMapper.getSacksNumber(14);//获取麻袋单号
        userSacks.setUserId(loginId);
        userSacks.setSacksNumber(sacksNumber);
        userSacks.setUserBatchId(param.getUserBatchId());
        Integer insert = userSacksMapper.insert(userSacks);
        if (insert>0){
            UserSacks userSacks1 = userSacksMapper.selectById(userSacks.getId());
            return userSacks1;
        }
        return null;
    }

    //根据ID获取麻袋
    public UserSacks getUserSacks(Integer sacksId){
        UserSacks userSacks = userSacksMapper.selectById(sacksId);
        return userSacks;
    }

    //获取此批次全部麻袋
    public List<UserSacks> getBatchUserSacks(ParamId param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("user_id",loginUserId);
        wrapper.eq("user_batch_id",param.getId());
        List list = userSacksMapper.selectList(wrapper);
        return list;
    }

    //获取当前麻袋里的全部运单
    public List<WayBill> getSacksWayBill(GetSacksWayBillParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id", loginUserId);
        wrapper.eq("user_sacks_id",param.getSacksId());
        wrapper.eq("user_batch_id",param.getBatchId());
        Page<WayBill> page = new Page<>(param.getCurrent(),param.getSize());
        List<WayBill> wayBills = wayBillMapper.selectPage(page, wrapper);
        return wayBills;
    }

    //获取自定义麻袋电子单号
    public String getSacksNumber(Integer sacksNumber){
       return userSacksMapper.getSacksNumber(sacksNumber);
    }


}
