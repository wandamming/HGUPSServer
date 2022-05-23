package com.hgups.express.controller.adminmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.business.ShipPartnerFile;
import com.hgups.express.business.dhl.DHLService;
import com.hgups.express.business.dhl.closeout.CloseoutResponse;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.exception.MyException;
import com.hgups.express.service.adminmgi.ShippingBatchService;
import com.hgups.express.service.adminmgi.ShippingSacksService;
import com.hgups.express.service.usermgi.RightsManagementService;
import com.hgups.express.service.usermgi.UserService;
import com.hgups.express.service.waybillmgi.PointScanRecordService;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.util.BatchLabelUtils;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author fanc
 * 2020/7/7 0007-16:52
 */
@Api(description = "航运批次API")
@RestController
@Slf4j
@RequestMapping("/shippingBatch")
public class ShippingBatchController {

    @Resource
    private ShippingBatchService shippingBatchService;
    @Resource
    private ShippingSacksService shippingSacksService;
    @Resource
    private WayBillService wayBillService;
    @Resource
    private UserService userService;
    @Resource
    private RightsManagementService rightsManagementService;
    @Resource
    private DHLService dhlService;
    @Resource
    private PointScanRecordService pointScanRecordService;

    @ApiOperation(value = "创建航运批次API")
    @PostMapping("/createShippingBatch")
    public Response createShippingBatch(@RequestBody CreateShippingBatchParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        if (param.getId() == 0) {
            ShippingBatch shippingBatch = shippingBatchService.createShippingBatch(param);
            if (null == shippingBatch) {
                response.setStatusCode(300);
                response.setMsg("创建失败");
                return response;
            }
            response.setData(shippingBatch);
            response.setStatusCode(200);
            response.setMsg("创建成功");
            return response;
        } else {
            ShippingBatch shippingBatch = DomainCopyUtil.map(param, ShippingBatch.class);
            shippingBatch.setUserId(loginUserId);
            boolean b = shippingBatchService.updateById(shippingBatch);
            if (b) {
                response.setStatusCode(200);
                response.setMsg("修改成功");
                return response;
            }
            response.setStatusCode(300);
            response.setMsg("修改成功");
            return response;
        }
    }


    @ApiOperation(value = "获取当前航运批次的麻袋信息")
    @PostMapping("/getShippingBatchSacks")
    public Response getShippingBatchSacks(@RequestBody IdPageParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("shipping_batch_id", param.getId());
        wrapper.eq("state", "3");
        Page<ShippingSacks> page = new Page<>(param.getCurrent(), param.getSize());
        Page<ShippingSacks> page1 = shippingSacksService.selectPage(page, wrapper);
        List<ShippingSacks> records = page1.getRecords();
        Map<Object, Object> result = new HashMap<>();
        int total = shippingSacksService.selectCount(wrapper);//总条数
        result.put("total", total);
        result.put("current", param.getCurrent());
        result.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总页数
        result.put("records", records);
        response.setData(result);
        return response;
    }


    @ApiOperation(value = "麻袋装入航运批次")
    @PostMapping("/SacksIntoShippingBatch")
    public Response SacksIntoShippingBatch(@RequestBody ShippingSacksIntoBatchParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        ShippingBatch shippingBatch = shippingBatchService.selectById(param.getShippingBatchId());
        //int sacksId = param.getSacksId();
        String sacksNumber = param.getSacksNumber();
        Map<Object, Object> map = new HashMap<>();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("sacks_number", sacksNumber);
        int processRole = rightsManagementService.isProcessRole(loginUserId);
        if (processRole == 1) {
            wrapper.eq("user_id", loginUserId);
        }
        if (processRole == -1) {
            wrapper.eq("is_process", 0);
        }
        ShippingSacks shippingSacks = shippingSacksService.selectOne(wrapper);
        if (null == shippingSacks || null == shippingBatch) {
            response.setStatusCode(322);
            response.setMsg("麻袋加入批次失败:未找到该麻袋");
            map.put("url", "https://www.onezerobeat.com/hgups/static/audio/batch-ship-sack-not-found-girl.mp3");
            map.put("records", null);
            response.setData(map);
            return response;
        }

        //判断麻袋与批次的渠道
        if (!(shippingBatch.getChannel().equalsIgnoreCase(shippingSacks.getChannel()))) {
            response.setStatusCode(320);
            response.setMsg("麻袋加入批次失败:渠道不匹配");
            //map.put("url","https://www.onezerobeat.com/hgups/static/audio/batch-ship-sack-entry-dismatch-girl.mp3");
            map.put("records", null);
            response.setData(map);
            return response;
        }

        if (!(shippingBatch.getEntrySite().equals(shippingSacks.getEntrySite()))) {
            response.setStatusCode(320);
            response.setMsg("麻袋加入批次失败:入境口岸不匹配");
            map.put("url", "https://www.onezerobeat.com/hgups/static/audio/batch-ship-sack-entry-dismatch-girl.mp3");
            map.put("records", null);
            response.setData(map);
            return response;
        }

        if (shippingSacks.getShippingBatchId() > 0) {
            response.setStatusCode(321);
            response.setMsg("麻袋加入批次失败:麻袋已加入批次");
            map.put("url", "https://www.onezerobeat.com/hgups/static/audio/batch-ship-sack-exsit-girl.mp3");
            map.put("records", null);
            response.setData(map);
            return response;
        }
        response.setStatusCode(200);
        response.setMsg("添加成功");
        map.put("url", "https://www.onezerobeat.com/hgups/static/audio/ship-batch-success-girl.mp3");
        SacksIntoShippingBatchVo intoShippingBatchVo = DomainCopyUtil.map(shippingSacks, SacksIntoShippingBatchVo.class);
        map.put("records", intoShippingBatchVo);
        response.setData(map);
        return response;

    }

    @ApiOperation(value = "删除航运批次")
    @PostMapping("/deleteShippingBatch")
    public Response deleteShippingBatch(@RequestBody IdParam param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();

        int batchId = param.getId();
        ShippingBatch shippingBatch = shippingBatchService.selectById(batchId);
        if (null != shippingBatch) {
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("shipping_batch_id", batchId);
            List<ShippingSacks> sacksList = shippingSacksService.selectList(wrapper);
            for (ShippingSacks shippingSacks : sacksList) {
                EntityWrapper wrapper1 = new EntityWrapper();
                wrapper1.eq("shipping_batch_id", batchId);
                wrapper1.eq("shipping_sacks_id", shippingSacks.getId());
                List<WayBill> wayBillList = wayBillService.selectList(wrapper1);
                for (WayBill wayBill : wayBillList) {
                    wayBill.setUserBatchId(0);
                    wayBill.setUserSacksId(0);
                    wayBillService.updateById(wayBill);
                }
                shippingSacks.setShippingBatchId(0);
                shippingSacksService.updateById(shippingSacks);
            }
            shippingBatchService.deleteById(batchId);
            response.setStatusCode(200);
            response.setMsg("删除成功");
        } else {
            response.setStatusCode(300);
            response.setMsg("删除失败");
        }
        return response;
    }


    @ApiOperation(value = "关闭航运批次API")
    @PostMapping("/closeShippingBatch")
    @Transactional(rollbackFor = Exception.class)
    public Response closeShippingBatch(@RequestBody ShippingBatchSacksIdParam param) throws MyException {
        Long loginUserId = ShiroUtil.getLoginUserId();
        User user = userService.selectById(loginUserId);
        Response response = new Response();
        int batchId = param.getBatchId();

        List sacksIdsInto = param.getSacksIdsInto();
        List sacksIdsOut = param.getSacksIdsOut();
        ShippingBatch shippingBatch = shippingBatchService.selectById(batchId);
        if (null != shippingBatch) {
            List<ShippingSacks> shippingSacksListInto = new ArrayList<>();
            List<ShippingSacks> shippingSacksListOut = new ArrayList<>();
            if (null != sacksIdsInto && sacksIdsInto.size() > 0) {
                EntityWrapper wrapper = new EntityWrapper();
                wrapper.in("id", sacksIdsInto);
                shippingSacksListInto = shippingSacksService.selectList(wrapper);
            }
            if (null != sacksIdsOut && sacksIdsOut.size() > 0) {
                EntityWrapper wrapper = new EntityWrapper();
                wrapper.in("id", sacksIdsOut);
                shippingSacksListOut = shippingSacksService.selectList(wrapper);
            }
            float sumPrice = 0;//航运批次总金额
            float warePrice = 0;//航运批次总金额
            int sumWayBillNumber = 0;
            for (ShippingSacks shippingSacks : shippingSacksListInto) {
                int sacksId = shippingSacks.getId();
                sumPrice += shippingSacks.getSumPrice();//航运批次打单总金额
                warePrice += shippingSacks.getWarePrice();//航运批次核重总金额
                sumWayBillNumber += shippingSacks.getParcelNumber();
                EntityWrapper wrapper1 = new EntityWrapper();
                wrapper1.eq("shipping_sacks_id", sacksId);
                List<WayBill> wayBillList = wayBillService.selectList(wrapper1);
                // 麻袋加入批次
                pointScanRecordService.addSysRecord(2, shippingSacks.getSacksNumber(), "batch", null, new Date(), "批次单号：" + shippingBatch.getTrackingNumber());
                for (WayBill wayBill : wayBillList) {
                    wayBill.setShippingBatchId(batchId);
                    wayBillService.updateById(wayBill);
                    // 运单加入批次
                    pointScanRecordService.addSysRecord(1, wayBill.getTrackingNumber(), "batch", null, new Date(), "批次单号：" + shippingBatch.getTrackingNumber());
                }
                shippingSacks.setShippingBatchId(batchId);
                shippingSacks.setState("3");

                shippingSacksService.updateById(shippingSacks);
            }
            for (ShippingSacks shippingSacks : shippingSacksListOut) {
                int sacksId = shippingSacks.getId();
                sumPrice -= shippingSacks.getSumPrice();//航运批次打单总金额
                warePrice -= shippingSacks.getWarePrice();//航运批核重次总金额
                sumWayBillNumber -= shippingSacks.getParcelNumber();
                EntityWrapper wrapper1 = new EntityWrapper();
                wrapper1.eq("shipping_sacks_id", sacksId);
                List<WayBill> wayBillList = wayBillService.selectList(wrapper1);
                for (WayBill wayBill : wayBillList) {
                    wayBill.setShippingBatchId(0);
                    wayBillService.updateById(wayBill);
                }
                shippingSacks.setShippingBatchId(0);
                shippingSacks.setState("2");
                shippingSacksService.updateById(shippingSacks);
            }

            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("shipping_batch_id", batchId);
            int count = shippingSacksService.selectCount(wrapper);
            shippingBatch.setSacksNumber(count);//航运批次麻袋总数
            shippingBatch.setTotalAmount(sumPrice);//航运批次打单总金额
            shippingBatch.setWarePrice(warePrice);//航运批次核重总金额
            shippingBatch.setParcelNumber(sumWayBillNumber);//航运批次运单总数
            String shippingSacksCode = BatchLabelUtils.createShippingBatchPDF(shippingBatch, user.getUsername(), user.getCompany());//生成PDF
            //String sacksCode = PDFToBase64Util.PDFToBase64(shippingSacks.getSacksNumber());
            shippingBatch.setCoding(shippingSacksCode);
            if (shippingBatch.getSacksNumber() <= 0) {
                shippingBatch.setState("1");
                shippingBatchService.updateById(shippingBatch);
                response.setStatusCode(301);
                response.setMsg("关闭失败,批次中无麻袋,请添加麻袋后再关闭");
                return response;
            } else {
                shippingBatch.setState("2");
            }
            String channel = shippingBatch.getChannel();
            if ("DHL".equals(channel)) {
                CloseoutResponse closeoutResponse = dhlService.closeoutFull(shippingBatch);
                List<CloseoutResponse.DataBean.CloseoutsBean> closeouts = closeoutResponse.getData().getCloseouts();
                for (CloseoutResponse.DataBean.CloseoutsBean closeout : closeouts) {
                    List<CloseoutResponse.DataBean.CloseoutsBean.ManifestsBean> manifests = closeout.getManifests();
                    for (CloseoutResponse.DataBean.CloseoutsBean.ManifestsBean manifest : manifests) {
                        //批次面单
                        String file = manifest.getFile();
                        String url = manifest.getUrl();
                        shippingBatch.setDhlCodingUrl(url);
                        shippingBatch.setCoding(file);
                    }
                }
            }
            shippingBatchService.updateById(shippingBatch);
        } else {
            response.setStatusCode(300);
            response.setMsg("航运批次关闭失败");
            return response;
        }
        response.setData(shippingBatch);
        response.setStatusCode(200);
        // 关闭批次
        pointScanRecordService.addSysRecord(3, shippingBatch.getTrackingNumber(), "batch", null, new Date(), "");
        return response;
    }


    @ApiOperation(value = "获取全部已创建航运批次")
    @PostMapping("/getAllShippingBatch")
    public Response getAllShippingBatch(@RequestBody PageParam param) {
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
        List<ShippingBatch> allShippingBatch = shippingBatchService.getAllShippingBatch(param, wrapper);

        Map<Object, Object> result = new HashMap<>();
        int total = shippingBatchService.selectCount(wrapper);//总条数
        result.put("total", total);
        result.put("current", param.getCurrent());
        result.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总页数
        result.put("records", allShippingBatch);
        response.setData(result);
        response.setStatusCode(200);
        return response;
    }

    @ApiOperation(value = "获取全部已关闭航运批次")
    @PostMapping("/getCloseShippingBatch")
    public Response getCloseShippingBatch(@RequestBody ShippingBatchParam param) {
        log.info(" getCloseShippingBatch ShippingBatchParam: " + param);
        System.out.println("获取全部已关闭航运批次" + param);
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();

        EntityWrapper wrapper = new EntityWrapper();
        wrapper.orderBy("create_time", false);
        if (param.spEventState == null) {

            wrapper.eq("state", 2);
            int processRole = rightsManagementService.isProcessRole(loginUserId);
            if (processRole == 1) {
                wrapper.eq("user_id", loginUserId);
            }
            if (processRole == -1) {
                wrapper.eq("is_process", 0);
            }

            List<ShippingBatch> closeShippingBatch = shippingBatchService.getAllShippingBatch(param, wrapper);
            Map<Object, Object> result = new HashMap<>();
            int total = shippingBatchService.selectCount(wrapper);//总条数
            result.put("total", total);
            result.put("current", param.getCurrent());
            result.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总页数
            result.put("records", closeShippingBatch);
            response.setData(result);
            response.setStatusCode(200);
            return response;
        }
        //预上线航运批次列表
        String[] states = {"2", "4", "5"};
        wrapper.in("state", states);
        wrapper.eq("sp_event_state", param.spEventState);
        wrapper.eq("entry_site", param.getPort());

        List<ShippingBatch> closeShippingBatch = shippingBatchService.getAllShippingBatch(param, wrapper);
        Map<Object, Object> result = new HashMap<>();
        int total = shippingBatchService.selectCount(wrapper);//总条数
        result.put("total", total);
        result.put("current", param.getCurrent());
        result.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总页数
        result.put("records", closeShippingBatch);
        response.setData(result);
        response.setStatusCode(200);
        return response;
    }



   /* @ApiOperation(value = "获取全部已关闭航运批次")
    @PostMapping("/getCloseShippingBatch")
    public Response getCloseShippingBatch(@RequestBody ShippingBatchParam param){
        log.info(" getCloseShippingBatch ShippingBatchParam: " + param);
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("state",2).or()
                .eq("state",4).or()
                .eq("state",5);
        int processRole = rightsManagementService.isProcessRole(loginUserId);
        if (processRole==1){
            wrapper.eq("user_id",loginUserId);
        }
        if (processRole==-1){
            wrapper.eq("is_process",0);
        }
        if(!StringUtils.isEmpty(param.spEventState)) {
            wrapper.eq("sp_event_state", param.spEventState);
        }
//
//        if(param.hasSSF != null) {
//            wrapper.eq("has_ssf", param.hasSSF);
//        }

        List<ShippingBatch> closeShippingBatch = shippingBatchService.getAllShippingBatch(param, wrapper);
        Map<Object,Object> result = new HashMap<>();
        int total = shippingBatchService.selectCount(wrapper);//总条数
        result.put("total",total);
        result.put("current",param.getCurrent());
        result.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);//总页数
        result.put("records",closeShippingBatch);
        response.setData(result);
        response.setStatusCode(200);
        return response;
    }*/

    @ApiOperation(value = "更新航运批次的SSF")
    @PostMapping("/updateSSF")
    public Response updateSSF(@RequestBody ShipBatchSSFParam param) {
        log.info(" updateSSF param: " + param);
        shippingBatchService.updateSSF(param.getIds(), param.isSsf());
        return new Response();
    }

    @ApiOperation(value = "更新航运批次的SSF")
    @PostMapping("/updateSpEventState")
    public Response updateSpEventState(@RequestBody ShipBatchSpStateParam param) {
        log.info(" updateSpEventState param: " + param);
        Response response = new Response();

        if (!ShipPartnerFile.isValidState(param.getState())) {
            response.setStatusCode(ResponseCode.SHIP_FILE_INVALID_STATE);
            return response;
        }
        try {
            shippingBatchService.updateSpEventState(param.getIds(), param.getState());
        } catch (MyException e) {
            response.setResponseByErrorMsg(e.getMessage());
        }

        return response;
    }
}
