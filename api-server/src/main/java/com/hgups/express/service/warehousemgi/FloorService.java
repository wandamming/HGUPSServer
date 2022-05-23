package com.hgups.express.service.warehousemgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.Floor;
import com.hgups.express.mapper.FloorMapper;
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
public class FloorService extends ServiceImpl<FloorMapper,Floor> {

    @Resource
    private FloorMapper floorMapper;
    @Resource
    private AreaService areaService;


    //增加修改楼层
    public Integer addUpdateFloor(Floor floor){
        Long fid = floor.getId();
        if (null!=fid && fid!=0){
            Floor floor1 = DomainCopyUtil.map(floor, Floor.class);
            Integer integer = floorMapper.updateById(floor1);
            if (integer>0){
                return 1;
            }
            return -1;
        }
        Integer insert = floorMapper.insert(floor);
        if (insert>0){
            return 2;
        }
        return -2;
    }


    //根据楼层ID删除楼层
    @Transactional
    public boolean deleteFloor(Long fid){
        //删除区域
        areaService.deleteAreaByFid(fid);
        //删除楼层
        Integer integer = floorMapper.deleteById(fid);
        return integer>0;
    }
    //根据楼号ID删除楼层
    @Transactional
    public void deleteFloorByNid(Long nid){
        EntityWrapper<Floor> wrapper = new EntityWrapper<>();
        wrapper.eq("building_no_id",nid);
        floorMapper.delete(wrapper);
    }
    //根据楼号ID获取楼层
    @Transactional
    public List<Floor> listFloorByNid(Long nid){
        EntityWrapper<Floor> wrapper = new EntityWrapper<>();
        wrapper.eq("building_no_id",nid);
        return floorMapper.selectList(wrapper);
    }

}
