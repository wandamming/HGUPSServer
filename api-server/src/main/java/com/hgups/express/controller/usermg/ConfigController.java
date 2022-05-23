package com.hgups.express.controller.usermg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.Config;
import com.hgups.express.domain.ConfigRecord;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.ConfigExchangeRateParam;
import com.hgups.express.domain.param.ConfigParam;
import com.hgups.express.domain.param.ConfigParamTo;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.service.usermgi.ConfigRecordService;
import com.hgups.express.service.usermgi.ConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2020/6/24 0024-16:40
 */
@Api(description = "价格配置API")
@Slf4j
@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @ApiOperation(value = "修改全程价格配置")
    @PostMapping("/setDeclarePrice")
    public Response setDeclarePrice(@RequestBody ConfigParam param){
        Response response = new Response();
        boolean update1 = true;
        boolean update2 = true;
        boolean update3 = true;
        StringBuffer msg = new StringBuffer();
        if (null!=param.getChinaPrice()){
            Config config1 = new Config();
            config1.setV(param.getChinaPrice());
            EntityWrapper wrapper1 = new EntityWrapper();
            wrapper1.eq("k","报关单价");
            update1 = configService.update(config1, wrapper1);
        }


        if (null!=param.getAviationPrice()){
            Config config2 = new Config();
            config2.setV(param.getAviationPrice());
            EntityWrapper wrapper2 = new EntityWrapper();
            wrapper2.eq("k","航空单价");
            update2 = configService.update(config2, wrapper2);
        }


        if(null!=param.getExchangeRate()){
            Config config3 = new Config();
            config3.setV(param.getExchangeRate());
            EntityWrapper wrapper3 = new EntityWrapper();
            wrapper3.eq("k","全程汇率");
            update3 = configService.update(config3, wrapper3);
        }

        if(!update1){
            msg.append("中国报关单价修改失败,");
        }
        if(!update2){
            msg.append("航空单价修改失败,");
        }
        if(!update3){
            msg.append("汇率修改失败");
        }
        if(update1&&update2&&update3){
            response.setMsg("修改成功");
            response.setStatusCode(200);
            return response;
        }else {
            response.setMsg(msg.toString());
            response.setStatusCode(130);
            return response;
        }


    }
    @Resource
    public ConfigRecordService configRecordService;
    @ApiOperation(value = "获取汇率配置记录API")
    @PostMapping("/getConfigRecord")
    public Response getOperateLog(@ApiParam(value = "分页参数") @RequestBody PageParam pageParam) {

        Response response = new Response();
        Page<ConfigRecord> logsUnitPage = configRecordService.listConfigRecord(pageParam);
        response.setData(logsUnitPage);
        return response;
    }


    @ApiOperation(value = "修改平台汇率配置")
    @PostMapping("/updatePlatformConfig")
    public Response updatePlatformConfig(@RequestBody ConfigExchangeRateParam param){
        Response response = new Response();
        String exchangeRate = param.getExchangeRate();
        if(null==exchangeRate){
            response.setStatusCode(199);
            response.setMsg("参数错误");
            return response;
        }
        Config config = new Config();
        config.setV(exchangeRate);
        EntityWrapper wrapper = new EntityWrapper();
        String s = "平台汇率";
        configService.wrappereq(s);
        boolean update = configService.update(config, wrapper);
        if (update){
            response.setStatusCode(ResponseCode.SUCCESS_CODE);
            response.setMsg("修改成功");
        }else {
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("修改失败");
        }
        return response;
    }

    @ApiOperation(value = "修改后程汇率配置")
    @PostMapping("/updateAfterUserConfig")
    public Response updateAfterUserConfig(@RequestBody ConfigExchangeRateParam param){
        Response response = new Response();
        String exchangeRate = param.getExchangeRate();
        if(null==exchangeRate){
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("参数错误");
            return response;
        }
        Config config = new Config();
        config.setV(exchangeRate);
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("k","后程汇率");
        boolean update = configService.update(config, wrapper);
        if (update){
            response.setStatusCode(200);
            response.setMsg("修改成功");
        }else {
            response.setStatusCode(199);
            response.setMsg("修改失败");
        }
        return response;
    }
    @ApiOperation(value = "修改海外仓汇率配置")
    @PostMapping("/updateWarehouseConfig")
    public Response updateWarehouseConfig(@RequestBody ConfigExchangeRateParam param){
        Response response = new Response();
        String exchangeRate = param.getExchangeRate();
        if(null==exchangeRate){
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("参数错误");
            return response;
        }
        Config config = new Config();
        config.setV(exchangeRate);
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("k","海外仓汇率");
        boolean update = configService.update(config, wrapper);
        if (update){
            response.setStatusCode(200);
            response.setMsg("修改成功");
        }else {
            response.setStatusCode(199);
            response.setMsg("修改失败");
        }
        return response;
    }

    @ApiOperation(value = "修改DHL汇率配置")
    @PostMapping("/updateDhlConfig")
    public Response updateDhlConfig(@RequestBody ConfigExchangeRateParam param){
        Response response = new Response();
        String exchangeRate = param.getExchangeRate();
        if(null==exchangeRate){
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("参数错误");
            return response;
        }
        Config config = new Config();
        config.setV(exchangeRate);
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("k","DHL汇率");
        boolean update = configService.update(config, wrapper);
        if (update){
            response.setStatusCode(200);
            response.setMsg("修改成功");
        }else {
            response.setStatusCode(199);
            response.setMsg("修改失败");
        }
        return response;
    }

    @ApiOperation(value = "获取全程配置")
    @PostMapping("/getConfigs")
    public Response getConfigs(){
        Response response = new Response();
        List<Config> configs = configService.selectList(null);
        ConfigParamTo configTo = new ConfigParamTo();
        configTo.setDeclarePrice(configs.get(0).getV());
        configTo.setAviationPrice(configs.get(1).getV());
        configTo.setExchangeRate(configs.get(2).getV());
        response.setStatusCode(200);
        response.setData(configTo);
        return response;
    }

    @ApiOperation(value = "获取后程汇率配置")
    @PostMapping("/getAfterUserConfig")
    public Response getAfterUserConfigs(){
        Response response = new Response();
        List<Config> configs = configService.selectList(null);
        ConfigParamTo configTo = new ConfigParamTo();
        configTo.setExchangeRate(configs.get(5).getV());
        response.setStatusCode(200);
        response.setData(configTo);
        return response;
    }

    @ApiOperation(value = "获取DHL汇率配置")
    @PostMapping("/getDhlConfig")
    public Response getDhlConfig(){
        Response response = new Response();
        List<Config> configs = configService.selectList(null);
        ConfigParamTo configTo = new ConfigParamTo();
        configTo.setExchangeRate(configs.get(7).getV());
        response.setStatusCode(200);
        response.setData(configTo);
        return response;
    }

    @ApiOperation(value = "获取海外仓汇率配置")
    @PostMapping("/getWarehouseConfig")
    public Response getWarehouseConfig(){
        Response response = new Response();
        List<Config> configs = configService.selectList(null);
        ConfigParamTo configTo = new ConfigParamTo();
        configTo.setExchangeRate(configs.get(6).getV());
        response.setStatusCode(200);
        response.setData(configTo);
        return response;
    }
    @ApiOperation(value = "获取平台汇率配置")
    @PostMapping("/getPlatformConfig")
    public Response getPlatformConfig(){
        Response response = new Response();
        List<Config> configs = configService.selectList(null);
        ConfigParamTo configTo = new ConfigParamTo();
        configTo.setExchangeRate(configs.get(16).getV());
        response.setStatusCode(200);
        response.setData(configTo);
        return response;
    }
}
