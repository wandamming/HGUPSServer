package com.hgups.express.controller.waybillmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.service.usermgi.PortLateDhlService;
import com.hgups.express.service.waybillmgi.DhlPortService;
import com.hgups.express.service.waybillmgi.SenderService;
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
import java.util.HashMap;
import java.util.Map;

/**
 * @author fanc
 * 2020/9/19 0019-20:41
 */
@Api(description = "DHL入境口岸相关API")
@Slf4j
@RestController
@RequestMapping("dhlPort")
public class DhlPortController {

    @Resource
    private DhlPortService dhlPortService;
    @Resource
    private PortLateDhlService portLateDhlService;
    @Resource
    private SenderService senderService;

    @ApiOperation(value = "增加修改DHL入境口岸信息")
    @PostMapping("/addOrUpdateDhlPort")
    public Response addOrUpdateDhlPort(@RequestBody AddOrUpdateDhlPortParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        DhlPort dhlPort = DomainCopyUtil.map(param, DhlPort.class);
        boolean b = dhlPortService.insertOrUpdate(dhlPort);
        if (param.getId()==0){
            //存入后程DHL入境口岸
            PortLateDhl portLateDhl = new PortLateDhl();
            portLateDhl.setPortLateDhlName(param.getPortName());
            portLateDhl.setPortDhlState(dhlPort.getState());
            portLateDhl.setLateDhlState(0);
            portLateDhl.setPortId(dhlPort.getId());
            portLateDhlService.insert(portLateDhl);
        }else {
            //修改后称用户入境口岸名称
            EntityWrapper<PortLateDhl> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("port_dhl_id",param.getId());
            PortLateDhl portLateDhl = portLateDhlService.selectOne(wrapper1);
            portLateDhl.setPortLateDhlName(param.getPortName());
            portLateDhlService.updateById(portLateDhl);
        }

        if (b){
            response.setStatusCode(ResponseCode.SUCCESS_CODE);
            response.setMsg("成功");
        }else {
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("失败");
        }
        return response;
    }

    @ApiOperation(value = "删除DHL入境口岸信息")
    @PostMapping("/deleteDhlPort")
    public Response deleteDhlPort(@RequestBody IdParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        DhlPort dhlPort = dhlPortService.selectById(param.getId());
        dhlPort.setFakeDelete(1);
        boolean b = dhlPortService.updateById(dhlPort);
        if (b){
            response.setStatusCode(ResponseCode.SUCCESS_CODE);
            response.setMsg("删除成功");
        }else {
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("删除失败");
        }
        return response;
    }

    @ApiOperation(value = "修改DHL入境口岸状态")
    @PostMapping("/updateDhlPortState")
    public Response updateDhlPortState(@RequestBody DhlPortStateParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        DhlPort dhlPort = dhlPortService.selectById(param.getId());
        dhlPort.setState(param.getState());
        dhlPortService.updateById(dhlPort);
        EntityWrapper<PortLateDhl> wrapper = new EntityWrapper<>();
        wrapper.eq("port_dhl_id",dhlPort.getId());
        PortLateDhl portLateDhl = portLateDhlService.selectOne(wrapper);
        portLateDhl.setLateDhlState(param.getState());
        portLateDhl.setPortDhlState(param.getState());
        portLateDhlService.updateById(portLateDhl);
        if(param.getState()==1){
            response.setStatusCode(200);
            response.setMsg("已开启");
            return response;
        }
        response.setStatusCode(200);
        response.setMsg("已禁用");
        return response;
    }

    @ApiOperation(value = "增加修改DHL入境口岸联系人")
    @PostMapping("/addOrUpdateDhlPortContact")
    public Response addOrUpdateDhlPortContact(@RequestBody AddOrUpdateDhlPortContactParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        //入境口岸ID
        Integer id = param.getId();
        //联系人ID
        Integer sendId = param.getSendId();
        Sender sender = senderService.selectById(sendId);
        DhlPort dhlPort = dhlPortService.selectById(id);
        if (dhlPort==null || sender==null){
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("数据异常，请联系管理员");
            return response;
        }
        dhlPort.setSendId(sendId);
        dhlPort.setContactCity(sender.getCityEname());
        dhlPort.setContactCode(sender.getPostalCode());
        dhlPort.setContactAddressOne(sender.getAddressOne());
        dhlPort.setContactAddressTwo(sender.getAddressTwo());
        dhlPort.setContactCodet(sender.getPostalCodet());
        dhlPort.setContactCompany(sender.getCompany());
        dhlPort.setContactName(sender.getName());
        dhlPort.setContactPhone(sender.getPhone());
        dhlPort.setContactProvince(sender.getProvinceEname());
        dhlPort.setPhonePrefix(sender.getPhonePrefix());
        dhlPort.setSenderCarrierRoute(sender.getSenderCarrierRoute());
        dhlPort.setSenderDeliveryPoint(sender.getSenderDeliveryPoint());
        boolean b = dhlPortService.updateById(dhlPort);

        response.setStatusCode(ResponseCode.SUCCESS_CODE);
        response.setMsg("成功");
        return response;
    }

    @ApiOperation(value = "DHL入境口岸列表")
    @PostMapping("/dhlPortList")
    public Response dhlPortList(@RequestBody PageParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        Page<DhlPort> page = new Page<>(param.getCurrent(),param.getSize());
        EntityWrapper<DhlPort> wrapper = new EntityWrapper<>();
        wrapper.eq("fake_delete",0);//未删除的
        Page<DhlPort> page1 = dhlPortService.selectPage(page, wrapper);
        int total = dhlPortService.selectCount(wrapper);
        Map<Object,Object> result = new HashMap<>();
        result.put("total",total);
        result.put("size",param.getSize());
        result.put("current",param.getCurrent());
        result.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);//总条数
        result.put("records",page1.getRecords());
        response.setStatusCode(200);
        response.setData(result);
        return response;
    }

}
