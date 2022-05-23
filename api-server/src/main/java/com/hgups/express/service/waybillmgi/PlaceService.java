package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hgups.express.domain.Response;
import com.hgups.express.mapper.CityMapper;
import com.hgups.express.mapper.ProvinceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;

/**
 * @author fanc
 * 2020/6/19 0019-12:20
 */
@Service
public class PlaceService {

    @Resource
    private CityMapper cityMapper;
    @Resource
    private ProvinceMapper provinceMapper;

    @Transactional
    public boolean deleteProvince(Integer id) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("province_id", id);
        Integer delCity = cityMapper.delete(wrapper);
        Integer delProvince = provinceMapper.deleteById(id);
        if (delProvince>0) {
            return true;
        }else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }

    }
}
