package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.*;
import com.hgups.express.domain.enmus.PointScanType;
import com.hgups.express.domain.param.*;
import com.hgups.express.exception.MyException;
import com.hgups.express.service.adminmgi.ShippingBatchService;
import com.hgups.express.service.adminmgi.ShippingSacksService;
import com.hgups.express.service.usermgi.RoleService;
import com.hgups.express.service.usermgi.UserRoleService;
import com.hgups.express.service.usermgi.UserService;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.vo.PointScanRecordVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author fanc
 * 2020/11/5-17:14
 */
@Service
@Slf4j
public class PointScanAlwayService {


    @Resource
    private WayBillService wayBillService;
    @Resource
    private ShippingBatchService shippingBatchService;
    @Resource
    private ShippingSacksService shippingSacksService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private RoleService roleService;
    @Resource
    private PointScanRecordService pointScanRecordService;
    @Resource
    private PointScanService pointScanService;
    @Resource
    private UserService userService;

    //运单详情
    public PointScanWaybillDetails pointScanWayBillDetails(PointScanOrderDetailsParam param) {
        String order = param.getOrder();
        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.eq("tracking_number", order);
        WayBill wayBill = wayBillService.selectOne(wrapper);
        PointScanWaybillDetails waybillDetails = DomainCopyUtil.map(wayBill, PointScanWaybillDetails.class);
        User user = userService.selectById(wayBill.getUserId());
        String phonePrefix = user.getPhonePrefix();
        if (StringUtils.isEmpty(phonePrefix)) {
            waybillDetails.setPhone(user.getPhone());
        } else {
            String phone = phonePrefix + user.getPhone();
            waybillDetails.setPhone(phone);
        }
        return waybillDetails;
    }


    //麻袋详情
    public PointScanShippingSacksDetailsVo pointScanShippingSacksDetails(PointScanOrderDetailsParam param) {
        String order = param.getOrder();
        EntityWrapper<ShippingSacks> wrapper = new EntityWrapper<>();
        wrapper.eq("sacks_number", order);
        ShippingSacks shippingSacks = shippingSacksService.selectOne(wrapper);
        PointScanShippingSacksDetailsVo pointScanShippingSacksDetailsVo = DomainCopyUtil.map(shippingSacks, PointScanShippingSacksDetailsVo.class);


        EntityWrapper<WayBill> wrapper1 = new EntityWrapper<>();
        wrapper1.eq("shipping_sacks_id", shippingSacks.getId());
        Page<WayBill> page = new Page<>(param.getCurrent(), param.getSize());
        Page<WayBill> page1 = wayBillService.selectPage(page, wrapper1);
        List<PointScanSacksWayBillVo> pointScanSacksWayBillVos = DomainCopyUtil.mapList(page1.getRecords(), PointScanSacksWayBillVo.class);
        pointScanShippingSacksDetailsVo.setWayBillVos(pointScanSacksWayBillVos);
        return pointScanShippingSacksDetailsVo;
    }

    //批次详情
    public PointScanShippingBatchVo pointScanShippingBatchDetails(PointScanOrderDetailsParam param) {
        String order = param.getOrder();
        EntityWrapper<ShippingBatch> wrapper = new EntityWrapper<>();
        wrapper.eq("tracking_number", order);
        ShippingBatch shippingBatch = shippingBatchService.selectOne(wrapper);
        //总重量
        Double wareWeightByBatchId = wayBillService.getWareWeightByBatchId(order);
        PointScanShippingBatchVo pointScanShippingBatchVo = DomainCopyUtil.map(shippingBatch, PointScanShippingBatchVo.class);
        pointScanShippingBatchVo.setWareWeight(wareWeightByBatchId);


        EntityWrapper<ShippingSacks> wrapper1 = new EntityWrapper<>();
        wrapper1.eq("shipping_batch_id", shippingBatch.getId());
        Page<ShippingSacks> page = new Page<>(param.getCurrent(), param.getSize());
        Page<ShippingSacks> page1 = shippingSacksService.selectPage(page, wrapper1);
        List<PointScanShippingSacksVo> pointScanShippingSacksVos = DomainCopyUtil.mapList(page1.getRecords(), PointScanShippingSacksVo.class);
        pointScanShippingBatchVo.setShippingSacks(pointScanShippingSacksVos);
        return pointScanShippingBatchVo;
    }


    /**
     * 过点扫描获取扫描用户是否有权限、运单状态等信息
     *
     * @param param
     * @return
     */
    public Response appPointScan(PointScanParam param) throws MyException {

        User loginUser = ShiroUtil.getLoginUser();
        Response response = new Response();
        String trackingNumber1 = param.getTrackingNumber();
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher trackingNumber2 = p.matcher(trackingNumber1);
        String trackingNumber = trackingNumber2.replaceAll("").trim();
        if (trackingNumber.length() > 33) {
            trackingNumber = trackingNumber.substring(8, 34);
        }

        //判断扫描类型(1：运单2：麻袋3：批次)
        Integer trackingNumberType = 0;
        EntityWrapper<WayBill> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("tracking_number", trackingNumber);
        WayBill wayBill = wayBillService.selectOne(entityWrapper);
        if (wayBill != null) {
            trackingNumberType = 1;
        } else {
            trackingNumberType = getTrackingNumberType(trackingNumber);
            if (trackingNumberType == -1) {
                response.setStatusCode(ResponseCode.FAILED_CODE);
                response.setMsg("无法找到该单号信息");
                return response;
            }
        }
        //判断当前角色是否有物流过点扫描资格
        //物流过点扫描角色
        List<Role> logisticsPointScanRoles = roleService.getLogisticsPointScanRoles();
        //当前用户角色
        EntityWrapper<UserRole> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id", loginUser.getId());
        List<UserRole> userRoles = userRoleService.selectList(wrapper);
        boolean flag = true;
        for (UserRole userRole : userRoles) {
            int roleId = userRole.getRoleId();
            for (Role logisticsPointScanRole : logisticsPointScanRoles) {
                int rid = logisticsPointScanRole.getId();
                if (roleId == rid) {
                    flag = false;
                    break;
                }
            }
        }
        //无权限
        if (flag) {
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("你暂无此权限");
            return response;
        }

        //根据用户角色获取用户拥有的物流过点扫描的权限点
        List<Long> rids = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            Integer roleId = userRole.getRoleId();
            rids.add(roleId.longValue());
        }
        Map<String, List> map = new HashMap<>();
        map.put("rids", rids);
        List<PointScan> pointScanByRoleId = pointScanService.getPointScanByRoleId(map);
        pointScanByRoleId.sort((o1, o2) -> o1.getRank() < o2.getRank() ? -1 : 1);

        // 尝试查找父面单
        trackingNumber = wayBillService.getNewTrackingNumber(trackingNumber, trackingNumberType);
        //有权限
        List<PointScanRecord> pointScanRecords = pointScanRecordService.selectList(new EntityWrapper<PointScanRecord>()
                .eq("order_tracking_number", trackingNumber)
                .eq("point_type", trackingNumberType)
                .orderBy("rank", false));
        //获取实际扫描的状态
        if (pointScanRecords == null || pointScanRecords.size() == 0) {
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("暂无过点扫描信息");
            return response;
        }
        // 调用方法将完整流程补上
        Collections.reverse(pointScanRecords);
        List<PointScanRecordVo> pointScanRecordVos = dealPointScanRecord(pointScanRecords);
        int index = 0;
        for (int i = 0; i < pointScanRecordVos.size(); i++) {
            if (pointScanRecordVos.get(i).getIsError() != 1){
                index = i;
            }
        }
        List<PointScanRecordVo> collect = new ArrayList<>();
        for (int i = 0; i < pointScanRecordVos.size(); i++) {
            if (i>index){
                collect.add(pointScanRecordVos.get(i));
            }
        }
        if (collect.size() == 0) {
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("此单号已完成最后扫码");
            return response;
        }
        collect.forEach(pointScanRecord -> pointScanRecord.setStateAndStatus(PointScanType.UNFINISHED));
        //获取当前应该扫描的状态
        PointScanRecord pointScanNext = collect.get(0);
        List<Long> unScanIds = collect.stream().map(PointScanRecordVo::getPointScanId).collect(Collectors.toList());

        // 创建返回vo实例
        PointScanVo pointScanVo = new PointScanVo();
        pointScanVo.setPointType(trackingNumberType);
        pointScanVo.setOrderTrackingNumber(trackingNumber);

        pointScanVo.setActualRoleName(pointScanNext.getPointScanName());
        pointScanVo.setActualRoleId(pointScanNext.getPointScanId());
        pointScanVo.setScanUserName(ShiroUtil.getLoginUser().getUsername());
        pointScanVo.setScanState(2);//异常扫描

        // 补扫描
        for (PointScan scanRoleUser : pointScanByRoleId) {
            if (unScanIds.contains(scanRoleUser.getId())) {
                break;
            }
            Optional<PointScanRecordVo> first = pointScanRecordVos.stream()
                    .filter(pointScanRecordVo -> {
                        if (pointScanRecordVo.getPointScanId() != null) {
                            return pointScanRecordVo.getIsError() == 1 && pointScanRecordVo.getPointScanId().equals(scanRoleUser.getId());
                        } else {
                            return false;
                        }
                    })
                    .findFirst();
            if (first.isPresent()) {
                pointScanVo.setScanRoleName(scanRoleUser.getScanName());
                pointScanVo.setScanRoleId(scanRoleUser.getId());
                PointScanRecordVo pointScanRecordVo = first.get();
                pointScanRecordVo.setStateAndStatus(PointScanType.THISTIME_REPLENISH);
                pointScanRecordVo.setComment("您当前所负责的阶段");
                pointScanVo.setPointScanRecords(pointScanRecordVos);
                response.setData(pointScanVo);
                return response;
            }
        }

        // 当前用户的权限
        for (PointScan scanRoleUser : pointScanByRoleId) {
            long scanRoleId = scanRoleUser.getId();
            // 当前用户有最新阶段权限
            if (pointScanNext.getPointScanId() == scanRoleId) {
                pointScanVo.setScanRoleId(scanRoleId);
                pointScanVo.setPointScanRecords(pointScanRecordVos);
                // 设置
                PointScanRecordVo pointScanRecordVoTemp = pointScanRecordVos.stream().filter(pointScanRecordVo -> pointScanRecordVo.getPointScanId().equals(scanRoleId)).findFirst().get();
                pointScanRecordVoTemp.setStateAndStatus(PointScanType.THISTIME);
                pointScanRecordVoTemp.setComment("您当前所负责的阶段");
                pointScanVo.setScanState(1);//正常扫描
                response.setData(pointScanVo);
                log.info("扫描回显：" + pointScanVo);
                return response;
            }
        }
        //当前阶段不是该用户扫码
        for (PointScan scanRoleUser : pointScanByRoleId) {
            Optional<PointScanRecordVo> first = pointScanRecordVos.stream()
                    .filter(pointScanRecordVo -> {
                        return pointScanRecordVo.getIsError() == 1
                                &&
                                pointScanRecordVo.getPointScanId().equals(scanRoleUser.getId());
                    }).findFirst();
            if (first.isPresent()) {
                System.out.println(first.get());
                pointScanVo.setScanRoleName(scanRoleUser.getScanName());
                pointScanVo.setScanRoleId(scanRoleUser.getId());
                break;
            }
        }
        // 将跳过的点设为未处理
        if (pointScanVo.getScanRoleId() != null) {
            for (PointScanRecordVo pointScanRecordVo : pointScanRecordVos) {
                if (pointScanRecordVo.getPointScanId().equals(pointScanVo.getScanRoleId())) {
                    pointScanRecordVo.setStateAndStatus(PointScanType.THISTIME);
                    pointScanRecordVo.setComment("您当前所负责的阶段");
                    break;
                }
            }
        }
        pointScanVo.setPointScanRecords(pointScanRecordVos);
        response.setData(pointScanVo);
        log.info("扫描回显：" + pointScanVo);
        return response;
    }


    /**
     * 后台运单状态列表
     *
     * @param id
     * @return
     */
    public Response pointScanInfoListV2(Long id) throws MyException {
        ShiroUtil.getLoginUser();
        Response response = new Response();
        WayBill wayBill1 = wayBillService.selectById(id);
        if (wayBill1 == null) {
            response.setMsg("无法找到该单号信息");
            return response;
        }
        String trackingNumber1 = wayBill1.getTrackingNumber();
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher trackingNumber2 = p.matcher(trackingNumber1);
        String trackingNumber = trackingNumber2.replaceAll("").trim();
        if (trackingNumber.length() >= 30) {
            trackingNumber = trackingNumber.substring(8, trackingNumber.length());
        }

        //判断扫描类型(1：运单2：麻袋3：批次)
        Integer trackingNumberType = 0;
        EntityWrapper<WayBill> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("tracking_number", trackingNumber);
        WayBill wayBill = wayBillService.selectOne(entityWrapper);
        if (wayBill != null) {
            trackingNumberType = 1;
        } else {
            trackingNumberType = getTrackingNumberType(trackingNumber);
            if (trackingNumberType == -1) {
                response.setStatusCode(ResponseCode.FAILED_CODE);
                response.setMsg("无法找到该单号信息");
                return response;
            }
        }
        //获取当前应该扫描的状态
        trackingNumber = wayBillService.getNewTrackingNumber(trackingNumber, trackingNumberType);
        //返回app的单号状态列表
        EntityWrapper<PointScanRecord> wrapper3 = new EntityWrapper<>();
        wrapper3.eq("order_tracking_number", trackingNumber);
        wrapper3.eq("point_type", trackingNumberType);
        wrapper3.orderBy("rank");
        List<PointScanRecord> pointScanRecordList = pointScanRecordService.selectList(wrapper3);
        //  处理数据
        List<PointScanRecordVo> pointScanRecordVos = dealPointScanRecord(pointScanRecordList);
        if (pointScanRecordVos == null || pointScanRecordVos.size() == 0) {
            response.setMsg("暂无过点扫描信息");
            return response;
        }
        response.setData(pointScanRecordVos);
        return response;
    }

    //过点扫描信息列表
    @Deprecated
    public Response pointScanInfoList(PointScanParam param) {
        ShiroUtil.getLoginUser();
        Response response = new Response();
        String trackingNumber1 = param.getTrackingNumber();
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher trackingNumber2 = p.matcher(trackingNumber1);
        String trackingNumber = trackingNumber2.replaceAll("").trim();
        if (trackingNumber.length() >= 30) {
            trackingNumber = trackingNumber.substring(8, trackingNumber.length());
        }

        //判断扫描类型(1：运单2：麻袋3：批次)
        Integer trackingNumberType;
        EntityWrapper<WayBill> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("tracking_number", trackingNumber);
        WayBill wayBill = wayBillService.selectOne(entityWrapper);
        if (wayBill != null) {
            trackingNumberType = 1;
        } else {
            trackingNumberType = getTrackingNumberType(trackingNumber);
            if (trackingNumberType == -1) {
                response.setStatusCode(ResponseCode.FAILED_CODE);
                response.setMsg("无法找到该单号信息");
                return response;
            }
        }
        //有权限
        EntityWrapper<PointScanRecord> wrapper1 = new EntityWrapper<>();
        wrapper1.eq("order_tracking_number", trackingNumber);
        wrapper1.eq("point_type", trackingNumberType);
        wrapper1.orderBy("point_scan_id", false);
        List<PointScanRecord> pointScanRecords = pointScanRecordService.selectList(wrapper1);
        ///获取实际扫描的状态
        if (pointScanRecords == null || pointScanRecords.size() == 0) {
            response.setMsg("暂无过点扫描信息");
            return response;
        }
        PointScanRecord pointScanRecord = pointScanRecords.get(0);

        //获取当前应该扫描的状态
        //返回app的单号状态列表
        EntityWrapper<PointScanRecord> wrapper3 = new EntityWrapper<>();
        wrapper3.eq("order_tracking_number", trackingNumber);
        wrapper3.eq("point_type", trackingNumberType);
        wrapper3.orderBy("scan_time", false);
        List<PointScanRecord> pointScanRecordList = pointScanRecordService.selectList(wrapper3);
        PointScanVo pointScanVo = new PointScanVo();

        EntityWrapper<PointScanRecord> wrapper4 = new EntityWrapper<>();
        wrapper4.eq("order_tracking_number", trackingNumber);
        wrapper4.eq("point_type", trackingNumberType);
        wrapper4.gt("point_scan_id", 0);
        wrapper4.orderBy("point_scan_id", false);
        List<PointScanRecord> pointScanRecordList1 = pointScanRecordService.selectList(wrapper3);
        EntityWrapper<PointScan> wrapper5 = new EntityWrapper<>();
        wrapper5.eq("scan_type", 1);
        wrapper5.eq("fake_delete", 1);
        wrapper5.orderBy("id", true);
        if (pointScanRecordList1 == null) {
            List<PointScan> pointScans = pointScanService.selectList(wrapper5);
            if (pointScans == null || pointScans.size() == 0) {
                response.setStatusCode(ResponseCode.FAILED_CODE);
                response.setMsg("暂无扫码角色，请在后台配置");
                return response;
            }
            PointScan pointScan = pointScans.get(0);
            pointScanVo.setScanRoleId(pointScan.getId());
            pointScanVo.setScanRoleName(pointScan.getScanName());
        } else {
            PointScanRecord pointScanRecord1 = pointScanRecordList1.get(0);
            //获取当前应该扫描的状态
            wrapper5.gt("id", pointScanRecord1.getPointScanId());
            List<PointScan> pointScanRoles = pointScanService.selectList(wrapper5);
            if (pointScanRoles == null || pointScanRoles.size() == 0) {
                pointScanVo.setScanRoleId(0L);
                pointScanVo.setScanRoleName("无");
            } else {
                PointScan pointScan = pointScanRoles.get(0);
                pointScanVo.setScanRoleId(pointScan.getId());
                pointScanVo.setScanRoleName(pointScan.getScanName());
            }
        }
        // 处理数据
        List<PointScanRecordVo> pointScanRecordVos = dealPointScanRecord(pointScanRecords);

        pointScanVo.setPointType(trackingNumberType);
        pointScanVo.setOrderTrackingNumber(pointScanRecord.getOrderTrackingNumber());
        pointScanVo.setPointScanRecords(pointScanRecordVos);
        response.setData(pointScanVo);
        return response;
    }

    private List<PointScanRecordVo> dealPointScanRecord(List<PointScanRecord> pointScanRecordList) {
        List<PointScanRecordVo> result = new ArrayList<>();

        Map<Long, PointScanRecordVo> map = new HashMap<>();
        Long pointScanId;
        for (PointScanRecord pointScanRecord : pointScanRecordList) {
            PointScanRecordVo pointScanRecordVo = DomainCopyUtil.map(pointScanRecord, PointScanRecordVo.class);
            pointScanId = pointScanRecordVo.getPointScanId();
            if (!map.keySet().contains(pointScanId)) {
                if (pointScanRecordVo.getIsError() == 0) {
                    pointScanRecordVo.setStateAndStatus(PointScanType.FINISHED);
                } else {
                    pointScanRecordVo.setScanTime(null);
                    pointScanRecordVo.setStateAndStatus(PointScanType.UNTREATED);
                }
                pointScanRecordVo.setCurrentStage(false);
                map.put(pointScanId, pointScanRecordVo);
                result.add(pointScanRecordVo);
            } else {
                // 不展示子状态
//                if (map.get(pointScanId).getChildren() == null) {
//                    map.get(pointScanId).setChildren(Arrays.asList(pointScanRecord));
//                } else {
//                    map.get(pointScanId).getChildren().add(pointScanRecord);
//                }
//                map.get(pointScanId).setStatus(pointScanRecord.getPointScanName());
            }
        }
        // 补全所有的流程
        result = complementPointScanRecords(result);
        return result;
    }

    /**
     * 提供完整流程
     *
     * @param pointScanRecordVos
     */
    public List<PointScanRecordVo> complementPointScanRecords(List<PointScanRecordVo> pointScanRecordVos) {
        List<PointScanRecordVo> result = new ArrayList<>();
        if (pointScanRecordVos.size() > 0) {
            // 完整流程列表
            List<PointScan> pointScanList;
            // 取第一个过点扫描，获取是哪种流程
            Integer pointType = pointScanRecordVos.get(0).getPointType();
            String orderTrackingNumber = pointScanRecordVos.get(0).getOrderTrackingNumber();
            // 根据类型获取完整流程
            pointScanList = pointScanService.getCompleteProcess(pointType);

            // 现已记录的过点扫描流程id
            List<Long> pointScanIds = pointScanRecordVos.stream().map(PointScanRecordVo::getPointScanId).collect(Collectors.toList());
            for (PointScan pointScan : pointScanList) {
                if(pointScanIds.contains(pointScan.getId())){
                    result.add(pointScanRecordVos.stream().filter(pointScanRecordVo -> pointScanRecordVo.getPointScanId().equals(pointScan.getId())).findFirst().get());
                }else{
                    PointScanRecordVo pointScanRecordVo = new PointScanRecordVo();
                    pointScanRecordVo.setPointType(pointType);
                    pointScanRecordVo.setPointScanId(pointScan.getId());
                    pointScanRecordVo.setPointScanName(pointScan.getScanName());
                    pointScanRecordVo.setSysRecord(pointScan.getSystemType() == 1 ? 1 : 0);
                    pointScanRecordVo.setOrderTrackingNumber(orderTrackingNumber);
                    pointScanRecordVo.setStateAndStatus(PointScanType.UNFINISHED);
                    pointScanRecordVo.setCurrentStage(false);
                    pointScanRecordVo.setIsError(1);
                    pointScanRecordVo.setRank(pointScan.getRank());
                    result.add(pointScanRecordVo);
                }
            }
        }
        Collections.reverse(result);
        Optional<PointScanRecordVo> first = result.stream().filter(pointScanRecordVo -> pointScanRecordVo.getIsError() != 1).findFirst();
        if (first.isPresent()){
            PointScanRecordVo pointScanRecordVo = first.get();
            pointScanRecordVo.setCurrentStage(true);
            pointScanRecordVo.setStateAndStatus(PointScanType.PRESENT);
        }
        Collections.reverse(result);
        return result;
    }


    /**
     * app确定扫描
     *
     * @param param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer appNotarizePointScan(NotarizePointScanParam param) throws MyException {
        Integer trackingNumberType;
        EntityWrapper<WayBill> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("tracking_number", param.getOrderTrackingNumber());
        WayBill wayBill = wayBillService.selectOne(entityWrapper);
        if (wayBill != null) {
            trackingNumberType = 1;
        } else {
            trackingNumberType = getTrackingNumberType(param.getOrderTrackingNumber());
            if (trackingNumberType == -1) {
                throw new MyException("无法找到该单号信息");
            }
        }
        boolean insert = true;
        if (trackingNumberType == 1) {
            insert = notarizeAddRecord(param.getOrderTrackingNumber(), param.getScanRoleId(), param.getScanRoleName(), 1);
        } else if (trackingNumberType == 2) { // 麻袋的扫描批量操作
            notarizeAddRecord(param.getOrderTrackingNumber(), param.getScanRoleId(), param.getScanRoleName(), 2);
            ShippingSacks shippingSacks = shippingSacksService.selectOne(new EntityWrapper<ShippingSacks>().eq("sacks_number", param.getOrderTrackingNumber()));
            List<WayBill> wayBills = wayBillService.selectList(new EntityWrapper<WayBill>().eq("shipping_sacks_id", shippingSacks.getId()));
            for (WayBill bill : wayBills) {
                insert = insert && notarizeAddRecord(bill.getTrackingNumber(), param.getScanRoleId(), param.getScanRoleName(), 1);
            }
        } else { // 批次的扫描批量操作
            insert = notarizeAddRecord(param.getOrderTrackingNumber(), param.getScanRoleId(), param.getScanRoleName(), 3);
            ShippingBatch shippingBatch = shippingBatchService.selectOne(new EntityWrapper<ShippingBatch>().eq("tracking_number", param.getOrderTrackingNumber()));
            List<ShippingSacks> shippingSacks = shippingSacksService.selectList(new EntityWrapper<ShippingSacks>().eq("shipping_batch_id", shippingBatch.getId()));
            for (ShippingSacks shippingSack : shippingSacks) {
                insert = insert && notarizeAddRecord(shippingSack.getSacksNumber(), param.getScanRoleId(), param.getScanRoleName(), 2);
            }
            List<WayBill> wayBills = wayBillService.selectList(new EntityWrapper<WayBill>().eq("shipping_batch_id", shippingBatch.getId()));
            for (WayBill bill : wayBills) {
                insert = insert && notarizeAddRecord(bill.getTrackingNumber(), param.getScanRoleId(), param.getScanRoleName(), 1);
            }
        }
        return insert ? 1 : 2;
    }

    private boolean notarizeAddRecord(String orderTrackingNumber, Long pointScanId, String pointScanName, int pointType) throws MyException {
        orderTrackingNumber = wayBillService.getNewTrackingNumber(orderTrackingNumber, pointType);
        // 判断当前流程是否正确
        PointScanRecord pointScanRecord = pointScanRecordService.selectOne(new EntityWrapper<PointScanRecord>()
                .eq("order_tracking_number", orderTrackingNumber).eq("point_scan_id", pointScanId));
        if (pointScanRecord != null) {
            pointScanRecord.setIsError(0);
            pointScanRecord.setContent("补扫");
        } else {
            PointScan pointScan = pointScanService.selectById(pointScanId);
            pointScanRecordService.ifRecordByOrder(pointScanId, orderTrackingNumber, pointType);
            pointScanRecord = new PointScanRecord();
            pointScanRecord.setOrderTrackingNumber(orderTrackingNumber);
            pointScanRecord.setPointType(pointType);//单号类型
            pointScanRecord.setSysRecord(0);//0:过点扫描生成状态
            pointScanRecord.setPointScanName(pointScanName);
            pointScanRecord.setPointScanId(pointScanId);
            pointScanRecord.setRank(pointScan.getRank());
        }
        pointScanRecord.setScanUserName(ShiroUtil.getLoginUser().getUsername());
        pointScanRecord.setScanTime(new Date());
        return pointScanRecordService.insertOrUpdate(pointScanRecord); //正常扫描
    }

    // 2021-03-06 LZJ修改
//    public Integer appNotarizePointScan(NotarizePointScanParam param) {
//        Integer scanState = param.getScanState();
//        if (scanState == 1) {
//            PointScanRecord pointScanRecord = new PointScanRecord();
//            pointScanRecord.setOrderTrackingNumber(param.getOrderTrackingNumber());
//            pointScanRecord.setPointType(param.getPointType());//单号类型
//            pointScanRecord.setSysRecord(0);//0:过点扫描生成状态
//            pointScanRecord.setScanUserName(ShiroUtil.getLoginUser().getUsername());
//            pointScanRecord.setPointScanName(param.getScanRoleName());
//            pointScanRecord.setScanTime(new Date());
//            pointScanRecord.setPointScanId(param.getScanRoleId());
//            boolean insert = pointScanRecordService.insert(pointScanRecord); //正常扫描
//            if (insert) {
//                return 1;
//            }
//            return 2;
//        } else if (scanState == 2) {
//            //实际阶段ID
//            Long actualRoleId = param.getActualRoleId();
//            //当前阶段ID
//            Long scanRoleId = param.getScanRoleId();
//            EntityWrapper<PointScan> wrapper = new EntityWrapper<>();
//            wrapper.eq("scan_type", 1);//物流扫描点
//            wrapper.eq("fake_delete", 1);
//            wrapper.ge("id", actualRoleId);
//            wrapper.lt("id", scanRoleId);
//            //异常阶段
//            List<PointScan> pointScanRoles = pointScanService.selectList(wrapper);
//            for (PointScan pointScan : pointScanRoles) {
//                PointScanRecord pointScanRecord = new PointScanRecord();
//                pointScanRecord.setOrderTrackingNumber(param.getOrderTrackingNumber());
//                pointScanRecord.setPointType(param.getPointType());//运单类型
//                pointScanRecord.setSysRecord(0);//0:过点扫描生成状态
//                pointScanRecord.setScanUserName("无");
//                pointScanRecord.setPointScanName(pointScan.getScanName());
//                pointScanRecord.setScanTime(new Date());
//                pointScanRecord.setPointScanId(param.getScanRoleId());
//                pointScanRecordService.insert(pointScanRecord);
//            }
//            PointScanRecord pointScanRecord = new PointScanRecord();
//            pointScanRecord.setOrderTrackingNumber(param.getOrderTrackingNumber());
//            pointScanRecord.setPointType(param.getPointType());//运单类型
//            pointScanRecord.setSysRecord(0);//0:过点扫描生成状态
//            pointScanRecord.setScanUserName(ShiroUtil.getLoginUser().getUsername());
//            pointScanRecord.setPointScanName(param.getScanRoleName());
//            pointScanRecord.setScanTime(new Date());
//            pointScanRecord.setPointScanId(param.getScanRoleId());
//            pointScanRecordService.insert(pointScanRecord);
//            return 1;
//        }
//        return -1;
//    }


    //判断单号类型
    public Integer getTrackingNumberType(String trackingNumber) {
        //批次
        EntityWrapper<ShippingBatch> wrapper1 = new EntityWrapper<>();
        wrapper1.eq("tracking_number", trackingNumber);
        ShippingBatch shippingBatch = shippingBatchService.selectOne(wrapper1);
        //麻袋
        EntityWrapper<ShippingSacks> wrapper = new EntityWrapper<>();
        wrapper.eq("sacks_number", trackingNumber);
        ShippingSacks shippingSacks = shippingSacksService.selectOne(wrapper);
        if (shippingBatch != null) {
            return 3;//批次
        } else if (shippingSacks != null) {
            return 2;//麻袋
        }
        return -1;
    }

    //之前
    //过点扫描
   /* public Response appPointScan(PointScanParam param){
        User loginUser = ShiroUtil.getLoginUser();
        Response response = new Response();
        String trackingNumber = param.getTrackingNumber();
        if (trackingNumber==null){
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("参数错误");
            return response;
        }

        //是否有配置角色
        List<PointScanRole> pointScanRoles1 = pointScanRoleService.selectList(null);
        if (pointScanRoles1 == null){
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("暂无扫码角色，请在后台配置");
            return response;
        }

        //判断扫描类型(1：运单2：麻袋3：批次)
        Integer trackingNumberType = 0;
        EntityWrapper<WayBill> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("tracking_number",trackingNumber);
        WayBill wayBill = wayBillService.selectOne(entityWrapper);
        if (wayBill!=null){
            trackingNumberType=1;
        }else {
            trackingNumberType=getTrackingNumberType(trackingNumber);
            if (trackingNumberType==-1){
                response.setStatusCode(ResponseCode.FAILED_CODE);
                response.setMsg("无法找到该单号信息");
                return response;
            }
        }
        //判断当前角色是否有过点扫描资格
        //无
        EntityWrapper<ScanRoleUser> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id",loginUser.getId());
        List<ScanRoleUser> scanRoleUsers = scanRoleUserService.selectList(wrapper);
        if (scanRoleUsers==null || scanRoleUsers.size()==0){
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("你暂无此权限");
            return response;
        }
        //有
        EntityWrapper<PointScanRecord> wrapper1 = new EntityWrapper<>();
        wrapper1.eq("order_tracking_number",trackingNumber);
        wrapper1.eq("point_type",trackingNumberType);
        wrapper1.orderBy("scan_role_id",false);
        List<PointScanRecord> pointScanRecords = pointScanRecordService.selectList(wrapper1);
        //过点扫描最新状态
        PointScanRecord pointScanRecord = pointScanRecords.get(0);
        //获取当前应该扫描的状态
        EntityWrapper<PointScanRole> wrapper2 = new EntityWrapper<>();
        wrapper2.gt("id",pointScanRecord.getScanRoleId());
        List<PointScanRole> pointScanRoles = pointScanRoleService.selectList(wrapper2);
        if (pointScanRoles==null || pointScanRoles.size()==0){
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("此单号已完成最后扫码");
            return response;
        }
        //获取当前应该扫描的状态
        PointScanRole pointScanRole = pointScanRoles.get(0);

        //返回app的单号状态列表
        EntityWrapper<PointScanRecord> wrapper3 = new EntityWrapper<>();
        wrapper3.eq("order_tracking_number",trackingNumber);
        wrapper3.eq("point_type",trackingNumberType);
        wrapper3.orderBy("scan_time",false);
        List<PointScanRecord> pointScanRecordList = pointScanRecordService.selectList(wrapper3);



        for (ScanRoleUser scanRoleUser : scanRoleUsers) {
            long scanRoleId = scanRoleUser.getScanRoleId();
            //当前用户有最新阶段权限
            if (pointScanRole.getId()==scanRoleId || (pointScanRecords.size()==1 && scanRoleId==pointScanRoles1.get(0).getId())){
                PointScanVo pointScanVo = new PointScanVo();
                pointScanVo.setScanRoleId(scanRoleId);
                pointScanVo.setPointType(trackingNumberType);
                pointScanVo.setOrderTrackingNumber(pointScanRecord.getOrderTrackingNumber());
                pointScanVo.setPointScanRecords(pointScanRecordList);
                pointScanVo.setScanRoleName(pointScanRole.getRoleName());
                pointScanVo.setScanUserName(loginUser.getUsername());
                pointScanVo.setScanState(1);//正常扫描
                response.setData(pointScanVo);
                return response;
            }
        }
        //当前阶段不是该用户扫码
        PointScanVo pointScanVo = new PointScanVo();
        pointScanVo.setPointType(trackingNumberType);
        pointScanVo.setOrderTrackingNumber(pointScanRecord.getOrderTrackingNumber());
        pointScanVo.setPointScanRecords(pointScanRecordList);
        pointScanVo.setScanRoleName(pointScanRole.getRoleName());
        pointScanVo.setScanUserName(ShiroUtil.getLoginUser().getUsername());
        pointScanVo.setScanState(2);//异常扫描
        for (ScanRoleUser scanRoleUser : scanRoleUsers) {
            Long scanRoleId = scanRoleUser.getScanRoleId();
            if (scanRoleId>pointScanRole.getId()){
                PointScanRole pointScanRole1 = pointScanRoleService.selectById(scanRoleId);
                pointScanVo.setActualRoleName(pointScanRole1.getRoleName());
                pointScanVo.setActualRoleId(pointScanRole1.getId());
                break;
            }
        }
        response.setData(pointScanVo);
        return response;
    }*/


}
