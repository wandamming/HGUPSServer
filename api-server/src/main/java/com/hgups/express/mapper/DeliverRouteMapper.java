package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.DeliverRoute;
import com.hgups.express.domain.param.IdParam;
import com.hgups.express.domain.vo.DeliverRouteVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wdm
 * @since 2021-07-27
 */
public interface DeliverRouteMapper extends BaseMapper<DeliverRoute> {
    List<DeliverRouteVo> getDeliverRoute(IdParam param);

}
