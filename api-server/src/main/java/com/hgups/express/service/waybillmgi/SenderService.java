package com.hgups.express.service.waybillmgi;

import cn.hutool.http.HttpException;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.Sender;
import com.hgups.express.domain.param.SenderParam;
import com.hgups.express.mapper.SenderMapper;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.util.USPSApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/6/4 0004-15:43
 */
@Service
@Transactional
public class SenderService extends ServiceImpl<SenderMapper, Sender> {

    @Autowired(required = false)
    private SenderMapper senderMapper;

    //id获取发件人信息
    public Sender getSender(Integer id) {

        return senderMapper.selectById(id);
    }

    //发件人数量
    public int getCount() {
        Long loginUserId = ShiroUtil.getLoginUserId();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("user_id",loginUserId);
        return senderMapper.selectCount(wrapper);
    }

    //全部发件人信息
    public List<Sender> allSender(Map map) {
        return senderMapper.allSender(map);
    }

    //删除发件人信息
    public boolean deleteSender(Integer id) {
        try {
            senderMapper.deleteSender(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //存储发件人信息
    public Integer setSender(SenderParam param) {
        try {
            Long loginUserId = ShiroUtil.getLoginUserId();
            Sender sender = DomainCopyUtil.map(param, Sender.class);
            if(null==sender.getPostalCodet()){
                sender.setPostalCodet("");
            }
            System.out.println("---=-=>>"+param);

            USPSApi.Address address = new USPSApi.Address();
            address.state = param.getProvinceEname();
            address.city = param.getCityEname();
            address.zipCode5 = param.getPostalCode();
            address.zipCode4 = param.getPostalCodet();
            address.address1 = param.getAddressOne();
            address.address2 = param.getAddressTwo();
            USPSApi.Address realAddress = USPSApi.validateAddress(address);

            if(!realAddress.isValid) {
                return  -1;
            }
            String cr = realAddress.getCarrierRoute();
            String dp = realAddress.getDeliveryPoint();

            sender.setSenderCarrierRoute(cr);
            sender.setSenderDeliveryPoint(dp);
            sender.setUserId(loginUserId);
            senderMapper.insert(sender);
            int senderId = sender.getId();
            return senderId;
        } catch (HttpException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //修改发件人信息
    public boolean updateSender(Sender sender) {
        try {

            USPSApi.Address address = new USPSApi.Address();
            address.state = sender.getProvinceEname();
            address.city = sender.getCityEname();
            address.zipCode5 = sender.getPostalCode();
            address.zipCode4 = sender.getPostalCodet();
            address.address1 = sender.getAddressOne();
            address.address2 = sender.getAddressTwo();
            USPSApi.Address realAddress = USPSApi.validateAddress(address);

            if(!realAddress.isValid) {
                return  false;
            }
            String cr = realAddress.getCarrierRoute();
            String dp = realAddress.getDeliveryPoint();

            sender.setSenderCarrierRoute(cr);
            sender.setSenderDeliveryPoint(dp);

            senderMapper.updateById(sender);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }
}
