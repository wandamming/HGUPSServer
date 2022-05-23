package com.hgups.express.controller.warehousermg;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.google.common.collect.Lists;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.SendOrderInfo;
import com.hgups.express.domain.param.LongIdParam;
import com.hgups.express.domain.param.SendOrderParam;
import com.hgups.express.service.warehousemgi.SendOrderInfoService;
import com.hgups.express.util.MyFileUtil;
import com.hgups.express.util.PathUtils;
import com.hgups.express.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author fanc
 * 2020/9/28 0028-18:43
 */
@Api(description = "寄送中国相关API")
@Slf4j
@RestController
@RequestMapping("sendOrder")
public class SendOrderController {

    @Resource
    private SendOrderInfoService sendOrderInfoService;
    @Autowired
    private HttpServletRequest httpServletRequest;


    @ApiOperation(value = "寄送订单列表")
    @PostMapping("/sendOrderList")
    public Response sendOrderList(@RequestBody SendOrderParam param){
        Response response = new Response();
        List<SendOrderInfo> sendOrderInfoList = sendOrderInfoService.sendOrderList(param);

        Map<Object,Object> result = new HashMap<>();
        int total = sendOrderInfoService.sendOrderListCount(param);
        result.put("total",total);
        result.put("size",param.getSize());
        result.put("current",param.getCurrent());
        result.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);//总条数
        result.put("records",sendOrderInfoList);
        response.setStatusCode(200);
        response.setData(result);
        return response;

    }

    @ApiOperation(value = "提交寄送订单")
    @PostMapping("/addSendOrder")
    public Response addSendOrder(@RequestBody SendOrderInfo param){
        Response response = new Response();
        boolean b = sendOrderInfoService.addSendOrder(param);
        if (b){
            response.setStatusCode(200);
            response.setMsg("已提交订单");
            return response;
        }
        response.setStatusCode(201);
        response.setMsg("提交失败");
        return response;
    }
    @ApiOperation(value = "签收寄送订单")
    @PostMapping("/updateSendOrderState")
    public Response updateSendOrderState(@RequestBody LongIdParam param){
        Response response = new Response();
        boolean b = sendOrderInfoService.updateSendOrderState(param.getIds());
        if (b){
            response.setStatusCode(200);
            response.setMsg("签收成功");
            return response;
        }
        response.setStatusCode(201);
        response.setMsg("签收失败");
        return response;
    }

    @ApiOperation(value = "删除寄送订单")
    @PostMapping("/deleteSendOrder")
    public Response deleteSendOrder(@RequestBody LongIdParam param){
        Response response = new Response();
        sendOrderInfoService.deleteSendOrder(param.getIds());
        response.setStatusCode(200);
        response.setMsg("签收成功");
        return response;
    }


    @ApiOperation(value = "导出入库单信息Excel")
    @GetMapping("/exportSendOrder")
    public ResponseEntity exportSendOrder(@RequestParam List ids) {
        ShiroUtil.getLoginUserId();
        List<SendOrderInfo> sendOrderInfoList = sendOrderInfoService.selectBatchIds(ids);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//设置日期格式
        List<String> headRow1 = Lists.newArrayList("序号", "中文名称","英文名称", "数量", "价值"
                , "体积重（磅）", "重量", "保险金额", "发货方式", "运费", "收货人","收货人电话一","收货人电话二"
                , "收货人地址", "收货人", "发货人电话", "发货人地址", "寄送状态", "创建时间", "寄送时间"
                , "签收时间", "备注", "图片地址");
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.writeHeadRow(headRow1);
        for (int i = 0; i < headRow1.size(); i++) {
            writer.autoSizeColumn(i);
            if (i == 0) {
                writer.setColumnWidth(i, 12);
            }else {
                writer.setColumnWidth(i, 30);
            }
        }
        List i = new ArrayList();
        sendOrderInfoList.forEach(x -> {
            i.add("");
            List<Object> dataList = Lists.newArrayList();
            dataList.add(i.size());
            dataList.add(x.getCName());
            dataList.add(x.getEName());
            dataList.add(x.getNumber());
            dataList.add(x.getWorth());
            dataList.add(x.getVolume());
            dataList.add(x.getWeight());
            dataList.add(x.getInsurancePrice());
            dataList.add(x.getDeliveryType());
            dataList.add(x.getFreight());
            dataList.add(x.getReceiveName());
            dataList.add(x.getReceivePhone());
            dataList.add(x.getReceivePhoneTwo());
            dataList.add(x.getReceiveAddress());
            dataList.add(x.getSendName());
            dataList.add(x.getSendPhoneOne());
            dataList.add(x.getSendAddress());

            if (x.getState()==1){
                dataList.add("待寄送");
            }else if (x.getState()==2){
                dataList.add("寄送中");
            }else if (x.getState()==3){
                dataList.add("已签收");
            }else {
                dataList.add("--");
            }

            Date createTime = x.getCreateTime();//创建时间
            Date sendTime = x.getSendTime();//处理时间
            Date signTime = x.getSignTime();//出库时间

            if (null!=createTime){
                dataList.add(df.format(createTime));
            }else {
                dataList.add("--");
            }
            if (null!=sendTime){
                dataList.add(df.format(sendTime));
            }else {
                dataList.add("--");
            }
            if (null!=signTime){
                dataList.add(df.format(signTime));
            }else {
                dataList.add("--");
            }

            writer.writeRow(dataList);
        });

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "寄送中国订单列表.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @ApiOperation(value = "上传寄送中国运单图片")
    @PostMapping("/uploadSendOrderImg")
    public Response uploadSendOrderImg(MultipartFile file, HttpServletRequest request,@RequestParam(value = "comment")String comment){

        Response response = new Response();
        if (file == null) {
            response.setStatusCode(600);
            response.setMsg("文件丢失,请重新上传");
            return response;
        }
        //文件名
        String filename = file.getOriginalFilename();

        FileOutputStream outputStream = null;
        InputStream fileSource = null;
        String tempFileName = "";
        String tempFileNameUrl = "";
        String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() +"/static/warehouse/img/";//存储路径

        //String path = request.getSession().getServletContext).getRealPath("warehouse/img/"); //文件存储位置
        String path = PathUtils.resDir + "warehouse/sendOrderImg/";
        try {
            fileSource= file.getInputStream();
            tempFileName = path+file.hashCode()+System.currentTimeMillis()+filename;
            tempFileNameUrl = returnUrl+file.hashCode()+System.currentTimeMillis()+filename;

            System.out.println("文件地址--======"+tempFileName);
            System.out.println("http地址--------"+tempFileNameUrl);
            //tempFile指向临时文件
            File tempFile = new File(tempFileName);
            if(!tempFile.exists()) {
                tempFile.getParentFile().mkdirs();
                tempFile.createNewFile();
            }
            //outputStream文件输出流指向这个临时文件

            outputStream = new FileOutputStream(tempFile);

            byte[]  b = new byte[1024];
            int n;
            while((n=fileSource.read(b)) != -1){
                outputStream.write(b, 0, n);
            }
        }catch (IOException e){
            e.printStackTrace();
            response.setStatusCode(203);
            response.setMsg("文件上传出错");
            return response;
        }finally {
            //关闭输入输出流
            if (null!=outputStream){
                try {
                    outputStream.close();
                    fileSource.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        SendOrderInfo sendOrderInfo = new SendOrderInfo();
        sendOrderInfo.setImgUrl(tempFileNameUrl);
        sendOrderInfo.setComment(comment);
        sendOrderInfo.setCreateTime(new Date());
        sendOrderInfoService.insert(sendOrderInfo);
        response.setStatusCode(200);
        response.setMsg("上传成功");
        return response;
    }
}
