package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.ServiceType;
import com.hgups.express.mapper.ServiceTypeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2020/7/7 0007-15:09
 */
@Service
public class ServiceTypeService extends ServiceImpl<ServiceTypeMapper,ServiceType> {

    @Resource
    private ServiceTypeMapper serviceTypeMapper;

    //获取全部服务类型（无分页）
    public List<ServiceType> getAllServiceType(){
        List<ServiceType> serviceTypes = serviceTypeMapper.selectList(null);
        return serviceTypes;
    }


}
