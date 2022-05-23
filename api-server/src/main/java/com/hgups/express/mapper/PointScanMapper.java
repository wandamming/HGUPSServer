package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.PointScan;

import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/11/6-14:23
 */
public interface PointScanMapper extends BaseMapper<PointScan> {

    //根据用户角色获取用户拥有的物流过点扫描的权限点
    List<PointScan> getPointScanByRoleId(Map map);

}
