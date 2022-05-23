package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.City;

import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/6/8 0008-16:00
 */
public interface CityMapper extends BaseMapper<City> {

    List<Map<String ,Object>> getCityName();
}
