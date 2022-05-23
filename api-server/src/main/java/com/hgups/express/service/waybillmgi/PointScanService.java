package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.PointScan;
import com.hgups.express.domain.Role;
import com.hgups.express.domain.ScanRoleMiddle;
import com.hgups.express.domain.param.IdLongParam;
import com.hgups.express.domain.param.IdParam;
import com.hgups.express.domain.param.PointScanConfigParam;
import com.hgups.express.domain.param.SortPointScanParam;
import com.hgups.express.exception.MyException;
import com.hgups.express.mapper.PointScanMapper;
import com.hgups.express.service.usermgi.RightsManagementService;
import com.hgups.express.service.usermgi.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fanc
 * 2020/11/6-14:26
 */
@Service
public class PointScanService extends ServiceImpl<PointScanMapper, PointScan> {

    @Resource
    private ScanRoleMiddleService scanRoleMiddleService;//扫描点角色中间表
    @Resource
    private PointScanMapper PointScanMapper;
    @Resource
    private RoleService roleService;
    @Resource
    private RightsManagementService rightsManagementService;
    //根据用户角色获取用户拥有的物流过点扫描的权限点
    public List<PointScan> getPointScanByRoleId(Map map) {
        return PointScanMapper.getPointScanByRoleId(map);
    }


    //增加修改扫描点
    @Transactional(rollbackFor = Exception.class)
    public boolean addAndModifyPointScan(PointScanConfigParam param) {
        //扫描点ID
        Long pointScanId = param.getPointScanId();
        //扫描点名称
        String scanName = param.getScanName();
        //扫描点类型
        Integer scanType = param.getScanType() == 3 ? 3 : 2;
        //新增
        if (pointScanId == null || pointScanId == 0) {
            PointScan pointScanLast = selectOne(new EntityWrapper<PointScan>().eq("scan_type", scanType).orderBy("rank", false));
            //插入扫描点表
            PointScan pointScan = new PointScan();
            pointScan.setScanType(scanType);
            pointScan.setScanName(scanName);
            pointScan.setDesc(param.getDesc());
            pointScan.setRank(pointScanLast.getRank() + 1);
            boolean insert = insert(pointScan);
            //插入中间表
            Role role = roleService.addRoleForPointScan(param);
            ScanRoleMiddle scanRoleMiddle = new ScanRoleMiddle();
            scanRoleMiddle.setPointScanId(pointScan.getId());
            scanRoleMiddle.setScanType(scanType);
            scanRoleMiddle.setRoleId((long) role.getId());
            boolean b = scanRoleMiddleService.insert(scanRoleMiddle);
            return insert && b;
        } else {
            //修改扫描点
            PointScan pointScan = selectById(pointScanId);
            pointScan.setScanName(scanName);
            pointScan.setScanType(scanType);
            pointScan.setDesc(param.getDesc());
            return updateById(pointScan);
        }
    }

    /**
     * 过点扫描排序
     *
     * @param param
     * @return
     */
    public boolean sortPointScan(SortPointScanParam param) {
        List<PointScan> result = param.getPointScanList();
        PointScan pointScan = result.get(param.getRank());
        PointScan pointScanBefore = result.get(param.getRank() - 1);
        if (param.getRank() != 1) {
            pointScan.setScanType(pointScanBefore.getScanType());
        } else if (pointScanBefore.getScanType() == 3) {
            pointScan.setScanType(3);
        } else {
            pointScan.setScanType(1);
        }
        for (int i = 0; i < result.size(); i++) {
            result.get(i).setRank(i + 1);
        }
        return updateBatchById(result);
    }

    /**
     * 过点扫描开启、关闭
     *
     * @param id
     * @return
     * @throws MyException
     */
    public boolean openPointScan(Long id) throws MyException {
        PointScan pointScan = selectById(id);
        if (pointScan == null) {
            throw new MyException("过点扫描不存在");
        }
        pointScan.setIsOpen((pointScan.getIsOpen() + 1) % 2);
        return updateById(pointScan);
    }

    /**
     * 删除过点扫描
     *
     * @param pointScanId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePointScan(Long pointScanId) throws MyException {
        //删除中间表
        PointScan pointScan = selectById(pointScanId);
        if(pointScan == null){
            throw new MyException("该过点扫描不存在或已被删除,请刷新页面确认");
        }
        if (pointScan.getSystemType() == 1){
            throw new MyException("系统点不能删除");
        }
        pointScan.setFakeDelete(2);
        boolean a = updateById(pointScan);
        EntityWrapper<ScanRoleMiddle> wrapper = new EntityWrapper<>();
        wrapper.eq("point_scan_id", pointScanId);
        ScanRoleMiddle scanRoleMiddle = scanRoleMiddleService.selectOne(wrapper);
        boolean delete = scanRoleMiddleService.deleteById(scanRoleMiddle);

        new Thread(() -> {
            IdParam idParam = new IdParam();
            idParam.setId((int)(long)scanRoleMiddle.getRoleId());
            rightsManagementService.deleteRole(idParam);
        }).start();
        return delete && a;
    }

    /**
     * 根据类型获取完整流程
     *
     * @param pointType 1运单 2麻袋 3批次 4入库 5出库
     * @return
     */
    public List<PointScan> getCompleteProcess(Integer pointType) {
        List<PointScan> pointScanList;
        // 获取完整流程
        if (pointType == 1 || pointType == 2 || pointType == 3) {
            pointScanList = selectList(new EntityWrapper<PointScan>()
                    .in("scan_type", Arrays.asList(1, 2))
                    .eq("fake_delete", 1)
                    .eq("is_open", 1).orderBy("rank"));
            if (pointType != 1) {
                // 获取清关的rank
                Integer endRank = selectOne(new EntityWrapper<PointScan>().eq("short_name", "clearance")).getRank();
                Integer startRank;
                if (pointType == 2) {
                    // 获取核重的rank
                    startRank = selectOne(new EntityWrapper<PointScan>().eq("short_name", "nuclear")).getRank();
                } else {
                    // 获取加入批次的rank
                    startRank = selectOne(new EntityWrapper<PointScan>().eq("short_name", "batch")).getRank();
                }
                pointScanList = pointScanList.stream().filter(pointScan -> pointScan.getRank() >= startRank && pointScan.getRank() <= endRank).collect(Collectors.toList());
                if (pointType == 2) {
                    PointScan pointScan = new PointScan();
                    pointScan.setScanName("麻袋已创建");
                    pointScan.setRank(0);
                    pointScan.setId(-1L);
                    List<PointScan> pointScanListTemp = new ArrayList<>();
                    pointScanListTemp.add(pointScan);
                    pointScanListTemp.addAll(pointScanList);
                    pointScanList = pointScanListTemp;
                } else {
                    PointScan pointScan = new PointScan();
                    pointScan.setScanName("批次已创建");
                    pointScan.setRank(0);
                    pointScan.setId(-1L);
                    List<PointScan> pointScanListTemp = new ArrayList<>();
                    pointScanListTemp.add(pointScan);
                    pointScanListTemp.addAll(pointScanList);
                    pointScanList = pointScanListTemp;
                }
                pointScanList.forEach(pointScan -> {
                    if (StringUtils.isNotBlank(pointScan.getShortName())){
                        if (pointScan.getShortName().equals("sack")){
                            pointScan.setScanName("关闭麻袋");
                        }
                        if (pointType == 3 && pointScan.getShortName().equals("batch")){
                            pointScan.setScanName("关闭批次");
                        }
                    }
                });
            }
        } else {
            pointScanList = selectList(new EntityWrapper<PointScan>()
                    .eq("scan_type", 3)
                    .eq("fake_delete", 1)
                    .eq("is_open", 1).orderBy("rank"));
            // 数据库必有,无需验证
            PointScan createOutbound = pointScanList.stream().filter(pointScan1 -> pointScan1.getShortName().equals("createOutbound")).findFirst().get();
            // 出入库完整流程分离
            if (pointType == 4) { // 入库
                pointScanList = pointScanList.stream().filter(pointScan1 -> pointScan1.getRank() < createOutbound.getRank()).collect(Collectors.toList());
            } else {
                pointScanList = pointScanList.stream().filter(pointScan1 -> pointScan1.getRank() >= createOutbound.getRank()).collect(Collectors.toList());
            }
        }
        return pointScanList;
    }
}
