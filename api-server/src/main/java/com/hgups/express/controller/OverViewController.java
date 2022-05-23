package com.hgups.express.controller;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.hgups.express.domain.Banner;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.*;
import com.hgups.express.mapper.EntryButtonMapper;
import com.hgups.express.service.adminmgi.BannerService;
import com.hgups.express.service.usermgi.EntryButtonService;
import com.hgups.express.service.usermgi.WalletService;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.util.ResultParamUtil;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * @author lyx
 * 2021/7/21 0008-14:48
 */

@Api(description = "总览界面API")
@Slf4j
@RestController
@RequestMapping("/overview")
public class OverViewController {
    @Resource
    private WayBillService wayBillService;

    @Resource
    private WalletService walletService;

    @ApiOperation("数据模块")
    @PostMapping(value = "/getDataModule")
    public Response  getDataModule(){
        Response response = new Response();
        DataModuleVo moduleVoList = wayBillService.getDataModule();
        response.setData(moduleVoList);
        return  response;
    }

    @ApiOperation("用户钱包")
    @GetMapping(value = "/getUserWallet")
    public Response getUserWallet(){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        WalletVo walletVo = walletService.getWalletBalance(loginUserId);
        response.setData(walletVo);
        return response;
    }


    @ApiOperation("在线充值")
    @PostMapping(value = "/onlineRecharge")
    public Response onlineRecharge(@RequestBody OnlineRechargeParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        Boolean a = true;
        if (a) {
            response.setResponseBySuccessMsg("充值成功");
            return response;
        }
        response.setResponseByErrorMsg("充值失败");
        return response;
    }



    @ApiOperation("转账汇款")
    @PostMapping(value = "/moneyTransfer")
    public Response moneyTransfer(@RequestBody TransferParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        Boolean a = true;
        if (a) {
            response.setResponseBySuccessMsg("转账汇款成功");
            return response;
        }
        response.setResponseByErrorMsg("转账汇款失败");
        return response;
    }

    @Resource
    private BannerService bannerService;
    @ApiOperation("官网banner,公告，使用帮助列表")
    @PostMapping(value = "/bannerList")
    public Response bannerList(@RequestBody WebBannerParam param) {
        Response response = new Response();
        List<Banner> bannerList = bannerService.getBannerList(param);
        Integer count = bannerService.getBannerCount(param);
        Map result = ResultParamUtil.result(bannerList, count, param.getCurrent() / param.getSize() + 1, param.getSize());
        response.setData(result);
        return response;
    }

    @Resource
    public EntryButtonService entryButtonService;

    @ApiOperation("快捷入口")
    @PostMapping(value = "/getQuickEntry")
    public Response getQuickEntry(){ //只需要根据用户id进行查询 where use_id = ？ and is_select = 1 and is_show = 1
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        List<EntryVo> entryLists = entryButtonService.getEntry(loginUserId);
        response.setData(entryLists);
        return response;
    }



    @ApiOperation("入口列表")
    @PostMapping(value = "/getEntryList")
    public Response getEntryList(@RequestBody NameParam param){//根据名称模糊查询
        Response response = new Response();
        List<EntryVo> entryVoLists = entryButtonService.getAllEntry(param);
        response.setData(entryVoLists);
        return response;
    }



    @ApiOperation(value = "添加快捷入口",notes = "修改user_entry的is_select的值为1")
    @PostMapping(value = "/addAndUpdateQuickEntry")
    public Response addAndUpdateQuickEntry(@RequestBody ButtonParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();//调用方法的时候 button_id 和 loginUserId
        Response response = new Response();
        Boolean a = entryButtonService.insertEntry(loginUserId,param);
        if (a) {
            response.setResponseBySuccessMsg("添加成功");
            return response;
        }
        response.setResponseByErrorMsg("添加失败");
        return response;
    }


    @ApiOperation(value = "删除快捷入口",notes = "修改entrybutton_user的is_select的值为0")
    @PostMapping(value = "/deleteQuickEntry")
    public Response deleteQuickEntry(@RequestBody ButtonParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();//调用Service层方法的时候 button_id 和 loginUserId
        Response response = new Response();
        Boolean a = entryButtonService.deleteEntry(loginUserId,param);
        if (a) {
            response.setResponseBySuccessMsg("删除入口成功");
            return response;
        }
        response.setResponseByErrorMsg("删除入口失败");
        return  response;
    }


    @ApiOperation("使用记录")//
    @PostMapping(value = "/getUsingRecord")
    public Response getUsingRecord(){
        Long loginUserId = ShiroUtil.getLoginUserId();//参数用user_id
        Response response = new Response();
        List<EntryVo> entryVoLists = entryButtonService.getRecord(loginUserId);
        response.setData(entryVoLists);
        return response;
    }


    @ApiOperation("清空使用记录")
    @PostMapping(value = "/deleteUsingRecord")
    public Response deleteUsingRecord(){
        Long loginUserId = ShiroUtil.getLoginUserId();//参数用user_id  清空：删除所有
        Response response = new Response();
        Boolean a = entryButtonService.deleteAllRecords(loginUserId);
        if (a) {
            response.setResponseBySuccessMsg("清空记录成功");
            return response;
        }
        response.setResponseByErrorMsg("清空记录失败");
        return response;
    }


    @ApiOperation(value = "更新使用记录",notes = "用户点击按钮后更新此用户的使用记录")//修改update_time //如果记录表中存在该记录则更新相应的update_time，否则添加记录
    @PostMapping(value = "updateOrAddRecord")
    public Response updateOrAddRecord(@RequestBody UserButtonParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();//参数用user_id 和 button_id
        Response response = new Response();
        Boolean a = entryButtonService.updateRecords(param);
        if (a) {
            response.setResponseBySuccessMsg("更新记录成功");
            return response;
        }
        response.setResponseByErrorMsg("更新记录失败");
        return response;
    }

    @Resource
    private EntryButtonMapper entryButtonMapper;
    @ApiOperation(value = "进行排序")
    @PostMapping(value = "/sortEntry")
    public Response sortEntry(@RequestBody SortParam param){
        Response response = new Response();
        boolean a =entryButtonService.sortEntry(param);
        if (a) {
            response.setResponseBySuccessMsg("排序成功");
            return response;
        }
        response.setResponseByErrorMsg("排序失败");
        return response;

    }


    @ApiOperation(value = "新手指引，产品文档...")
    @PostMapping(value = "/getGuide")
    public Response getGuide(){
        Response response = new Response();
        return response;
    }
}
