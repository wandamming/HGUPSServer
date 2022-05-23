package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.Receive;
import com.hgups.express.domain.param.ReceiveParam;
import com.hgups.express.mapper.ReceiveMapper;
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
 * 2020/6/5 0005-15:50
 */
@Service
@Transactional
public class ReceiveService extends ServiceImpl<ReceiveMapper,Receive>{

    @Autowired(required = false)
    private ReceiveMapper receiveMapper;

    //id获取收件人信息
    public Receive getReceive(Integer id) {
        return receiveMapper.selectById(id);
    }

    //是否有该收件人
    public Receive isReceive(Receive receive) {
        return receiveMapper.getReceive(receive);
    }

    //收件人数量
    public int getCount() {
        Long loginUserId = ShiroUtil.getLoginUserId();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("user_id",loginUserId);
        return receiveMapper.selectCount(wrapper);
    }

    //全部收件人信息
    public List<Receive> allReceive(Map map) {
        return receiveMapper.allReceive(map);
    }

    //删除收件人信息
    public boolean deleteReceive(Integer id) {
        try {
            receiveMapper.deleteReceive(id);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //存储收件人信息
    public Integer setReceive(ReceiveParam param){
        try {
            Long loginUserId = ShiroUtil.getLoginUserId();
            Receive receive = DomainCopyUtil.map(param,Receive.class);
            receive.setUserId(loginUserId);
            if(null==receive.getPostalCodet()){
                receive.setPostalCodet("");
            }

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

            receive.setReceiveDeliveryPoint(dp);
            receive.setReceiveCarrierRoute(cr);
            receiveMapper.insert(receive);
            int receiveId = receive.getId();
            return receiveId;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    //修改收件人信息
    public boolean updateReceive(Receive receive){
        try {
            USPSApi.Address address = new USPSApi.Address();
            address.state = receive.getProvinceEname();
            address.city = receive.getCityEname();
            address.zipCode5 = receive.getPostalCode();
            address.zipCode4 = receive.getPostalCodet();
            address.address1 = receive.getAddressOne();
            address.address2 = receive.getAddressTwo();
            USPSApi.Address realAddress = USPSApi.validateAddress(address);

            if(!realAddress.isValid) {
                return  false;
            }
            String cr = realAddress.getCarrierRoute();
            String dp = realAddress.getDeliveryPoint();

            receive.setReceiveDeliveryPoint(dp);
            receive.setReceiveCarrierRoute(cr);

            receiveMapper.updateById(receive);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }


}
