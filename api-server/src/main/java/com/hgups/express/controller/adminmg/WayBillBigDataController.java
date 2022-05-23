package com.hgups.express.controller.adminmg;

import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.AllProvinceWayBillParam;
import com.hgups.express.domain.param.CateGoryWayBillParam;
import com.hgups.express.domain.param.WayBillBigDataProvinceParam;
import com.hgups.express.service.waybillmgi.ArticleService;
import com.hgups.express.service.waybillmgi.WayBillContactService;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.util.MyTransUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fanc
 * 2020/9/21 0021-16:38
 */
@Api(description = "运单大数据API")
@RestController
@Slf4j
@RequestMapping("/wayBillBigData")
public class WayBillBigDataController {

    @Resource
    private WayBillContactService wayBillContactService;
    @Resource
    private ArticleService articleService;
    @Resource
    private WayBillService wayBillService;


    @ApiOperation("美国州对应的运单数量API")
    @PostMapping(value = "/AllProvinceWayBill")
    public Response AllProvinceWayBill() {
        Response response = new Response();
        List<AllProvinceWayBillParam> paramList = wayBillContactService.getWaybillContactProvince();
        List<AllProvinceWayBillParam> newList = new ArrayList<>();
        for (AllProvinceWayBillParam allProvinceWayBillParam : paramList) {
            String name = allProvinceWayBillParam.getName();
            if(StringUtils.isEmpty(name)) {
                continue;
            }
            allProvinceWayBillParam.setName(MyTransUtil.FirstLetterCapital(name).trim());
            newList.add(allProvinceWayBillParam);
        }

        response.setData(newList);
        return response;
    }

    @ApiOperation("传入州获取对应城市的运单数量API")
    @PostMapping(value = "/provinceGetCityWayBill")
    public Response provinceGetCityWayBill(@RequestBody WayBillBigDataProvinceParam param) {
        Response response = new Response();
        if (null!=param){
            if (!StringUtils.isEmpty(param.getName())){
                List<AllProvinceWayBillParam> paramList = wayBillContactService.provinceGetCityWayBill(param.getName());
                for (AllProvinceWayBillParam allProvinceWayBillParam : paramList) {
                    String name = allProvinceWayBillParam.getName();
                    String newName = MyTransUtil.FirstLetterCapital(name);
                    allProvinceWayBillParam.setName(newName);
                }
                response.setData(paramList);
                return response;
            }
        }
        response.setStatusCode(203);
        response.setMsg("参数不能为空");
        return response;
    }

    @ApiOperation("传入城市获取对应的运单数量API")
    @PostMapping(value = "/cityWayBill")
    public Response cityWayBill(@RequestBody WayBillBigDataProvinceParam param) {
        Response response = new Response();
        if (null!=param){
            if (!StringUtils.isEmpty(param.getName())){
                List<AllProvinceWayBillParam> paramList = wayBillContactService.cityWayBill(param.getName());
                response.setData(paramList);
                return response;
            }
        }
        response.setStatusCode(203);
        response.setMsg("参数不能为空");
        return response;
    }

    @ApiOperation("物品类型分类获取运单价格与重量")
    @PostMapping(value = "/cateGoryWayBill")
    public Response cateGoryWayBill() {
        Response response = new Response();
        List<CateGoryWayBillParam> cateGoryWayBillParams = wayBillContactService.cateGoryWayBill();
        response.setData(cateGoryWayBillParams);
        return response;
    }

}
