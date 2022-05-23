package com.hgups.express.controller.adminmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.service.usermgi.RightsManagementService;
import com.hgups.express.service.waybillmgi.*;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.LabelUtils;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.util.USPSApi;
import com.hgups.express.vo.DifferenceVo;
import com.hgups.express.vo.WayBillVo;
import com.jpay.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/7/31 0031-16:24
 */
@Api(description = "跟换面单API")
@RestController
@Slf4j
@RequestMapping("/changeSingle")
public class ChangeSingleController {

    @Resource
    private WayBillService wayBillService;
    @Resource
    private WayBillVoService wayBillVoService;
    @Resource
    private UserAccountService userAccountService;
    @Resource
    private ZoneService zoneService;
    @Resource
    private PortContactService portContactService;
    @Resource
    private RightsManagementService rightsManagementService;


    @ApiOperation(value = "更换面单列表API")
    @PostMapping("/changeSingleList")
    public Response getChangeSingleList(@RequestBody ChangeSingleParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        int processRole = rightsManagementService.isProcessRole(loginUserId);
        //是否是后程用户
        if (processRole == 1) {
            param.setProcessUserId(loginUserId);
        }
        int superRole = rightsManagementService.isSuperRole(loginUserId);
        //是否是超级用户
        if (superRole == 1) {
            param.setProcessUserId(null);
        }
        Response response = new Response();
        param.setCurrent((param.getCurrent() - 1) * param.getSize());//起始页
        List<String> trackingNumbers = param.getTrackingNumbers();
        if (null != trackingNumbers && trackingNumbers.size() > 0 && (!"".equals(trackingNumbers.get(0)))) { //判断是否需要根据单号查询
            param.setIsTrackingNumber("1");
            System.out.println("有运单号-------");
        }
        List<WayBillAndUserParam> changeSingle = wayBillService.getChangeSingle(param);
        Integer total = wayBillService.getChangeSingleCount(param);
        Map<Object, Object> map = new HashMap<>();
        map.put("current", param.getCurrent() + 1);
        map.put("total", total);
        map.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总页数
        map.put("records", changeSingle);
        response.setStatusCode(200);
        response.setData(map);
        return response;
    }

    @ApiOperation(value = "获取更换面单运单信息")
    @PostMapping("/getChangeSingle")
    public Response getChangeSingle(@RequestBody ChangeTrackingNumberParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        EntityWrapper wrapper = new EntityWrapper();
        String trackingNumber = param.getTrackingNumber();
        if (null != trackingNumber && trackingNumber.length() > 33) {
            trackingNumber = trackingNumber.substring(8, 34);
        }
        wrapper.eq("tracking_number", trackingNumber);
        //wrapper.eq("state",1);
        WayBill wayBill = wayBillService.selectOne(wrapper);

        if (null == wayBill) {
            response.setStatusCode(300);
            response.setMsg("无法查询到追踪号码对应的包裹信息");
            return response;
        }
        ChangeSingleTrackingNumberVo wayBillVo = new ChangeSingleTrackingNumberVo();


        wayBillVo = DomainCopyUtil.map(wayBill, ChangeSingleTrackingNumberVo.class);
        if ((wayBill.getNewWayBillId() == -1) && (wayBill.getNewTrackingNumber() == null)) {
            wayBillVo.setIsCoding("0");
            wayBillVo.setFlag("0");
            response.setData(wayBillVo);
            return response;
        }
        if ((wayBill.getNewWayBillId() == -1) && (wayBill.getNewTrackingNumber() != null)) {
            wayBillVo.setIsCoding("1");
            wayBillVo.setFlag("1");
        }
        if ((wayBill.getNewWayBillId() != -1) && (wayBill.getNewTrackingNumber() == null)) {
            WayBill wayBill1 = wayBillService.selectById(wayBill.getNewWayBillId());
            wayBillVo.setIsCoding("1");
            wayBillVo.setNewTrackingNumber(wayBill1.getTrackingNumber());
            wayBillVo.setFlag("0");
            WayBill wayBill2 = wayBillService.selectById(wayBill.getNewWayBillId());
            wayBillVo.setCoding(wayBill2.getCoding());
            response.setData(wayBillVo);
            return response;
        }
        String exchange1 = wayBillVo.getNewTrackingNumber();
        String exchange2 = wayBillVo.getTrackingNumber();
        wayBillVo.setTrackingNumber(exchange1);
        wayBillVo.setNewTrackingNumber(exchange2);
        response.setData(wayBillVo);
        return response;
    }


    @ApiOperation(value = "更换面单")
    @PostMapping("/changeSingle")
    public Response changeSingle(@RequestBody ChangeSinglePlusParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        Integer wid = param.getId();
        WayBill wayBill = wayBillService.selectById(wid);
        if (null == wayBill) {
            response.setStatusCode(210);
            response.setMsg("无法查询到追踪号码对应的包裹信息");
            return response;
        }
        if ("1".equals(param.getFlag())) { //打单重量与核重不匹配
            double wareWeight = param.getWareWeight();//磅
            double ware = wayBillVoService.getOnePrice(wareWeight, wayBill.getZone(), wayBill.getChannel(), wayBill.getUserId());
            DecimalFormat format = new DecimalFormat("#.00");
            double warePrice = Double.parseDouble(format.format(ware));
            if (-1 == warePrice) {
                response.setStatusCode(209);
                response.setMsg("传入核重未找到对应价格");
                return response;
            }
            double billPrice = wayBill.getPrice();//打单价格

            //多退少补
            DifferenceVo difference = userAccountService.difference(warePrice, billPrice, wayBill);
            if (difference.getCode() == -1) {
                response.setStatusCode(211);
                response.setMsg("余额不足无法补全差价");
                return response;
            }
            if (difference.getCode() == 1) {
                response.setStatusCode(212);
                response.setMsg("未找到该运单");
                return response;
            }

            WayBill newWayBill = null;
            if ((wayBill.getNewWayBillId() == -1) && (wayBill.getNewTrackingNumber() == null)) {
                System.out.println("没有新单--->>>创建");
                newWayBill = DomainCopyUtil.map(wayBill, WayBill.class);
                wayBillService.insert(newWayBill);
            } else {
                newWayBill = wayBillService.selectById(wid);
            }

            System.out.println("新单---->>>" + newWayBill);
            if (wareWeight >= 1) {
                newWayBill.setService("P");
            } else {
                newWayBill.setService("F");
            }
            newWayBill.setChangeSingleTime(new Date());
            newWayBill.setWareWeight(wareWeight);
            newWayBill.setWarePrice(warePrice);
            WayBillVo wayBillVo = null;
            if ((wayBill.getNewWayBillId() == -1) && (wayBill.getNewTrackingNumber() != null)) {
                EntityWrapper wrapper = new EntityWrapper();
                wrapper.eq("tracking_number", wayBill.getNewTrackingNumber());
                WayBill wayBill1 = wayBillService.selectOne(wrapper);
                wayBillVo = wayBillService.getChangeSingleInfo(wayBill1.getId());
            } else {
                wayBillVo = wayBillService.getChangeSingleInfo(wayBill.getId());
            }
            System.out.println("-----新面单-------" + newWayBill);
            wayBillVo.setWayBill(newWayBill);
            WaybillContact waybillContact = wayBillVo.getWaybillContact();
            Receive receive = new Receive();
            receive.setReceiveDeliveryPoint(waybillContact.getReceiveDeliveryPoint());
            receive.setReceiveCarrierRoute(waybillContact.getReceiveCarrierRoute());
            receive.setCountries(waybillContact.getReceiveCountries());
            receive.setName(waybillContact.getReceiveName());
            receive.setCompany(StringUtils.isEmpty(waybillContact.getReceiveCompany()) ? "" : waybillContact.getReceiveCompany());
            receive.setProvinceEname(waybillContact.getReceiveProvince());
            receive.setCityEname(waybillContact.getReceiveCity());
            receive.setPostalCodet(StringUtils.isEmpty(waybillContact.getReceivePostalCodet()) ? "" : waybillContact.getReceivePostalCodet());
            receive.setPostalCode(waybillContact.getReceivePostalCode());
            receive.setAddressOne(StringUtils.isEmpty(waybillContact.getReceiveAddressOne()) ? "" : waybillContact.getReceiveAddressOne());
            receive.setAddressTwo(waybillContact.getReceiveAddressTwo());
            receive.setPhone(StringUtils.isEmpty(waybillContact.getReceivePhone()) ? "" : waybillContact.getReceivePhone());
            receive.setPhonePrefix(waybillContact.getReceivePhonePrefix());
            receive.setEmail(waybillContact.getReceiveEmail());
            wayBillVo.setReceive(receive);
            LabelUtils.Label label = LabelUtils.createLabel(wayBillVo);
            newWayBill.setCoding(label.base64);
            newWayBill.setTrackingNumber(label.trackNo);
            newWayBill.setState(1);
            newWayBill.setIsCoding(0);
            newWayBill.setWareWeight(wareWeight);
            newWayBill.setWarePrice(warePrice);
            if ((wayBill.getNewWayBillId() == -1) && (wayBill.getNewTrackingNumber() == null)) {
                newWayBill.setNewTrackingNumber(wayBill.getTrackingNumber());
            }

            wayBillService.updateById(newWayBill);

            if ((wayBill.getNewWayBillId() == -1) && (wayBill.getNewTrackingNumber() == null)) {
                wayBill.setIsProblemSolve(2);
                //wayBill.setNewTrackingNumber(newWayBill.getTrackingNumber());
                wayBill.setNewWayBillId(newWayBill.getId());
                wayBill.setState(5);//已作废
                wayBillService.updateById(wayBill);
            }
            response.setStatusCode(200);
            response.setMsg("面单更换成功");
            return response;
        }
        if ("2".equals(param.getFlag())) { //入境口岸跟换
            double wareWeight = param.getWareWeight();
            double ware = wayBillVoService.getOnePrice(wareWeight, wayBill.getZone(), wayBill.getChannel(), wayBill.getUserId());
            DecimalFormat format = new DecimalFormat("#.00");
            double warePrice = Double.parseDouble(format.format(ware));
            if (-1 == warePrice) {
                response.setStatusCode(209);
                response.setMsg("传入核重未找到对应价格");
                return response;
            }
            double billPrice = wayBill.getPrice();//打单价格

            //多退少补
            DifferenceVo difference = userAccountService.difference(warePrice, billPrice, wayBill);
            if (difference.getCode() == -1) {
                response.setStatusCode(211);
                response.setMsg("余额不足无法补全差价");
                return response;
            }
            if (difference.getCode() == 1) {
                response.setStatusCode(212);
                response.setMsg("未找到该运单");
                return response;
            }

            WayBill newWayBill = null;
            if ((wayBill.getNewWayBillId() == -1) && (wayBill.getNewTrackingNumber() == null)) {
                System.out.println("没有新单--->>>创建");
                newWayBill = DomainCopyUtil.map(wayBill, WayBill.class);
                wayBillService.insert(newWayBill);
            } else {
                System.out.println("有新单--->>>修改");
                newWayBill = wayBillService.selectById(wid);
            }

            System.out.println("新单---->>>" + newWayBill);
            if (wareWeight >= 1) {
                newWayBill.setService("P");
            } else {
                newWayBill.setService("F");
            }
            newWayBill.setChangeSingleTime(new Date());
            newWayBill.setBillWeight(wareWeight);
            newWayBill.setPrice(warePrice);
            newWayBill.setWareWeight(wareWeight);
            newWayBill.setWarePrice(warePrice);
            WayBillVo wayBillVo = null;
            if ((wayBill.getNewWayBillId() == -1) && (wayBill.getNewTrackingNumber() != null)) {
                EntityWrapper wrapper = new EntityWrapper();
                wrapper.eq("tracking_number", wayBill.getNewTrackingNumber());
                WayBill wayBill1 = wayBillService.selectOne(wrapper);
                wayBillVo = wayBillService.getChangeSingleInfo(wayBill1.getId());
            } else {
                wayBillVo = wayBillService.getChangeSingleInfo(wayBill.getId());
            }
            System.out.println("-----新面单-------" + newWayBill);
            wayBillVo.setWayBill(newWayBill);
            WaybillContact waybillContact = wayBillVo.getWaybillContact();
            Receive receive = new Receive();
            receive.setReceiveDeliveryPoint(waybillContact.getReceiveDeliveryPoint());
            receive.setReceiveCarrierRoute(waybillContact.getReceiveCarrierRoute());
            receive.setCountries(waybillContact.getReceiveCountries());
            receive.setName(waybillContact.getReceiveName());
            receive.setCompany(waybillContact.getReceiveCompany().equals("null") ? "" : waybillContact.getReceiveCompany());
            receive.setProvinceEname(waybillContact.getReceiveProvince());
            receive.setCityEname(waybillContact.getReceiveCity());
            receive.setPostalCodet(waybillContact.getReceivePostalCodet().equals("null") ? "" : waybillContact.getReceivePostalCodet());
            receive.setPostalCode(waybillContact.getReceivePostalCode());
            receive.setAddressOne(waybillContact.getReceiveAddressOne().equals("null") ? "" : waybillContact.getReceiveAddressOne());
            receive.setAddressTwo(waybillContact.getReceiveAddressTwo());
            receive.setPhone(waybillContact.getReceivePhone().equals("null") ? "" : waybillContact.getReceivePhone());
            receive.setPhonePrefix(waybillContact.getReceivePhonePrefix());
            receive.setEmail(waybillContact.getReceiveEmail());
            wayBillVo.setReceive(receive);



            /*-----------入境口岸更换面单-------------*/

            String cr = "";
            String dp = "";

            if (wayBillVo.getCheckAddress()) {
                USPSApi.Address address = new USPSApi.Address();
                address.state = wayBillVo.getReceive().getProvinceEname();
                address.city = wayBillVo.getReceive().getCityEname();
                address.zipCode5 = wayBillVo.getReceive().getPostalCode();
                address.zipCode4 = wayBillVo.getReceive().getPostalCodet();
                address.address1 = wayBillVo.getReceive().getAddressOne();
                address.address2 = wayBillVo.getReceive().getAddressTwo();
                USPSApi.Address realAddress = USPSApi.validateAddress(address);

                if (!realAddress.isValid) {
                    response.setStatusCode(311);
                    response.setMsg("网络超时请稍后重试");
                    return response;
                }
                cr = realAddress.getCarrierRoute();
                dp = realAddress.getDeliveryPoint();
            }

            wayBillVo.getWayBill().setCarrierRoute(cr);
            wayBillVo.getWayBill().setDeliveryPoint(dp);
            log.info(" create way bill cr: " + cr + ", dp: " + dp);
            ZoneDto zoneDto = zoneService.calculateZone(receive.getPostalCode(), loginUserId);
            String zone = zoneDto.getZone();
            String portEntryName = zoneDto.getPortEntryName();
            wayBillVo.getWayBill().setZone(zone);
            wayBillVo.getWayBill().setEntrySite(portEntryName);
            EntityWrapper wrapper1 = new EntityWrapper();
            wrapper1.eq("port_id", zoneDto.getPortEntryId());
            PortContact portContact = portContactService.selectOne(wrapper1);
            if ("".equals(portEntryName) || "".equals(zone) || "".equals(portContact.getConName()) || "".equals(portContact.getConAddressTwo()) || "".equals(portContact.getConCode())) {
                response.setStatusCode(219);
                response.setMsg("入境口岸联系人为空");
                return response;
            }
            wayBillVo.getWayBill().setPortId(portContact.getId());
            System.out.println("入境口岸中间表------------))))))" + portContact);
            wayBillVo.setPortContact(portContact);

            /*-----------入境口岸更换面单-------------*/

            LabelUtils.Label label = LabelUtils.createLabel(wayBillVo);
            newWayBill.setCoding(label.base64);
            newWayBill.setTrackingNumber(label.trackNo);
            newWayBill.setState(1);
            newWayBill.setIsCoding(0);
            newWayBill.setWareWeight(wareWeight);
            newWayBill.setWarePrice(warePrice);
            if ((wayBill.getNewWayBillId() == -1) && (wayBill.getNewTrackingNumber() == null)) {
                newWayBill.setNewTrackingNumber(wayBill.getTrackingNumber());
            }

            wayBillService.updateById(newWayBill);

            if ((wayBill.getNewWayBillId() == -1) && (wayBill.getNewTrackingNumber() == null)) {
                wayBill.setIsProblemSolve(2);
                //wayBill.setNewTrackingNumber(newWayBill.getTrackingNumber());
                wayBill.setNewWayBillId(newWayBill.getId());
                wayBill.setState(5);//已作废
                wayBillService.updateById(wayBill);
            }
            response.setStatusCode(200);
            response.setMsg("面单更换成功");
            return response;
        }
        response.setStatusCode(208);
        response.setMsg("传入参数有误");
        return response;
    }


}
