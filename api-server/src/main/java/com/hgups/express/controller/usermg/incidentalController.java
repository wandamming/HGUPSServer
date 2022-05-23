package com.hgups.express.controller.usermg;

import com.hgups.express.domain.Incidental;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.IncidentalParam;
import com.hgups.express.service.usermgi.IncidentalService;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2020/6/10 0010-22:52
 */
@Api(description = "后程用户海关及其他费用接口API")
@Slf4j
@RestController
@RequestMapping("/incidental")
public class incidentalController {

    @Resource
    private IncidentalService incidentalService;

    @ApiOperation(value = "获取后程用户其他费用（海关及其他）")
    @PostMapping("/getIncidental")
    public Response getIncidental() {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        List<Incidental> incidentalList = incidentalService.selectList(null);
        if (incidentalList == null || incidentalList.size()<=0) {
            Incidental incidental = new Incidental();
            incidental.setCustomsPrice(0);
            incidental.setReservedOne(0);
            incidental.setReservedTwo(0);
            incidental.setReservedThree(0);
            incidentalList.add(incidental);
        }
        List<IncidentalParam> incidentalParams = DomainCopyUtil.mapList(incidentalList, IncidentalParam.class);
        response.setData(incidentalParams);
        return response;
    }


    @ApiOperation(value = "修改后程用户其他费用（海关及其他）")
    @PostMapping("/addUpdateIncidental")
    public Response addUpdateIncidental(@RequestBody IncidentalParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        Incidental incidental = DomainCopyUtil.map(param, Incidental.class);
        List<Incidental> incidentalList = incidentalService.selectList(null);

        if (null == incidentalList || incidentalList.size()<=0) {
            boolean insert = incidentalService.insert(incidental);
            if (insert) {
                response.setStatusCode(200);
                response.setMsg("修改成功");
                return response;
            }
            response.setStatusCode(202);
            response.setMsg("修改失败");
            return response;
        }


        Incidental incidental1 = incidentalList.get(0);
        incidental1.setCustomsPrice(param.getCustomsPrice());
        incidental1.setReservedOne(param.getReservedOne());
        incidental1.setReservedTwo(param.getReservedTwo());
        incidental1.setReservedThree(param.getReservedThree());
        boolean b = incidentalService.updateById(incidental1);
        if (b) {
            response.setStatusCode(200);
            response.setMsg("修改成功");
            return response;
        }

        response.setStatusCode(202);
        response.setMsg("修改失败");
        return response;
    }

}
