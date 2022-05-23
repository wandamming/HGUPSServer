package com.hgups.express.controller.waybillmg;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.domain.vo.StoreVo;
import com.hgups.express.service.waybillmgi.StoreService;
import com.hgups.express.util.MyFileUtil;
import com.hgups.express.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wandaming
 * 2021/7/21-15:37
 */

@Api(description = "店铺API")
@Slf4j
@RestController
@RequestMapping("/Store")
public class StoreController {



    @Resource
    private StoreService storeService;

    @ApiOperation(value = "查询店铺",notes = "此接口用于获取发出订单的店铺")
    @PostMapping("/getStore")
    public Response getStore(@ApiParam(value = "店铺参数") @RequestBody StoreParam param ) {
        Response response = new Response();
        Page<StoreVo> vo = storeService.getStoreList(param);
        response.setData(vo);
        return response;
    }

    @ApiOperation(value = "添加店铺",notes = "此接口用于添加以及授权所在平台店铺")
    @PostMapping("/insertStore")
    public boolean insertStore(@ApiParam(value = "添加店铺参数") @RequestBody InsertStoreParam param ) {
        try{
            storeService.insertStore(param);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }



    @ApiOperation(value = "取消店铺授权",notes = "此接口用于取消对店铺的授权,即更新授权状态")
    @PostMapping("/removeAuthorize")
    public boolean removeAuthorize(@ApiParam(value = "更新店铺授权状态参数")@RequestBody IdParam param) {
        try{

            storeService.removeAuthorize(param);
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }



    @ApiOperation(value = "切换店铺状态",notes = "此接口用于切换店铺状态，1启用2停用")
    @PostMapping("/changeState")
    public boolean changeState(@ApiParam(value = "切换店铺id")@RequestBody IdParam param) {
        try{
            storeService.changeState(param);
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }


    @ApiOperation(value = "导出店铺信息Excel")
    @GetMapping("/exportStore")
    public ResponseEntity exportStore(@RequestParam List storeList) {

     return storeService.exportStoreList(storeList);
    }




}
