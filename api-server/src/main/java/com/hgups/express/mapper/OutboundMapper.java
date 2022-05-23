package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.Outbound;

import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/9/16 0016-16:02
 */
public interface OutboundMapper extends BaseMapper<Outbound> {

    List<Outbound> createOutboundList(Map map);

    Integer createOutboundCount(Map map);

}
