package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.Countries;
import com.hgups.express.mapper.CountriesMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author fanc
 * 2020/6/8 0008-15:48
 */

@Service
public class CountriesService extends ServiceImpl<CountriesMapper,Countries> {

    @Resource
    private CountriesMapper countriesMapper;

    public List<Countries> getCountries(Integer countriesId) {
        EntityWrapper<Countries> wrapper = new EntityWrapper<>();
        wrapper.eq("id",countriesId);
        return countriesMapper.selectList(wrapper);
    }
}
