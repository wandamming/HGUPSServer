package com.hgups.express.service.warehousemgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.Area;
import com.hgups.express.mapper.AreaMapper;
import com.hgups.express.util.DomainCopyUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2020/9/25 0025-16:43
 */
@Service
public class AreaService extends ServiceImpl<AreaMapper,Area> {

    @Resource
    private AreaMapper areaMapper;


    //增加修改楼层
    public Integer addUpdateFloor(Area area){
        Long aid = area.getId();
        if (null!=aid && aid!=0){
            Area area1 = DomainCopyUtil.map(area, Area.class);
            Integer integer = areaMapper.updateById(area1);
            if (integer>0){
                return 1;
            }
            return -1;
        }
        Integer insert = areaMapper.insert(area);
        if (insert>0){
            return 2;
        }
        return -2;
    }


    //根据区域ID删除
    @Transactional
    public boolean deleteArea(Long aid){
        Integer integer = areaMapper.deleteById(aid);
        return integer>0;
    }
    //根据楼层ID删除
    @Transactional
    public void deleteAreaByFid(Long fid){
        EntityWrapper<Area> wrapper = new EntityWrapper<>();
        wrapper.eq("floor_id",fid);
        Integer integer = areaMapper.delete(wrapper);
    }

    //楼号列表
    public List<Area> listArea(Long fid){
        EntityWrapper<Area> wrapper = new EntityWrapper<>();
        wrapper.eq("floor_id",fid);
        return areaMapper.selectList(wrapper);
    }

}
