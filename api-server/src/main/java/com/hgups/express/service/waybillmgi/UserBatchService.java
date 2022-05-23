package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.UserBatch;
import com.hgups.express.domain.param.CreateUserBatchParam;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.mapper.UserBatchMapper;
import com.hgups.express.util.DomainCopyUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author fanc
 * 2020/7/4 0004-18:17
 */
@Service
public class UserBatchService extends ServiceImpl<UserBatchMapper,UserBatch> {

    @Resource
    private UserBatchMapper userBatchMapper;
    @Resource
    private UserSacksService userSacksService;

    //创建用户批次,并返回批次信息
    @Transactional
    public UserBatch setUserBatch(CreateUserBatchParam param,long userId){
        UserBatch userBatch = DomainCopyUtil.map(param, UserBatch.class);
        userBatch.setUserId(userId);
        String sacksNumber = userSacksService.getSacksNumber(14);
        userBatch.setTrackingNumber(sacksNumber);
        userBatch.setCreateTime(new Date());
        Integer insert = userBatchMapper.insert(userBatch);
        if (insert>0){
            UserBatch userBatch1 = userBatchMapper.selectById(userBatch.getId());
            return userBatch1;
        }
        return null;
    }

    //获取全部批次信息（分页）
    public List<UserBatch> getAllUserBatch(PageParam param, EntityWrapper wrapper){
        Page<UserBatch> page = new Page<>(param.getCurrent(),param.getSize());
        List list = userBatchMapper.selectPage(page, wrapper);
        return list;


    }
}
