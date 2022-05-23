package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.mapper.PrefectureMapper;
import com.hgups.express.domain.Prefecture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fanc
 * 2020/6/10 0010-10:39
 */
@Service
public class PrefectureService extends ServiceImpl<PrefectureMapper,Prefecture>{
    @Autowired
    private PrefectureMapper prefectureMapper;

    public List<Prefecture> getPrefecture(Integer cityId) {
        EntityWrapper<Prefecture> wrapper = new EntityWrapper<>();
        wrapper.eq("city_id",cityId);
        return prefectureMapper.selectList(wrapper);
    }
}
