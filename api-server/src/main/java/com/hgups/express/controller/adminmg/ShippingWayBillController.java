package com.hgups.express.controller.adminmg;

import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.WayBill;
import com.hgups.express.domain.param.*;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.ShiroUtil;
import com.jpay.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/7/20 0020-14:00
 */
@Api(description = "航运运单API")
@RestController
@Slf4j
@RequestMapping("/shippingWayBill")
public class ShippingWayBillController {

    @Resource
    private WayBillService wayBillService;



    @ApiOperation(value = "航运批次运单列表")
    @PostMapping("/getShippingWayBill")
    public Response getShippingWayBill(@RequestBody ShippingWayBillParam param){
        ShiroUtil.getLoginUserId();
        System.out.println(param);
        Response response = new Response();
        param.setCurrent((param.getCurrent()-1)*param.getSize());//起始页
        List<String> trackingNumbers = param.getTrackingNumbers();
        //获取航运批次跟换过面单的运单列表
        List<WayBill> wayBills = wayBillService.selectTrackingList();
        //将传入的更换过的面单的单号换成新单号
        if (null!=trackingNumbers&&trackingNumbers.size()>0&&(!"".equals(trackingNumbers.get(0)))){ //判断是否需要根据单号查询
            for (int i = 0; i <trackingNumbers.size(); i++) {
                for (WayBill wayBill : wayBills) {
                    boolean flag = false;
                    if (trackingNumbers.get(i).equals(wayBill.getTrackingNumber())){
                        trackingNumbers.set(i,wayBill.getNewTrackingNumber());
                        flag = true;
                    }
                    if (flag){
                        break;
                    }
                }
            }
            param.setIsTrackingNumber("1");
        }

        if(null!=param.getShippingSacksNumberString()&&(!"".equals(param.getShippingSacksNumberString()))){
            param.setShippingSacksNumbers(param.getShippingSacksNumberString().split(","));
            if (param.getShippingSacksNumbers().length>0&&(!"".equals(param.getShippingSacksNumbers()[0]))&&null!=(param.getShippingSacksNumbers()[0])&&null!=param.getShippingSacksNumbers()){ //判断是否需要根据单号查询
                param.setIsShippingSacksNumber("1");
                System.out.println("有麻袋ID-------");
            }
        }

        System.out.println("当前页---》》"+param.getCurrent());
        System.out.println("当前页大小---》》"+param.getSize());
        System.out.println(param);

        //获取更换过面单的运单
        EntityWrapper<WayBill> wrapper3 = new EntityWrapper<>();
        wrapper3.in("tracking_number",trackingNumbers);
        wrapper3.andNew().isNotNull("new_tracking_number").or().ne("new_way_bill_id",-1);
        wrapper3.setSqlSelect("tracking_number");
        List<WayBill> wayBillChange = wayBillService.selectList(wrapper3);
        List<ShippingWayBillListParam> wayBillList = wayBillService.allWayBill(param);

        for (int j = 0; j < wayBillList.size();j++) {
            String trackingNumber = wayBillList.get(j).getTrackingNumber();
            for (WayBill bill : wayBillChange) {
                ShippingWayBillListParam wayBill = wayBillList.get(j);
                String trackingNumber1 = bill.getTrackingNumber();
                if (trackingNumber1.equals(trackingNumber)){
                    if (wayBill.getId()==-1){ //新单
                        String newTrackingNumber = wayBill.getNewTrackingNumber();
                        String trackingNumber2 = wayBill.getTrackingNumber();
                        wayBill.setNewTrackingNumber(trackingNumber2);
                        wayBill.setTrackingNumber(newTrackingNumber);
                    }else { //旧单
                        WayBill wayBill1 = wayBillService.selectById(wayBill.getNewWayBillId());
                        String newTrackingNumber = wayBill1.getTrackingNumber();
                        int wayBillId = wayBill.getId();
                        ShippingWayBillListParam billListParam = DomainCopyUtil.map(wayBill1, ShippingWayBillListParam.class);
                        billListParam.setUsername(wayBill.getUsername());
                        wayBillList.set(j,billListParam);
                        wayBillList.get(j).setTrackingNumber(wayBill.getTrackingNumber());
                        wayBillList.get(j).setNewTrackingNumber(newTrackingNumber);
                        wayBillList.get(j).setId(wayBillId);
                    }
                }
            }
        }

        Map<Object,Object> map = new HashMap<>();
        Integer total = wayBillService.countWayBill(param);
        System.out.println("=====total==="+total);
        map.put("current",param.getCurrent()+1);
        map.put("total",total);
        map.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);//总页数
        map.put("records",wayBillList);
        response.setStatusCode(200);
        response.setData(map);
        return response;
    }

    @ApiOperation(value = "批量导入运单号")
    @PostMapping("/importTrackingNumber")
    public Response importTrackingNumber(MultipartFile excelFile){
        ShiroUtil.getLoginUserId();
        if (excelFile == null) {
            return new Response(600,"Excel文件丢失",null);
        }
        Response response = new Response();
        List<ImportTrackingNumberParam> trackingNumberList = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();
        try {
            List<List<Object>> lines = ExcelUtil.getReader(excelFile.getInputStream()).read();
            for (int i = 0; i < lines.size(); i++) {
                ImportTrackingNumberParam trackingNumber = new ImportTrackingNumberParam();
                List<Object> line = lines.get(i);
                int index = 0;
                try {
                    String tracking = String.valueOf(line.get(index++));
                    trackingNumber.setTrackingNumber(tracking);
                    trackingNumberList.add(trackingNumber);
                } catch (ArrayIndexOutOfBoundsException e) {
                    errorMsg.append(String.format("第%s行只有%s列", i + 1, index)).append("\n");
                } catch (Exception e) {
                    System.out.println(e);
                    errorMsg.append(String.format("第%s行第%s列解析失败", i + 1, index)).append("\n");
                }
            }
        } catch (Exception e) {
            log.error("parse error:", e);
            response.setStatusCode(601);
            response.setMsg("文件解析异常，请检查excel文件格式是否正确!");
            return response;
        }

        if (StringUtils.isNotBlank(errorMsg.toString())) {
            response.setStatusCode(602);
            response.setMsg("文件解析异常，请检查excel文件格式是否正确!具体错误信息:" + errorMsg.toString());
            return response;
        }
        response.setStatusCode(200);
        response.setMsg("导入成功");
        response.setData(trackingNumberList);
        return response;
    }


    @ApiOperation(value = "航运批次清关报关运单号")
    @PostMapping("/getShippingWayBillNumber")
    public Response getShippingWayBillNumber(@RequestBody IdParam param){
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        List<ShippingWayBillNumberVo> shippingWayBillNumbers = wayBillService.getShippingWayBillNumber(param.getId());
        response.setData(shippingWayBillNumbers);
        return response;
    }


}
