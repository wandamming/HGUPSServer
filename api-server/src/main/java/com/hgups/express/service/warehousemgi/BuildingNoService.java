package com.hgups.express.service.warehousemgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.Area;
import com.hgups.express.domain.BuildingNo;
import com.hgups.express.domain.Floor;
import com.hgups.express.domain.param.BuildingNoFloorAreaParam;
import com.hgups.express.domain.param.FloorAreaParam;
import com.hgups.express.mapper.BuildingNoMapper;
import com.hgups.express.util.DomainCopyUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fanc
 * 2020/9/25 0025-16:43
 */
@Service
public class BuildingNoService extends ServiceImpl<BuildingNoMapper,BuildingNo> {

    @Resource
    private BuildingNoMapper buildingNoMapper;
    @Resource
    private FloorService floorService;
    @Resource
    private AreaService areaService;


    //增加修改楼号
    public Integer addUpdateBuildingNo(BuildingNo no){
        Long nid = no.getId();
        if (null!=nid && nid!=0){
            BuildingNo buildingNo = DomainCopyUtil.map(no, BuildingNo.class);
            Integer integer = buildingNoMapper.updateById(buildingNo);
            if (integer>0){
                return 1;
            }
            return -1;
        }
        Integer insert = buildingNoMapper.insert(no);
        if (insert>0){
            return 2;
        }
        return -2;
    }

    //删除楼号
    @Transactional
    public boolean deleteBuildingNo(Long nid){

        //删除区域
        List<Floor> floors = floorService.listFloorByNid(nid);
        for (Floor floor : floors) {
            Long fid = floor.getId();
            areaService.deleteAreaByFid(fid);
        }
        //删除楼层
        floorService.deleteFloorByNid(nid);
        //删除楼号
        Integer integer = buildingNoMapper.deleteById(nid);

        return integer>0;
    }

    //楼号列表
    public List<BuildingNo> listBuildingNo(){
        return buildingNoMapper.selectList(null);
    }


    //获取楼号、楼层、区域
    public List<BuildingNoFloorAreaParam> getAllBuildingNoFloorArea(){
        List<BuildingNo> buildingNos = buildingNoMapper.selectList(null);
        List<BuildingNoFloorAreaParam> buildingNoFloorAreaParamList = new ArrayList<>();
        for (BuildingNo buildingNo : buildingNos) {
            BuildingNoFloorAreaParam buildingNoFloorAreaParam = new BuildingNoFloorAreaParam();
            buildingNoFloorAreaParam.setBuildingNoId(buildingNo.getId());
            buildingNoFloorAreaParam.setBuildingNoName(buildingNo.getBuildingNoName());
            List<FloorAreaParam> floorAreaParams = new ArrayList<>();
            EntityWrapper<Floor> wrapper = new EntityWrapper<>();
            wrapper.eq("building_no_id",buildingNo.getId());
            List<Floor> floors = floorService.selectList(wrapper);
            for (Floor floor : floors) {
                FloorAreaParam floorAreaParam = new FloorAreaParam();
                floorAreaParam.setFloorId(floor.getId());
                floorAreaParam.setFloorName(floor.getFloorName());
                EntityWrapper<Area> wrapper1 = new EntityWrapper<>();
                wrapper1.eq("floor_id",floor.getId());
                List<Area> areas = areaService.selectList(wrapper1);
                floorAreaParam.setAreas(areas);
                floorAreaParams.add(floorAreaParam);
            }
            buildingNoFloorAreaParam.setBuildingNos(floorAreaParams);
            buildingNoFloorAreaParamList.add(buildingNoFloorAreaParam);
        }
        return buildingNoFloorAreaParamList;
    }

}
