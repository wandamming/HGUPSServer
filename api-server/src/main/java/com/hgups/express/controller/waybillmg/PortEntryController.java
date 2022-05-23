package com.hgups.express.controller.waybillmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.constant.Constant;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.ParamId;
import com.hgups.express.domain.param.PortContactParam;
import com.hgups.express.domain.param.PortContactVo;
import com.hgups.express.domain.param.PortEntryParam;
import com.hgups.express.service.usermgi.PortLateService;
import com.hgups.express.service.waybillmgi.PortContactService;
import com.hgups.express.service.waybillmgi.PortEntryService;
import com.hgups.express.service.waybillmgi.SenderService;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.vo.PageParameters;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/6/20 0020-9:51
 */
@Slf4j
@Api(description = "入境口岸API")
@RestController
@RequestMapping("/port")
public class PortEntryController {

    @Resource
    private PortEntryService portEntryService;
    @Resource
    private PortContactService portContactService;
    @Resource
    private SenderService senderService;
    @Resource
    private PortLateService portLateService;

    @ApiOperation(value = "添加入境口岸API")
    @PostMapping("/setPortEntry")
    public Response setPortEntry(@RequestBody PortEntryParam param) {
        Response response = new Response();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("title", param.getTitle());
        wrapper.eq("type", param.getType());
        PortEntry portEntry = portEntryService.selectOne(wrapper);
        if (null != portEntry) {
            response.setStatusCode(ResponseCode.PORT_EXIST);
            return response;
        }
        PortEntry port = DomainCopyUtil.map(param, PortEntry.class);
        port.setState(1);
        boolean insert = portEntryService.insert(port);
        PortContact portContact = new PortContact();
        portContact.setPortCode(port.getZipCode());
        portContact.setPortTitle(port.getTitle());
        portContact.setPortState(port.getState());
        portContact.setPortId(port.getId());
        portContact.setMid(port.getMid());
        portContact.setCrid(port.getCrid());
        boolean insert1 = portContactService.insert(portContact);

        if (insert && insert1 && Constant.USPS_PORT_TAIL.equals(param.getType())) {
            //管理员增加入境口岸的同时---增加所有后程用户的入境口岸
            portLateService.addPortForAllUser(port);
        }

        response.setStatusCode(insert ? ResponseCode.SUCCESS_CODE : ResponseCode.FAILED_CODE);
        response.setMsg(insert ? "添加成功" : "添加失败");
        return response;
    }

    @ApiOperation(value = "修改入境口岸API")
    @PostMapping("/updatePortEntry")
    public Response updatePortEntry(@RequestBody PortEntryParam param) {
        Response response = new Response();

        PortEntry portEntry = portEntryService.selectById(param.getId());
        portEntry.setCrid(param.getCrid());
        portEntry.setMid(param.getMid());
        portEntry.setTitle(param.getTitle());
        portEntry.setZipCode(param.getZipCode());
        boolean update = portEntryService.updateById(portEntry);
        EntityWrapper<PortContact> wrapper = new EntityWrapper<>();
        wrapper.eq("port_id", portEntry.getId());
        PortContact portContact = portContactService.selectOne(wrapper);
        portContact.setPortCode(portEntry.getZipCode());
        portContact.setPortTitle(portEntry.getTitle());
        portContact.setMid(portEntry.getMid());
        portContact.setCrid(portEntry.getCrid());
        boolean b = portContactService.updateById(portContact);

        //后程入境口岸的话
        if (update && b && Constant.USPS_PORT_TAIL.equals(portEntry.getType())) {
            //修改后称用户入境口岸名称
            EntityWrapper<PortLate> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("port_id", param.getId());
            List<PortLate> portLates = portLateService.selectList(wrapper1);
            for(PortLate portLate : portLates) {
                portLate.setPortLateName(param.getTitle());
            }
            portLateService.updateBatchById(portLates);

            response.setStatusCode(200);
            response.setMsg("修改成功");
            return response;
        }
        response.setStatusCode(130);
        response.setMsg("修改失败");
        return response;
    }

    @ApiOperation(value = "删除入境口岸API")
    @PostMapping("/deletePortEntry")
    public Response deletePortEntry(@RequestBody ParamId portParam) {
        Response response = new Response();
        PortEntry port = portEntryService.selectById(portParam.getId());
        String type = port.getType();

        boolean delete = portEntryService.deleteById(port.getId());
        //后程入境口岸
        if (Constant.USPS_PORT_TAIL.equals(type)) {
            EntityWrapper<PortLate> wrapper = new EntityWrapper<>();
            wrapper.eq("port_id", portParam.getId());
            //删除后程用户入境口岸表
           boolean status = portLateService.delete(wrapper);
           log.info(" deletePortEntry status: " + status);
        }

        if (delete) {
            response.setStatusCode(200);
            response.setMsg("删除成功");
            return response;
        }

        response.setStatusCode(130);
        response.setMsg("删除失败");
        return response;
    }

    @ApiOperation(value = "修改入境口岸状态API")
    @PostMapping("/updatePortEntryState")
    public Response updatePortEntryState(@RequestBody ParamId param) {
        Response response = new Response();
        PortEntry port = portEntryService.selectById(param.getId());
        port.setState(param.getState());
        boolean update = portEntryService.updateById(port);
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("port_id", param.getId());
        PortContact portContact = portContactService.selectOne(wrapper);

        //后程入境口岸的变化，才需要进行遍历
        if (update && Constant.USPS_PORT_TAIL.equals(port.getType())) {
            //如果管理员关闭入境口岸-----修改后程用户的入境口岸状态为关闭
            EntityWrapper<PortLate> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("port_id", param.getId());
            List<PortLate> portLates = portLateService.selectList(wrapper1);

            for(PortLate portLate : portLates) {
                portLate.setPortState(param.getState());
            }
            portLateService.updateBatchById(portLates);
        }

        response.setStatusCode(update ? ResponseCode.SUCCESS_CODE : ResponseCode.FAILED_CODE);
        response.setMsg(update ? "修改成功" : "修改失败");
        return response;
    }

    @ApiOperation(value = "获取入境口岸API")
    @PostMapping("/getPortEntry")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = PortContactVo.class)
    })
    public Response getPortEntry(@RequestBody PageParameters param) {
        log.info(" getPortEntry param: " + param);
        Response response = new Response();
        List<PortEntry> portEntries;
        if (null == param.getSize() || null == param.getCurrent()) {
            portEntries = portEntryService.selectList(null);
            return new Response(portEntries);
        }

        List<PortContactVo> portContactVos = new ArrayList<>();
        Page<PortEntry> page = new Page<>(param.getCurrent(), param.getSize());
        EntityWrapper portWrapper = new EntityWrapper();
        portWrapper.eq("type", param.getType());
        Page<PortEntry> portEntryPage = portEntryService.selectPage(page, portWrapper);

        List<PortEntry> portEntrieList = portEntryPage.getRecords();
        for (PortEntry portEntry : portEntrieList) {
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("port_id", portEntry.getId());
            PortContact portContact = portContactService.selectOne(wrapper);
            PortContactVo portContactVo = portContact != null ? DomainCopyUtil.map(portContact, PortContactVo.class) : new PortContactVo();

            portContactVo.setPortId(portEntry.getId());
            portContactVo.setPortTitle(portEntry.getTitle());
            portContactVo.setPortCode(portEntry.getZipCode());
            portContactVo.setMid(portEntry.getMid());
            portContactVo.setCrid(portEntry.getCrid());
            portContactVo.setPortState(portEntry.getState());
            portContactVo.setSenderId(StringUtils.isEmpty(portContactVo.getConAddressTwo()) ? 0 : 1);
            portContactVo.setUspsId(StringUtils.isEmpty(portContactVo.getUspsAddressTwo()) ? 0 : 1);
            portContactVos.add(portContactVo);
        }

        Map<Object, Object> map = new HashMap<>();
        EntityWrapper totalWrapper = new EntityWrapper();
        totalWrapper.eq("type", param.getType());
        int total = portEntryService.selectCount(totalWrapper);//总个数
        map.put("total", total);
        map.put("current", param.getCurrent());
        map.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总条数
        map.put("records", portContactVos);
        response.setData(map);
        response.setStatusCode(200);
        return response;


    }


    //入境口岸绑定联系人
    @ApiOperation(value = "添加修改入境口岸联系人API")
    @PostMapping("/setportContact")
    public Response portContact(@RequestBody PortContactParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        int portId = param.getPortId();
        int senderId = param.getSenderId();
        Sender sender = senderService.getSender(senderId);
        PortEntry portEntry = portEntryService.selectById(portId);

        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("port_id", portId);
        PortContact portContact1 = portContactService.selectOne(wrapper);
        PortContact portContact = new PortContact();
        if (portContact1 == null) {
            portEntry.setSenderId(senderId);
            portEntryService.updateById(portEntry);
            portContact.setPortState(portEntry.getState());
            portContact.setPortTitle(portEntry.getTitle());
            portContact.setPortCode(portEntry.getZipCode());
            portContact.setConAddressOne(sender.getAddressOne() == null ? "" : sender.getAddressOne());
            portContact.setConAddressTwo(sender.getAddressTwo());
            portContact.setConCcity(sender.getCityCname());
            portContact.setConCode(sender.getPostalCode());
            portContact.setConCodet(sender.getPostalCodet() == null ? "" : sender.getPostalCodet());
            portContact.setConCompany(sender.getCompany() == null ? "" : sender.getCompany());
            portContact.setConName(sender.getName());
            portContact.setConPhone(sender.getPhone());
            portContact.setConEmail(sender.getEmail() == null ? "" : sender.getEmail());
            portContact.setConCprovince(sender.getProvinceCname());
            portContact.setConEcity(sender.getCityEname());
            portContact.setConEprovince(sender.getProvinceEname());
            portContact.setConPhonePrefix(sender.getPhonePrefix());
            portContact.setConCountries(sender.getCountries());
            portContact.setPortId(portEntry.getId());
            portContact.setUserId(loginUserId);

            boolean insert = portContactService.insert(portContact);
            if (insert) {
                response.setStatusCode(200);
                response.setMsg("添加成功");
                return response;
            }
            response.setStatusCode(300);
            response.setMsg("添加失败");
            return response;
        } else {
            portEntry.setSenderId(senderId);
            portEntryService.updateById(portEntry);
            portContact1.setPortState(portEntry.getState());
            portContact1.setPortTitle(portEntry.getTitle());
            portContact1.setPortCode(portEntry.getZipCode());
            portContact1.setConAddressOne(sender.getAddressOne() == null ? "" : sender.getAddressOne());
            portContact1.setConAddressTwo(sender.getAddressTwo());
            portContact1.setConCcity(sender.getCityCname());
            portContact1.setConCode(sender.getPostalCode());
            portContact1.setConCodet(sender.getPostalCodet() == null ? "" : sender.getPostalCodet());
            portContact1.setConCompany(sender.getCompany() == null ? "" : sender.getCompany());
            portContact1.setConName(sender.getName());
            portContact1.setConPhone(sender.getPhone());
            portContact1.setConEmail(sender.getEmail() == null ? "" : sender.getEmail());
            portContact1.setConCprovince(sender.getProvinceCname());
            portContact1.setConEcity(sender.getCityEname());
            portContact1.setConEprovince(sender.getProvinceEname());
            portContact1.setConPhonePrefix(sender.getPhonePrefix());
            portContact1.setConCountries(sender.getCountries());
            portContact1.setPortId(portEntry.getId());
            portContact1.setUserId(loginUserId);
            System.out.println("-=====ddfsdf======" + portContact1.toString());
            boolean insert = portContactService.updateById(portContact1);
            if (insert) {
                response.setStatusCode(200);
                response.setMsg("修改成功");
                return response;
            }
            response.setStatusCode(300);
            response.setMsg("修改失败");
            return response;
        }
    }


    //入境口岸绑定usps分配联系人
    @ApiOperation(value = "添加修改入境口岸USPS分配联系人API")
    @PostMapping("/setPortUsps")
    public Response setPortUsps(@RequestBody PortContactParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        int portId = param.getPortId();
        int senderId = param.getSenderId();
        Sender sender = senderService.getSender(senderId);
        PortEntry portEntry = portEntryService.selectById(portId);

        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("port_id", portId);
        PortContact portContact1 = portContactService.selectOne(wrapper);
        PortContact portContact = new PortContact();
        if (portContact1 == null) {
            portEntry.setSenderId(senderId);
            portEntryService.updateById(portEntry);
            portContact.setPortState(portEntry.getState());
            portContact.setPortTitle(portEntry.getTitle());
            portContact.setPortCode(portEntry.getZipCode());
            portContact.setUspsAddressOne(sender.getAddressOne() == null ? "" : sender.getAddressOne());
            portContact.setUspsAddressTwo(sender.getAddressTwo());
            portContact.setUspsCcity(sender.getCityCname());
            portContact.setUspsCode(sender.getPostalCode());
            portContact.setUspsCodet(sender.getPostalCodet() == null ? "" : sender.getPostalCodet());
            portContact.setUspsCompany(sender.getCompany() == null ? "" : sender.getCompany());
            portContact.setUspsName(sender.getName());
            portContact.setUspsPhone(sender.getPhone());
            portContact.setUspsEmail(sender.getEmail() == null ? "" : sender.getEmail());
            portContact.setUspsCprovince(sender.getProvinceCname());
            portContact.setUspsEcity(sender.getCityEname());
            portContact.setUspsEprovince(sender.getProvinceEname());
            portContact.setUspsPhonePrefix(sender.getPhonePrefix());
            portContact.setUspsCountries(sender.getCountries());
            portContact.setPortId(portEntry.getId());
            portContact.setUserId(loginUserId);

            boolean insert = portContactService.insert(portContact);
            if (insert) {
                response.setStatusCode(200);
                response.setMsg("添加成功");
                return response;
            }
            response.setStatusCode(300);
            response.setMsg("添加失败");
            return response;
        } else {
            portEntry.setSenderId(senderId);
            portEntryService.updateById(portEntry);
            portContact1.setPortState(portEntry.getState());
            portContact1.setPortTitle(portEntry.getTitle());
            portContact1.setPortCode(portEntry.getZipCode());
            portContact1.setUspsAddressOne(sender.getAddressOne() == null ? "" : sender.getAddressOne());
            portContact1.setUspsAddressTwo(sender.getAddressTwo());
            portContact1.setUspsCcity(sender.getCityCname());
            portContact1.setUspsCode(sender.getPostalCode());
            portContact1.setUspsCodet(sender.getPostalCodet() == null ? "" : sender.getPostalCodet());
            portContact1.setUspsCompany(sender.getCompany() == null ? "" : sender.getCompany());
            portContact1.setUspsName(sender.getName());
            portContact1.setUspsPhone(sender.getPhone());
            portContact1.setUspsEmail(sender.getEmail() == null ? "" : sender.getEmail());
            portContact1.setUspsCprovince(sender.getProvinceCname());
            portContact1.setUspsEcity(sender.getCityEname());
            portContact1.setUspsEprovince(sender.getProvinceEname());
            portContact1.setUspsPhonePrefix(sender.getPhonePrefix());
            portContact1.setUspsCountries(sender.getCountries());
            portContact1.setPortId(portEntry.getId());
            portContact1.setUserId(loginUserId);

            boolean insert = portContactService.updateById(portContact1);
            if (insert) {
                response.setStatusCode(200);
                response.setMsg("修改成功");
                return response;
            }
            response.setStatusCode(300);
            response.setMsg("修改失败");
            return response;
        }
    }
}
