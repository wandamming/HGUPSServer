package com.hgups.express.controller.adminmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.exception.MyException;
import com.hgups.express.service.adminmgi.ShippingSacksService;
import com.hgups.express.service.usermgi.ConfigService;
import com.hgups.express.service.usermgi.RightsManagementService;
import com.hgups.express.service.usermgi.UserCostService;
import com.hgups.express.service.usermgi.UserService;
import com.hgups.express.service.waybillmgi.PointScanRecordService;
import com.hgups.express.service.waybillmgi.UserAccountService;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.service.waybillmgi.WayBillVoService;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.SacksLabelUtils;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.vo.DifferenceVo;
import com.hgups.express.vo.PageParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author fanc
 * 2020/7/8 0008-10:52
 */
@Api(description = "航运麻袋API")
@RestController
@Slf4j
@RequestMapping("/shippingSacks")
public class ShippingSacksController {

    @Resource
    private ShippingSacksService shippingSacksService;
    @Resource
    private WayBillService wayBillService;
    @Resource
    private UserService userService;
    @Resource
    private WayBillVoService wayBillVoService;
    @Resource
    private UserAccountService userAccountService;
    @Resource
    private UserCostService userCostService;
    @Resource
    private RightsManagementService rightsManagementService;
    @Resource
    private ConfigService configService;
    @Resource
    private PointScanRecordService pointScanRecordService;

    @ApiOperation(value = "创建麻袋")
    @PostMapping("/setShippingSacks")
    public Response setShippingSacks(@RequestBody ShippingSacksParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        ShippingSacks shippingSacks = shippingSacksService.setShippingSacks(param);
        if (null == shippingSacks) {
            response.setStatusCode(300);
            response.setMsg("创建失败");
            return response;
        }
        response.setStatusCode(200);
        response.setMsg("创建成功");
        response.setData(shippingSacks);
        return response;
    }


    @ApiOperation(value = "编辑麻袋")
    @PostMapping("/updateShippingSacks")
    public Response updateShippingSacks(@RequestBody ShippingSacksParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        ShippingSacks shippingSacks = shippingSacksService.selectById(param.getId());
        if (null == shippingSacks) {
            response.setStatusCode(301);
            response.setMsg("麻袋未找到");
        }
        shippingSacks.setService(param.getService());
        shippingSacks.setEntrySite(param.getEntrySite());
        shippingSacks.setComment(param.getComment());
        boolean b = shippingSacksService.updateById(shippingSacks);
        if (b) {
            response.setStatusCode(200);
            response.setMsg("修改成功");
        } else {
            response.setStatusCode(300);
            response.setMsg("修改失败");
        }
        return response;
    }


    @ApiOperation(value = "获取全部已创建航运麻袋")
    @PostMapping("/getAllShippingSacks")
    public Response getAllShippingSacks(@RequestBody PageParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("state", 1);
        wrapper.orderBy("create_time", false);
        int processRole = rightsManagementService.isProcessRole(loginUserId);
        if (processRole == 1) {
            wrapper.eq("user_id", loginUserId);
        }
        if (processRole == -1) {
            wrapper.eq("is_process", 0);
        }
        List<ShippingSacks> allShippingSacks = shippingSacksService.getAllShippingSacks(param, wrapper);
        Map<Object, Object> result = new HashMap<>();
        int total = shippingSacksService.selectCount(wrapper);//总条数
        result.put("total", total);
        result.put("current", param.getCurrent());
        result.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总页数
        result.put("records", allShippingSacks);
        response.setData(result);
        response.setStatusCode(200);
        return response;
    }

    @ApiOperation(value = "获取全部已关闭航运麻袋")
    @PostMapping("/getCloseShippingSacks")
    public Response getCloseShippingSacks(@RequestBody ShippingSacksPageParam param) {
        log.info(" getCloseShippingSacks param: " + param);
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("state", 2);
        wrapper.orderBy("create_time", false);
//        int processRole = rightsManagementService.isProcessRole(loginUserId);
//        if (processRole==1){
//            wrapper.eq("user_id",loginUserId);
//        }
//
//        if (processRole==-1){
//            wrapper.eq("is_process",0);
//        }
        //不管是管理员还是后程用户，都返回用户独有的
        wrapper.eq("user_id", loginUserId);
        //是否进行运单搜索
        if (!StringUtils.isEmpty(param.getTrackNumber())) {
            WayBill wayBill = wayBillService.getWayBill(param.trackNumber);
            wrapper.eq("id", wayBill != null ? wayBill.getShippingSacksId() : -1);
        }

        List<ShippingSacks> closeShippingSacks = shippingSacksService.getAllShippingSacks(param, wrapper);
        Map<Object, Object> result = new HashMap<>();
        int total = shippingSacksService.selectCount(wrapper);//总条数
        result.put("total", total);
        result.put("current", param.getCurrent());
        result.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总页数
        result.put("records", closeShippingSacks);
        response.setData(result);
        response.setStatusCode(200);
        return response;
    }

    @ApiOperation(value = "关闭麻袋")
    @PostMapping("/closeSacks")
    @Transactional(rollbackFor = Exception.class)
    public Response closeSacks(@RequestBody ShippingSacksWayBillParam param) throws MyException {
        Long loginUserId = ShiroUtil.getLoginUserId();
        User user = userService.selectById(loginUserId);
        Response response = new Response();
        int sacksId = param.getSacksId();
        ShippingSacks shippingSacks = shippingSacksService.selectById(sacksId);
        if (shippingSacks == null) {
            response.setStatusCode(301);
            response.setMsg("麻袋异常,关闭失败");
            return response;
        }
        shippingSacks.setState("2");
        shippingSacks.setEndTime(new Date());

        List<WayBill> wayBillListInto = new ArrayList<>();
        List<WayBill> wayBillListOut = new ArrayList<>();
        List wayBillNumbersInto = param.getWayBillNumbersInto();
        List wayBillNumbersOut = param.getWayBillNumbersOut();
        if (wayBillNumbersInto.size() != 0) {
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.in("tracking_number", wayBillNumbersInto);
            wrapper.ne("shipping_sacks_id", sacksId);
            wayBillListInto = wayBillService.selectList(wrapper);
        }
        if (wayBillNumbersOut.size() != 0) {
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.in("tracking_number", wayBillNumbersOut);
            wrapper.eq("shipping_sacks_id", sacksId);
            wayBillListOut = wayBillService.selectList(wrapper);
        }
        double sumPrice = shippingSacks.getSumPrice();//麻袋中运单打单总金额
        double warePrice = shippingSacks.getWarePrice();//麻袋中运单核重总金额
        double sumBillWeight = shippingSacks.getBillWeight();//麻袋中运单打单总重量
        double sumWareWeight = shippingSacks.getWareWeight();//麻袋中运单打单总重量
        for (WayBill wayBill : wayBillListInto) {
            wayBill.setShippingSacksId(sacksId);
            wayBill.setWareWeightTime(new Date());
            wayBillService.updateById(wayBill);
            sumWareWeight += wayBill.getWareWeight();
            sumPrice += wayBill.getPrice();
            warePrice += wayBill.getWarePrice();
            sumBillWeight += wayBill.getBillWeight();

            try {
                pointScanRecordService.addSysRecord(1, wayBill.getTrackingNumber(), "sack", null, new Date(), "麻袋追踪号码：" + shippingSacks.getSacksNumber());
            } catch (MyException e) {
                response.setResponseByErrorMsg(e.getMessage());
                return response;
            }
        }
        for (WayBill wayBill : wayBillListOut) {
            wayBill.setShippingSacksId(0);
            wayBillService.updateById(wayBill);
            sumWareWeight -= wayBill.getWareWeight();
            sumPrice -= wayBill.getPrice();
            warePrice -= wayBill.getWarePrice();
            sumBillWeight -= wayBill.getBillWeight();
        }
        shippingSacks.setBillWeight(sumBillWeight);
        shippingSacks.setWareWeight(sumWareWeight);
        shippingSacks.setSumPrice(sumPrice);
        shippingSacks.setWarePrice(warePrice);
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("shipping_sacks_id", sacksId);
        int count = wayBillService.selectCount(wrapper);
        shippingSacks.setParcelNumber(count);

        String shippingSacksCode = SacksLabelUtils.createShippingSacksPDF(shippingSacks, user.getUsername(), user.getCompany());//生成PDF
        //String sacksCode = PDFToBase64Util.PDFToBase64(shippingSacks.getSacksNumber());
        shippingSacks.setCoding(shippingSacksCode);
        shippingSacks.setCloseWeight(param.getCloseWeight());
        boolean b = shippingSacksService.updateById(shippingSacks);
        if (b) {
            int parcelNumber = shippingSacks.getParcelNumber();
            if (parcelNumber <= 0) {
                shippingSacks.setState("1");
                shippingSacks.setEndTime(null);
                shippingSacksService.updateById(shippingSacks);
                response.setStatusCode(201);
                response.setMsg("关闭失败,麻袋中无运单,请添加运单后再关闭");
                return response;
            }
            response.setStatusCode(200);
            response.setMsg("关闭成功");

            // 关闭麻袋
            pointScanRecordService.addSysRecord(2, shippingSacks.getSacksNumber(), "nuclear", null, new Date(), String.valueOf(shippingSacks.getWareWeight()));
            // 关闭麻袋
            pointScanRecordService.addSysRecord(2, shippingSacks.getSacksNumber(), "sack", null, new Date(), "");
            return response;
        }
        response.setStatusCode(300);
        response.setMsg("关闭失败");
        return response;
    }


    /*
     *
     *   后程开发
     *
     *
     * */

    @ApiOperation(value = "运单加入航运麻袋API")
    @PostMapping("/wayBillIntoSacks")
    @Transactional(rollbackFor = Exception.class)
    public Response wayBillIntoSacks(@RequestBody WayBillShippingSacksParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        int sacksId = param.getSacksId();
        ShippingSacks shippingSacks = shippingSacksService.selectById(sacksId);
        if (null == shippingSacks) {
            response.setStatusCode(324);
            response.setMsg("添加包裹到麻袋失败:麻袋异常");
            return response;
        }
        String trackingNumber = param.getTrackingNumber();
        System.out.println("扫码运单号===========" + trackingNumber);
        if (null != trackingNumber && trackingNumber.length() >= 30) {
            trackingNumber = trackingNumber.substring(8, trackingNumber.length());
        }
        System.out.println("截取后运单号===========" + trackingNumber);
        List<Config> configs = configService.selectList(null);
        double gConversion = Float.parseFloat(configs.get(4).getV());//磅转克
        double wareWeightLb1 = param.getWareWeight() / gConversion;//仓库核重（lb）

        String format = String.format("%.4f", wareWeightLb1);
        double wareWeightLb = Double.parseDouble(format);
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("tracking_number", trackingNumber);
        List<Integer> state = new ArrayList<>();
        state.add(1);
        state.add(3);
        wrapper.in("state", state);
        wrapper.eq("has_ssf", false);
        /*if (processRole==1){
            wrapper.eq("user_id",loginUserId);
            wrapper.eq("is_process",1);
        }*/
        WayBill wayBill = wayBillService.selectOne(wrapper);

        //判断加入的运单是否和麻袋的channel符合

        Map<Object, Object> map = new HashMap<>();
        if (null != wayBill) {
            double warePriceD = wayBillVoService.getOnePrice(wareWeightLb, wayBill.getZone(), wayBill.getChannel(), wayBill.getUserId());
            DecimalFormat format1 = new DecimalFormat("#.00");
            double warePrice = Double.parseDouble(format1.format(warePriceD));
            double billPrice = wayBill.getPrice();

            if (!(wayBill.getChannel().equalsIgnoreCase(shippingSacks.getChannel()))) {
                response.setStatusCode(410);
                response.setMsg("运单与麻袋的渠道不匹配");
                return response;
            }

            if (wayBill.getShippingSacksId() > 0) {
                ShippingSacks prevShippingSacks = shippingSacksService.selectById(wayBill.getShippingSacksId());
                response.setStatusCode(323);
                map.put("url", ResponseCode.VOICE_BROADCAST_ONE);
                map.put("records", prevShippingSacks);
                response.setMsg("添加包裹到麻袋失败:运单已加入麻袋: " + prevShippingSacks.getSacksNumber());
                response.setData(map);
                return response;
            }

            if (1 == wayBill.getIsIntercept()) {
                response.setStatusCode(321);
                response.setMsg("添加包裹到麻袋失败:该运单已被拦截");
                map.put("url", ResponseCode.VOICE_BROADCAST_TWO);
                map.put("records", null);
                response.setData(map);
                return response;
            }


            if (!(shippingSacks.getEntrySite().equals(wayBill.getEntrySite()))) {
                wayBill.setWarePrice(warePrice);
                wayBill.setWareWeight(wareWeightLb);
               /*wayBill.setIsProblemParcel(3);
               wayBill.setIsProblemSolve(1);*/
                wayBill.setWareWeightTime(new Date());
                wayBillService.updateById(wayBill);
                response.setStatusCode(325);
                response.setMsg("添加包裹到麻袋失败:入境口岸不匹配");
                map.put("url", ResponseCode.VOICE_BROADCAST_THREE);
                map.put("records", null);
                response.setData(map);
                return response;
            }

            //麻袋服务类型
            String service = shippingSacks.getService();
            if (!(shippingSacks.getService().equals(wayBill.getService()))) {
                wayBill.setWarePrice(warePrice);
                wayBill.setWareWeight(wareWeightLb);
                wayBill.setWareWeightTime(new Date());
                wayBillService.updateById(wayBill);
                response.setStatusCode(326);
                response.setMsg("添加包裹到麻袋失败:运单与麻袋服务类型不匹配");
                map.put("url", ResponseCode.VOICE_BROADCAST_FOUR);
                map.put("records", null);
                response.setData(map);
                return response;
            }

            if ("P".equalsIgnoreCase(service)) {
                if (wareWeightLb < 1) {
                    response.setStatusCode(440);
                    response.setMsg("添加包裹到麻袋失败:打单重量与麻袋服务类型不匹配");
                    map.put("url", ResponseCode.VOICE_BROADCAST_FOUR);
                    map.put("records", null);
                    response.setData(map);
                    return response;
                }
            } else if ("F".equalsIgnoreCase(service)) {
                if (wareWeightLb >= 1) {
                    response.setStatusCode(441);
                    response.setMsg("添加包裹到麻袋失败:打单重量与麻袋服务类型不匹配");
                    map.put("url", ResponseCode.VOICE_BROADCAST_FOUR);
                    map.put("records", null);
                    response.setData(map);
                    return response;
                }
            }

            //还未进行核重
            if (wayBill.getWareWeight() == 0) {
                if ((wayBill.getBillWeight() >= 1 && wareWeightLb < 1) || (wayBill.getBillWeight() < 1 && wareWeightLb >= 1)) {
                    wayBill.setWarePrice(warePrice);
                    wayBill.setWareWeight(wareWeightLb);
               /*wayBill.setIsProblemParcel(2);
               wayBill.setIsProblemSolve(1);*/
                    wayBill.setWareWeightTime(new Date());
                    wayBill.setIsProblemParcel(1);
                    wayBill.setIsProblemSolve(1);
                    wayBillService.updateById(wayBill);
                    response.setStatusCode(322);
                    response.setMsg("添加包裹到麻袋失败:打单重量与核重服务类型不匹配");
                    map.put("url", ResponseCode.VOICE_BROADCAST_FOUR);
                    map.put("records", null);
                    response.setData(map);
                    return response;
                }
            } else {
                if ((wayBill.getWareWeight() >= 1 && wareWeightLb < 1) || (wayBill.getWareWeight() < 1 && wareWeightLb >= 1)) {
                    wayBill.setWarePrice(warePrice);
                    wayBill.setWareWeight(wareWeightLb);
                    wayBill.setWareWeightTime(new Date());
                    wayBill.setIsProblemParcel(1);
                    wayBill.setIsProblemSolve(1);
                    wayBillService.updateById(wayBill);
                    response.setStatusCode(322);
                    response.setMsg("添加包裹到麻袋失败:打单重量与核重服务类型不匹配");
                    map.put("url", ResponseCode.VOICE_BROADCAST_FOUR);
                    map.put("records", null);
                    response.setData(map);
                    return response;
                }
            }

            //多退少补
            DifferenceVo difference = userAccountService.difference(warePrice, billPrice, wayBill);

            if (difference.getCode() == -1) {
                response.setStatusCode(211);
                response.setMsg("余额不足无法补全差价");
                return response;
            }
            if (difference.getCode() == 1) {
                response.setStatusCode(320);
                response.setMsg("添加包裹到麻袋失败:未找到该运单");
                map.put("url", ResponseCode.VOICE_BROADCAST_FIVE);
                map.put("records", null);
                response.setData(map);
                return response;
            }

            UserCost userCost = userCostService.getUserWaybillPrice(wareWeightLb);
            if (null == userCost) {
                userCost = userCostService.getMaxPrice();
            }
            double userPrice = wayBillVoService.getUserPrice(userCost, wayBill.getZone());
            wayBill.setUserWaybillPrice(userPrice);


            wayBill.setWarePrice(warePrice);
            System.out.println("存入数据库前打单重量-->>>" + wareWeightLb);
            wayBill.setWareWeight(wareWeightLb);
            wayBill.setWareWeightTime(new Date());
            wayBillService.updateById(wayBill);
            System.out.println("存入数据库后重量-->>>" + wayBill.getWareWeight());

            try {
                if (difference.getCode() !=0){
                    pointScanRecordService.addSysRecord(1, wayBill.getTrackingNumber(), "nuclear", null, new Date(), "核重：" + wayBill.getWareWeight());
                }
                if (difference.getCode() == 2) {
                    pointScanRecordService.addSysRecord(1, wayBill.getTrackingNumber(), "nuclear", "deduct", new Date(), String.valueOf(difference.getAmount()));
                } else if (difference.getCode() == 3) {
                    pointScanRecordService.addSysRecord(1, wayBill.getTrackingNumber(), "nuclear", "refund", new Date(), String.valueOf(difference.getAmount()));
                }
            } catch (MyException e) {
                response.setResponseByErrorMsg(e.getMessage());
                return response;
            }

            WayBillIntoSacksVo wayBillIntoSacksVo = DomainCopyUtil.map(wayBill, WayBillIntoSacksVo.class);
            response.setStatusCode(200);
            response.setMsg("成功加入麻袋");
            map.put("url", ResponseCode.VOICE_BROADCAST_SIX);
            map.put("records", wayBillIntoSacksVo);
            response.setData(map);
            return response;
        } else {
            response.setStatusCode(320);
            response.setMsg("添加包裹到麻袋失败:未找到该运单");
            map.put("url", ResponseCode.VOICE_BROADCAST_FIVE);
            map.put("records", null);
            response.setData(map);
            return response;
        }

    }


    /*
     *
     *     之前代码
     *
     *
     * */


  /* @ApiOperation(value = "运单加入航运麻袋API")
   @PostMapping("/wayBillIntoSacks")
   public Response wayBillIntoSacks(@RequestBody WayBillShippingSacksParam param){
       Long loginUserId = ShiroUtil.getLoginUserId();
       Response response = new Response();
       int sacksId = param.getSacksId();
       ShippingSacks shippingSacks = shippingSacksService.selectById(sacksId);
       if (null==shippingSacks){
           response.setStatusCode(324);
           response.setMsg("添加包裹到麻袋失败:麻袋异常");
           return response;
       }
       String trackingNumber = param.getTrackingNumber();
       if (null!=trackingNumber && trackingNumber.length()>33){
           trackingNumber = trackingNumber.substring(8, 34);
       }
       List<Config> configs = configService.selectList(null);
       double gConversion = Float.parseFloat(configs.get(4).getV());//磅转克
       double wareWeightLb1 = param.getWareWeight()/gConversion;//仓库核重（lb）

       String format = String.format("%.4f", wareWeightLb1);
       double wareWeightLb = Double.parseDouble(format);
       EntityWrapper wrapper = new EntityWrapper();
       wrapper.eq("tracking_number",trackingNumber);
       wrapper.eq("state",1);
       int processRole = rightsManagementService.isProcessRole(loginUserId);

       if (processRole==-1){
           wrapper.eq("user_id",loginUserId);
           wrapper.eq("is_process",0);
       }
       WayBill wayBill = wayBillService.selectOne(wrapper);

       Map<Object,Object> map = new HashMap<>();
       if (null!=wayBill){
           double warePriceD = wayBillVoService.getPrice(wareWeightLb);
           DecimalFormat format1 = new DecimalFormat("#.00");
           double warePrice = Double.parseDouble(format1.format(warePriceD));
           double billPrice = wayBill.getPrice();

           if(wayBill.getShippingSacksId()>0){
               response.setStatusCode(323);
               map.put("url","https://www.onezerobeat.com/hgups/static/audio/ship-batch-error-exist-girl.mp3");
               map.put("records",null);
               response.setMsg("添加包裹到麻袋失败:运单已加入麻袋");
               response.setData(map);
               return response;
           }
           if (1==wayBill.getIsIntercept()){
               response.setStatusCode(321);
               response.setMsg("添加包裹到麻袋失败:该运单已被拦截");
               map.put("url","https://www.onezerobeat.com/hgups/static/audio/ship-batch-error-intercept-girl.mp3");
               map.put("records",null);
               response.setData(map);
               return response;
           }

           if (!(shippingSacks.getEntrySite().equals(wayBill.getEntrySite()))){
               wayBill.setWarePrice(warePrice);
               wayBill.setWareWeight(wareWeightLb);
               *//*wayBill.setIsProblemParcel(3);
               wayBill.setIsProblemSolve(1);*//*
               wayBill.setWareWeightTime(new Date());
               wayBillService.updateById(wayBill);
               response.setStatusCode(325);
               response.setMsg("添加包裹到麻袋失败:入境口岸不匹配");
               map.put("url","https://www.onezerobeat.com/hgups/static/audio/ship-batch-error-entry-dismatch-girl.mp3");
               map.put("records",null);
               response.setData(map);
               return response;
           }

           if (!(shippingSacks.getService().equals(wayBill.getService()))){
               wayBill.setWarePrice(warePrice);
               wayBill.setWareWeight(wareWeightLb);
               wayBill.setWareWeightTime(new Date());
               wayBillService.updateById(wayBill);
               response.setStatusCode(321);
               response.setMsg("添加包裹到麻袋失败:运单与麻袋服务类型不匹配");
               map.put("url","https://www.onezerobeat.com/hgups/static/audio/ship-batch-error-service-type-dismatch-girl.mp3");
               map.put("records",null);
               response.setData(map);
               return response;
           }

           if ((wayBill.getBillWeight()>=1&&wareWeightLb<1)||(wayBill.getBillWeight()<1&&wareWeightLb>=1)){
               wayBill.setWarePrice(warePrice);
               wayBill.setWareWeight(wareWeightLb);
               *//*wayBill.setIsProblemParcel(2);
               wayBill.setIsProblemSolve(1);*//*
               wayBill.setWareWeightTime(new Date());
               wayBill.setIsProblemParcel(1);
               wayBill.setIsProblemSolve(1);
               wayBillService.updateById(wayBill);
               response.setStatusCode(322);
               response.setMsg("添加包裹到麻袋失败:打单重量与核重服务类型不匹配");
               map.put("url","https://www.onezerobeat.com/hgups/static/audio/ship-batch-error-service-type-dismatch-girl.mp3");
               map.put("records",null);
               response.setData(map);
               return response;
           }
            //多退少补
           Integer difference = userAccountService.difference(warePrice, billPrice, wayBill);
           if (difference==-1){
               response.setStatusCode(211);
               response.setMsg("余额不足无法补全差价");
               return response;
           }
           if (difference==1){
               response.setStatusCode(212);
               response.setMsg("未找到该运单");
               return response;
           }

           UserCost userCost = userCostService.getUserWaybillPrice(wareWeightLb);
           if(null==userCost){
               userCost = userCostService.getMaxPrice();
           }
           double userPrice = wayBillVoService.getUserPrice(userCost, wayBill.getZone());
           wayBill.setUserWaybillPrice(userPrice);


           wayBill.setWarePrice(warePrice);
           System.out.println("存入数据库前打单重量-->>>"+wareWeightLb);
           wayBill.setWareWeight(wareWeightLb);
           wayBill.setWareWeightTime(new Date());
           wayBillService.updateById(wayBill);
           System.out.println("存入数据库后重量-->>>"+wayBill.getWareWeight());
           response.setStatusCode(200);
           response.setMsg("成功加入麻袋");
           map.put("url","https://www.onezerobeat.com/hgups/static/audio/ship-batch-success-girl.mp3");
           map.put("records",wayBill);
           response.setData(map);
           return response;
       }else {
           response.setStatusCode(320);
           response.setMsg("添加包裹到麻袋失败:未找到该运单");
           map.put("url","https://www.onezerobeat.com/hgups/static/audio/ship-batch-error-not-found-girl.mp3");
           map.put("records",null);
           response.setData(map);
           return response;
       }

   }*/

    @ApiOperation(value = "删除麻袋")
    @PostMapping("/deleteShippingSacks")
    public Response deleteShippingSacks(@RequestBody IdParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.eq("shipping_sacks_id", param.getId());
        List<WayBill> wayBillList = wayBillService.selectList(wrapper);
        for (WayBill wayBill : wayBillList) {
            wayBill.setShippingSacksId(0);
            wayBill.setShippingBatchId(0);
            wayBillService.updateById(wayBill);
        }
        boolean b = shippingSacksService.deleteById(param.getId());
        if (b) {
            response.setStatusCode(200);
            response.setMsg("删除成功");
        } else {
            response.setStatusCode(300);
            response.setMsg("删除失败");
        }
        return response;
    }

    @ApiOperation(value = "获取当前麻袋的运单")
    @PostMapping("/getShippingSacksWayBill")
    public Response getShippingSacksWayBill(@RequestBody IdPageParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.eq("shipping_sacks_id", param.getId());
        Page<WayBill> page = new Page<>(param.getCurrent(), param.getSize());

        Page<WayBill> wayBillPage = wayBillService.selectPage(page, wrapper);
        List<WayBill> wayBillList = wayBillPage.getRecords();
        Map<Object, Object> result = new HashMap<>();
        int total = wayBillService.selectCount(wrapper);//总条数
        result.put("total", total);
        result.put("current", param.getCurrent());
        result.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总页数
        result.put("records", wayBillList);
        response.setData(result);
        response.setStatusCode(200);

        return response;
    }


    @ApiOperation(value = "获取未加入麻袋的运单")
    @PostMapping("/getNotJoinWayBill")
    public Response getNotJoinWayBill(@RequestBody PageParameters param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        List<WayBill> wayBillList = wayBillService.getNotJoinWayBill();
        Map<Object, Object> result = new HashMap<>();
        int total = wayBillService.getNotJoinWayBillCount();//总条数
        result.put("total", total);
        result.put("current", param.getCurrent());
        result.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总页数
        result.put("records", wayBillList);
        response.setData(result);
        response.setStatusCode(200);

        return response;
    }

    @ApiOperation(value = "重开麻袋")
    @PostMapping("/reopenShippingSacks")
    public Response reopenShippingSacks(@RequestBody IdParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        ShippingSacks shippingSacks = shippingSacksService.selectById(param.getId());
        shippingSacks.setState("1");
        shippingSacks.setEndTime(null);
        boolean b = shippingSacksService.updateById(shippingSacks);
        if (b) {
            response.setStatusCode(200);
            response.setMsg("重开成功");
            return response;
        }
        response.setStatusCode(300);
        response.setMsg("重开失败");
        return response;
    }

}
