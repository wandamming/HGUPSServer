package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.PointScan;
import com.hgups.express.domain.PointScanChild;
import com.hgups.express.domain.PointScanRecord;
import com.hgups.express.domain.WayBill;
import com.hgups.express.exception.MyException;
import com.hgups.express.exception.NoAuthException;
import com.hgups.express.mapper.PointScanRecordMapper;
import com.hgups.express.service.PointScanChildService;
import com.hgups.express.util.ShiroUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fanc
 * 2020/11/5-11:26
 */
@Service
public class PointScanRecordService extends ServiceImpl<PointScanRecordMapper, PointScanRecord> {
    @Resource
    private PointScanService pointScanService;
    @Resource
    private PointScanChildService pointScanChildService;
    @Resource
    private WayBillService wayBillService;

    @Transactional(rollbackFor = Exception.class)
    public void addSysRecord(Integer pointType, String orderTrackingNumber, String pointScanShortName,
                             String pointScanChildShortName, Date scanTime, String content) throws MyException {
        // 查找父面单
        orderTrackingNumber = wayBillService.getNewTrackingNumber(orderTrackingNumber, pointType);
        PointScanChild pointScanChild = null;
        // 当传入子状态时，判断子状态发生的时间对不对
        if (StringUtils.isNotBlank(pointScanChildShortName)) {
            if (StringUtils.isBlank(pointScanShortName)) {
                PointScanRecord pointScanRecordTemp = selectOne(new EntityWrapper<PointScanRecord>()
                        .eq("order_tracking_number", orderTrackingNumber).orderBy("scan_time", false));
                if (pointScanRecordTemp == null) {
                    throw new MyException("运单轨迹异常:没有创建订单的流程");
                }
                PointScan pointScanTemp = pointScanService.selectById(pointScanRecordTemp.getPointScanId());
                List<PointScanChild> pointScanChildList = pointScanChildService.selectList(new EntityWrapper<PointScanChild>()
                        .eq("short_name", pointScanChildShortName));
                List<String> collect = pointScanChildList.stream().map(PointScanChild::getPShortName).collect(Collectors.toList());
                if (!collect.contains(pointScanTemp.getShortName())) {
                    throw new MyException("运单轨迹异常:" + pointScanChildList.get(0).getScanName() + "操作时间节点错误");
                }
                pointScanShortName = pointScanTemp.getShortName();
            }
            pointScanChild = pointScanChildService.selectOne(new EntityWrapper<PointScanChild>().eq("p_short_name", pointScanShortName)
                    .eq("short_name", pointScanChildShortName));
        }
        // 获取当前要添加的过点扫描记录
        PointScan pointScan = pointScanService.selectOne(new EntityWrapper<PointScan>().eq("short_name", pointScanShortName));
        // 判断当前要添加的过点扫描记录是否符合流程，不符合就加上未扫描记录，并返回最后一次记录
        List<Long> recordPointScanId = ifRecordByOrder(pointScan, orderTrackingNumber, pointType);
        PointScanRecord pointScanRecord = new PointScanRecord();
        if (recordPointScanId != null && recordPointScanId.contains(pointScan.getId())) {
            // 补扫
            pointScanRecord = selectOne(new EntityWrapper<PointScanRecord>()
                    .eq("point_scan_id", pointScan.getId())
                    .eq("order_tracking_number", orderTrackingNumber)
                    .orderBy("id"));
            pointScanRecord.setIsError(0);
        } else {
            pointScanRecord.setOrderTrackingNumber(orderTrackingNumber);
            pointScanRecord.setPointType(pointType);//运单类型
            pointScanRecord.setSysRecord(1);//系统生成状态
            // 运单的加入麻袋与加入批次-》在麻袋和批次里分别是关闭麻袋和关闭批次
            if (pointType == 2 && pointScan.getShortName().equals("sack")) {
                pointScan.setScanName("关闭麻袋");
            }
            if (pointType == 3 && pointScan.getShortName().equals("batch")) {
                pointScan.setScanName("关闭批次");
            }
            pointScanRecord.setPointScanName(pointScanChild == null ? pointScan.getScanName() : pointScanChild.getScanName());
            pointScanRecord.setPointScanId(pointScan.getId());
            pointScanRecord.setRank(pointScan.getRank());
        }
        // 设置操作人员
        try {
            pointScanRecord.setScanUserName(ShiroUtil.getLoginUser().getUsername());
        } catch (Exception e) {
            if (pointScanShortName.equals("delivery") || pointScanShortName.equals("signFor")) {
                pointScanRecord.setScanUserName("");// 签收和派送没有登录用户
            } else {
                throw new NoAuthException();
            }
        }
        //设置备注
        if (StringUtils.isNotBlank(pointScan.getDesc())) {
            pointScanRecord.setContent(pointScan.getDesc() + ";");
        } else {
            pointScanRecord.setContent("");
        }
        if (StringUtils.isNotBlank(content)) {
            pointScanRecord.setContent(pointScanRecord.getContent() + content);
        }
        pointScanRecord.setScanTime(scanTime);
        insertOrUpdate(pointScanRecord);
    }

    public List<Long> ifRecordByOrder(Long pointScanId, String orderTrackingNumber, int pointType) throws MyException {
        PointScan pointScan = pointScanService.selectById(pointScanId);
        return ifRecordByOrder(pointScan, orderTrackingNumber, pointType);
    }

    public List<Long> ifRecordByOrder(PointScan pointScan, String orderTrackingNumber, int pointType) throws MyException {
        // 最后一次扫描记录
        List<PointScanRecord> pointScanRecordLast = selectList(new EntityWrapper<PointScanRecord>()
                .eq("order_tracking_number", orderTrackingNumber)
                .orderBy("rank", false));
        Integer pointScanRank = pointScan.getRank();
        if (pointScanRecordLast.size()>0) {
            List<Long> pointScanId = pointScanRecordLast.stream().map(PointScanRecord::getPointScanId).collect(Collectors.toList());
            // 根据类型获取完整流程
            List<PointScan> pointScanList = pointScanService.getCompleteProcess(pointType);
            List<PointScan> missedPointScan = pointScanList.stream().filter(pointScan1 -> {
                return !pointScanId.contains(pointScan1.getId()) && pointScan1.getRank() < pointScanRank;
            }).collect(Collectors.toList());

            if (missedPointScan.size() > 0) {
                //  补齐漏掉的完整流程
                complementMissedPointScan(missedPointScan, orderTrackingNumber, pointType);
//                 throw new MyException(pointScanLast.getScanName() + "之后的流程应该是：" + missedPointScan.get(0).getScanName());
            }
            return pointScanId;
        }
        return null;
    }

    /**
     * 补齐漏掉的完整流程
     *
     * @param missedPointScan     遗漏的过点扫描
     * @param orderTrackingNumber 单号
     * @param pointType           单号类型
     */
    private void complementMissedPointScan(List<PointScan> missedPointScan, String orderTrackingNumber, int pointType) {
        List<PointScanRecord> missedPointScanRecords = new ArrayList<>();
        for (PointScan pointScan : missedPointScan) {
            PointScanRecord pointScanRecord = new PointScanRecord();
            pointScanRecord.setOrderTrackingNumber(orderTrackingNumber);
            pointScanRecord.setPointType(pointType);//运单类型
            pointScanRecord.setSysRecord(pointScan.getSystemType() == 1 ? 1 : 0);//系统/过点状态
            pointScanRecord.setScanUserName("");// 错误扫描
            pointScanRecord.setPointScanName(pointScan.getScanName());
            pointScanRecord.setPointScanId(pointScan.getId());
            pointScanRecord.setRank(pointScan.getRank());
            pointScanRecord.setContent("未扫描");
            pointScanRecord.setIsError(1);
            missedPointScanRecords.add(pointScanRecord);
        }
        insertBatch(missedPointScanRecords);
    }

}
