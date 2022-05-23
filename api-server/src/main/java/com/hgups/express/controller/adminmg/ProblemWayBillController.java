package com.hgups.express.controller.adminmg;

import com.hgups.express.domain.Response;
import com.hgups.express.domain.WayBill;
import com.hgups.express.domain.param.ProblemWayBillParam;
import com.hgups.express.service.usermgi.RightsManagementService;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/7/31 0031-13:56
 */
@Api(description = "问题运单列表API")
@RestController
@Slf4j
@RequestMapping("/ProblemWayBill")
public class ProblemWayBillController {

    @Resource
    private WayBillService wayBillService;
    @Resource
    private RightsManagementService rightsManagementService;

    @ApiOperation(value = "创建航运批次API")
    @PostMapping("/getProblemWayBill")
    public Response getProblemWayBill(@RequestBody ProblemWayBillParam param ){
        Long loginUserId = ShiroUtil.getLoginUserId();
        int processRole = rightsManagementService.isProcessRole(loginUserId);
        //是否是后程用户
        if (processRole==1){
            param.setProcessUserId(loginUserId);
        }
        int superRole = rightsManagementService.isSuperRole(loginUserId);
        //是否是超级用户
        if (superRole==1){
            param.setProcessUserId(null);
        }
        Response response = new Response();
        param.setCurrent((param.getCurrent()-1)*param.getSize());//起始页
        List<String> trackingNumbers = param.getTrackingNumbers();
        if (null!=trackingNumbers&&trackingNumbers.size()>0&&(!"".equals(trackingNumbers.get(0)))){ //判断是否需要根据单号查询
            param.setIsTrackingNumber("1");
            System.out.println("有运单号-------");
        }
        List<WayBill> problemWayBill = wayBillService.getProblemWayBill(param);
        Integer total = wayBillService.getProblemWayBillCount(param);
        Map<Object,Object> map = new HashMap<>();
        map.put("current",param.getCurrent()+1);
        map.put("total",total);
        map.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);//总页数
        map.put("records",problemWayBill);
        response.setStatusCode(200);
        response.setData(map);
        return response;
    }



}
