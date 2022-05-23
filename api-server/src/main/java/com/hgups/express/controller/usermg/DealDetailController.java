package com.hgups.express.controller.usermg;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.hgups.express.domain.DealDetail;
import com.hgups.express.domain.Outbound;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.WayBill;
import com.hgups.express.domain.param.DealDetailVo;
import com.hgups.express.service.usermgi.DealDetailService;
import com.hgups.express.service.warehousemgi.OutboundService;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.MyFileUtil;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.vo.PageParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author fanc
 * 2020/6/14 0014-13:57
 */
@Api(description = "交易明细API")
@Slf4j
@RestController
@RequestMapping("user")

public class DealDetailController {
    @Resource
    private DealDetailService dealDetailService;
    @Resource
    private WayBillService wayBillService;
    @Resource
    private OutboundService outboundService;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @ApiOperation(value = "获取交易明细")
    @PostMapping("/getDealDetail")
    public Response getDealDetail(@RequestBody PageParameters param) {
        long userId = param.getUserId();
        if (0 == userId || "".equals(userId)) {
            userId = ShiroUtil.getLoginUserId();
        } else {
            userId = param.getUserId();
        }
        List<String> trackingNumbers = param.getTrackingNumbers();
        Integer dealType = param.getDealType();
        EntityWrapper<DealDetail> wrapper = new EntityWrapper<DealDetail>();
        if (null!=param.getCreateTimeBegin()&&!"".equals(param.getCreateTimeBegin())
            &&null!=param.getCreateTimeEnd()&&!"".equals(param.getCreateTimeEnd())){
            wrapper.ge("deal_time", param.getCreateTimeBegin());
            wrapper.le("deal_time", param.getCreateTimeEnd());
        }
        if (null != trackingNumbers && trackingNumbers.size() > 0 && (!"".equals(trackingNumbers.get(0)))) { //判断是否需要根据单号查询
            EntityWrapper<WayBill> wrapper1 = new EntityWrapper<>();
            wrapper1.in("tracking_number", trackingNumbers);
            List<WayBill> wayBillList = wayBillService.selectList(wrapper1);

            EntityWrapper<Outbound> wrapper2 = new EntityWrapper<>();
            wrapper2.in("warehouse_waybill_number", trackingNumbers);
            List<Outbound> outboundList = outboundService.selectList(wrapper2);

            List<Integer> oIds = new ArrayList<>();
            List<Integer> wIds = new ArrayList<>();
            for (WayBill wayBill : wayBillList) {
                wIds.add(wayBill.getId());
            }
            for (Outbound outbound : outboundList) {
                oIds.add(outbound.getId().intValue());
            }
            if (wIds.size()==0){
                wIds.add(-9999);
            }
            if (oIds.size()==0){
                oIds.add(-9999);
            }
            wrapper.andNew().in("way_bill_id",wIds).and().eq("flag",1).or().in("way_bill_id",oIds).and().eq("flag",2);
        }
        if (null != dealType && 0!=dealType) { //交易类型
            wrapper.eq("deal_type", dealType);
        }
        wrapper.eq("user_id", userId);
        wrapper.orderBy("id", false);
        Page<DealDetail> page = new Page<>(param.getCurrent(), param.getSize());
        Page<DealDetail> dealPage = dealDetailService.selectPage(page, wrapper);
        List<DealDetail> records = dealPage.getRecords();
        List<DealDetailVo> dealDetailVos = DomainCopyUtil.mapList(records, DealDetailVo.class);


        Iterator<DealDetailVo> iterator = dealDetailVos.iterator();
        while (iterator.hasNext()){
            DealDetailVo deal = iterator.next();
            if (deal.getFlag()==1 && deal.getWayBillId()>0){
                int wayBillId = deal.getWayBillId();
                WayBill wayBill = wayBillService.selectById(wayBillId);
                if (wayBill==null){
                    //如果waybill为空则从集合中删除当前数据
                    iterator.remove();
                    continue;
                }
                deal.setTrackingNumber(wayBill.getTrackingNumber());
                deal.setBillWeight(wayBill.getBillWeight());
                deal.setPrice(wayBill.getPrice());
                deal.setWarePrice(wayBill.getWarePrice());
                deal.setWareWeight(wayBill.getWareWeight());
            }
            if (deal.getFlag()==2){
                //出库单ID
                int outboundId = deal.getWayBillId();
                Outbound outbound = outboundService.selectById(outboundId);
                if (outbound!=null){
                    String warehouseWaybillNumber = outbound.getWarehouseWaybillNumber();
                    deal.setTrackingNumber(warehouseWaybillNumber);

                    EntityWrapper<WayBill> wrapper3 = new EntityWrapper<>();
                    wrapper3.eq("tracking_number",warehouseWaybillNumber);
                    //出库单对应的运单价格
                    WayBill wayBill = wayBillService.selectOne(wrapper3);
                    if (wayBill==null){
                        //如果waybill为空则从集合中删除当前数据
                        iterator.remove();
                        continue;
                    }
                    deal.setBillWeight(wayBill.getBillWeight());
                    deal.setPrice(wayBill.getPrice());
                    deal.setWarePrice(wayBill.getWarePrice());
                    deal.setWareWeight(wayBill.getWareWeight());
                }
            }
        }
        Map<Object, Object> map = new HashMap<>();
        int total = dealDetailService.selectCount(wrapper);//总页数
        map.put("total", total);
        map.put("current", param.getCurrent());
        map.put("pages", (total % param.getSize()) == 0 ? total / param.getSize() : total / param.getSize() + 1);//总条数
        map.put("records", dealDetailVos);
        return new Response(map);
    }

    @ApiOperation(value = "导出交易明细")
    @GetMapping("/exportDealDetail")
    public ResponseEntity exportDealDetail( @RequestParam List ids) {
        ShiroUtil.getLoginUserId();

        EntityWrapper<DealDetail> wrapper = new EntityWrapper<>();
        if (null!=ids&&ids.size()>0){
            wrapper.in("id",ids);
        }

        List<DealDetail> dealDetails = dealDetailService.selectList(wrapper);
        List<DealDetailVo> dealDetailVos = DomainCopyUtil.mapList(dealDetails, DealDetailVo.class);
        Iterator<DealDetailVo> iterator = dealDetailVos.iterator();
        while (iterator.hasNext()){
            DealDetailVo deal = iterator.next();
            if (deal.getFlag()==1 && deal.getWayBillId()>0){
                int wayBillId = deal.getWayBillId();
                WayBill wayBill = wayBillService.selectById(wayBillId);
                if (wayBill==null){
                    //如果waybill为空则从集合中删除当前数据
                    iterator.remove();
                    continue;
                }
                deal.setTrackingNumber(wayBill.getTrackingNumber());
                deal.setBillWeight(wayBill.getBillWeight());
                deal.setPrice(wayBill.getPrice());
                deal.setWarePrice(wayBill.getWarePrice());
                deal.setWareWeight(wayBill.getWareWeight());
            }
            if (deal.getFlag()==2){
                //出库单ID
                int outboundId = deal.getWayBillId();
                Outbound outbound = outboundService.selectById(outboundId);
                if (outbound!=null){
                    String warehouseWaybillNumber = outbound.getWarehouseWaybillNumber();
                    deal.setTrackingNumber(warehouseWaybillNumber);

                    EntityWrapper<WayBill> wrapper3 = new EntityWrapper<>();
                    wrapper3.eq("tracking_number",warehouseWaybillNumber);
                    //出库单对应的运单价格
                    WayBill wayBill = wayBillService.selectOne(wrapper3);
                    if (wayBill==null){
                        //如果waybill为空则从集合中删除当前数据
                        iterator.remove();
                        continue;
                    }
                    deal.setBillWeight(wayBill.getBillWeight());
                    deal.setPrice(wayBill.getPrice());
                    deal.setWarePrice(wayBill.getWarePrice());
                    deal.setWareWeight(wayBill.getWareWeight());
                }
            }
        }
        List<String> headRow = Lists.newArrayList("序号","ID","运单号","交易时间", "交易金额", "账号余额", "交易类型", "交易状态","打单价格","打单重量","核重价格","核重重量");
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.writeHeadRow(headRow);
        int index = 1;
        List i = new ArrayList();
        dealDetailVos.forEach(x -> {
            i.add("");
            List<Object> dataList = Lists.newArrayList();
            dataList.add(i.size());
            dataList.add(String.valueOf(x.getId()));
            if (x.getFlag()==1 && x.getWayBillId()>0){
                int wayBillId = x.getWayBillId();
                WayBill wayBill = wayBillService.selectById(wayBillId);
                dataList.add(wayBill.getTrackingNumber());
            }else if (x.getFlag()==2){
                int wayBillId = x.getWayBillId();
                Outbound outbound = outboundService.selectById(wayBillId);
                dataList.add(outbound.getWarehouseWaybillNumber());
            }else {
                dataList.add("---");
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//设置日期格式
            String str = df.format(x.getDealTime());
            dataList.add(str);
            dataList.add(String.valueOf(x.getDealAmount()));
            dataList.add(String.valueOf(x.getBalance()));
            String dealType = "";
            if (1==x.getDealType()){
                dealType = "扣费";
            }else if (2==x.getDealType()){
                dealType = "充值";
            }else if (3==x.getDealType()){
                dealType = "退款";
            }else if (4==x.getDealType()){
                dealType = "补扣";
            }
            dataList.add(dealType);
            dataList.add(x.getState()==1?"成功":"失败");

            for (int j = 0; j < dataList.size(); j++) {
                writer.autoSizeColumn(j);
                writer.setColumnWidth(j,30);
            }
            dataList.add(x.getPrice());
            dataList.add(x.getBillWeight());
            dataList.add(x.getWarePrice());
            dataList.add(x.getWareWeight());
            writer.writeRow(dataList);
        });

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "交易记录.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}