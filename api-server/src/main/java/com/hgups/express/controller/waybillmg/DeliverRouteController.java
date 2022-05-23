package com.hgups.express.controller.waybillmg;

import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.IdParam;
import com.hgups.express.domain.vo.DeliverRouteVo;
import com.hgups.express.service.waybillmgi.DeliverRouteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wandaming
 * 2021/7/21-11:06
 */

@Api(description = "发货路线API")
@Slf4j
@RestController
@RequestMapping("/DeliverRoute")
public class DeliverRouteController {

    @Resource
    private DeliverRouteService deliverRouteService;

    @ApiOperation(value = "获取发货路线列表")
    @PostMapping("getDeliverRoute")
    public Response getDeliverRoute(@RequestBody IdParam param){
        Response response = new Response();
        List<DeliverRouteVo> vo = deliverRouteService.getStoreList(param);
        response.setData(vo);
        return response;
    }


}
