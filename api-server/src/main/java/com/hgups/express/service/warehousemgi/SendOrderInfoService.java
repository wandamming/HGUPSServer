package com.hgups.express.service.warehousemgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.SendOrderInfo;
import com.hgups.express.domain.param.SendOrderParam;
import com.hgups.express.mapper.SendOrderInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author fanc
 * 2020/9/28 0028-17:11
 */
@Service
public class SendOrderInfoService extends ServiceImpl<SendOrderInfoMapper,SendOrderInfo> {

    @Resource
    private SendOrderInfoMapper sendOrderInfoMapper;

    //寄送订单列表
    public List<SendOrderInfo> sendOrderList(SendOrderParam param){
        Page<SendOrderInfo> page = new Page<>(param.getCurrent(),param.getSize());
        EntityWrapper<SendOrderInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("state",param.getState());

        if (!StringUtils.isEmpty(param.getSendName())){
            wrapper.eq("send_name",param.getSendName());
        }
        if (!StringUtils.isEmpty(param.getReceiveName())){
            wrapper.eq("receive_name",param.getReceiveName());
        }
        boolean flag = true;
        if (!StringUtils.isEmpty(param.getCreateTime())){
            wrapper.ge("create_time",param.getCreateTime());
            wrapper.orderBy("create_time",true);
            flag = false;
        }
        if (!StringUtils.isEmpty(param.getSendName())){
            wrapper.ge("send_time",param.getSendTime());
            wrapper.orderBy("send_time",true);
            flag = false;
        }
        if (!StringUtils.isEmpty(param.getSignTime())){
            wrapper.ge("sign_time",param.getSignTime());
            wrapper.orderBy("sign_time",true);
            flag = false;
        }
        if (flag){
            wrapper.orderBy("create_time",false);
        }
        return sendOrderInfoMapper.selectPage(page,wrapper);
    }

    //寄送订单总数
    public int sendOrderListCount(SendOrderParam param){
        EntityWrapper<SendOrderInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("state",param.getState());

        if (!StringUtils.isEmpty(param.getSendName())){
            wrapper.eq("send_name",param.getSendName());
        }
        if (!StringUtils.isEmpty(param.getReceiveName())){
            wrapper.eq("receive_name",param.getReceiveName());
        }
        boolean flag = true;
        if (!StringUtils.isEmpty(param.getCreateTime())){
            wrapper.ge("create_time",param.getCreateTime());
            wrapper.orderBy("create_time",true);
            flag = false;
        }
        if (!StringUtils.isEmpty(param.getSendName())){
            wrapper.ge("send_time",param.getSendTime());
            wrapper.orderBy("send_time",true);
            flag = false;
        }
        if (!StringUtils.isEmpty(param.getSignTime())){
            wrapper.ge("sign_time",param.getSignTime());
            wrapper.orderBy("sign_time",true);
            flag = false;
        }
        if (flag){
            wrapper.orderBy("create_time",false);
        }
        return sendOrderInfoMapper.selectCount(wrapper);
    }



    //批量删除待寄送订单
    public void deleteSendOrder(List<Long> ids){
        sendOrderInfoMapper.deleteBatchIds(ids);
    }


    //提交订单信息
    public boolean addSendOrder(SendOrderInfo sendOrderInfo){
        if (null!=sendOrderInfo){
            Long id = sendOrderInfo.getId();
            if (null==id||0==id){
                sendOrderInfo.setSendTime(new Date());
                sendOrderInfo.setCreateTime(new Date());
                sendOrderInfo.setState(2);
                Integer insert = sendOrderInfoMapper.insert(sendOrderInfo);
                return insert>0;
            }else {
                SendOrderInfo sendOrderInfo1 = sendOrderInfoMapper.selectById(id);
                sendOrderInfo.setImgUrl(sendOrderInfo1.getImgUrl());
                sendOrderInfo.setCreateTime(sendOrderInfo1.getCreateTime());
                sendOrderInfo.setSendTime(new Date());
                sendOrderInfo.setState(2);
                Integer integer = sendOrderInfoMapper.updateById(sendOrderInfo);
                return integer>0;
            }
        }
        return false;
    }

    //改变订单状态为已签收
    public boolean updateSendOrderState(List<Long> ids){
        if (null != ids){
            for (Long id : ids) {
                SendOrderInfo sendOrderInfo = sendOrderInfoMapper.selectById(id);
                sendOrderInfo.setState(3);
                sendOrderInfo.setSignTime(new Date());
                sendOrderInfoMapper.updateById(sendOrderInfo);
            }
            return true;
        }
        return false;
    }


}
