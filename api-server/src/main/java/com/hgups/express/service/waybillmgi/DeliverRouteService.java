package com.hgups.express.service.waybillmgi;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.DeliverRoute;
import com.hgups.express.domain.param.IdParam;
import com.hgups.express.domain.vo.DeliverRouteVo;
import com.hgups.express.mapper.DeliverRouteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wandaming
 * 2021/7/19-11:25
 */
@Service
@Transactional
public class DeliverRouteService extends ServiceImpl<DeliverRouteMapper, DeliverRoute> {
    @Autowired(required = false)
    private DeliverRouteMapper deliverRouteMapper;

    public List<DeliverRouteVo> getStoreList(IdParam param) {

        return deliverRouteMapper.getDeliverRoute(param);
    }
}
