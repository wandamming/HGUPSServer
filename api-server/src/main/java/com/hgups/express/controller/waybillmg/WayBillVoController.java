package com.hgups.express.controller.waybillmg;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.exception.MyException;
import com.hgups.express.service.adminmgi.ShippingBatchService;
import com.hgups.express.service.waybillmgi.*;
import com.hgups.express.util.MyFileUtil;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.util.USPSApi;
import com.hgups.express.vo.WayBillVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author fanc
 * 2020/6/4 0004-13:40
 */
@Api(description = "运单接口")
@Slf4j
@RestController
@RequestMapping("/wayBillVo")

public class WayBillVoController {

    @Resource
    private WayBillVoService wayBillVoService;

    @Resource
    private WayBillService wayBillService;

    @Resource
    private ProvinceService provinceService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private BatchRecordService batchRecordService;

    @Resource
    private ShippingBatchService shippingBatchService;
    @Resource
    private ZoneService zoneService;

    @ApiOperation(value = "创建运单接口")
    @PostMapping("/createWayBillVo")
    public Response createWayBill(@ApiParam(value = "运单参数") @RequestBody WayBillVo wayBill){
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        try {
            response = wayBillVoService.createWaybillSync(wayBill);
            if (200==response.getStatusCode()){
                return response;
            }
            String code = String.valueOf(response.getStatusCode());
            return wayBillVoService.statusCodeMsg(code);
        } catch (MyException e) {
            response.setResponseByErrorMsg(e.getMessage());
            return response;
        }
    }

    //批量打单
    @ApiOperation(value = "批量打单")
    @PostMapping("/createBatchWayBill")
    public Response createBatchWayBill(@RequestBody BatchWayBill param) throws InterruptedException, ExecutionException{
        Long loginUserId = ShiroUtil.getLoginUserId();
        BatchRecord batchRecord = new BatchRecord();
        batchRecord.setChannel(param.getChannel());
        batchRecord.setUserId(loginUserId);
        batchRecord.setBatchName(param.getBatchName());

        //批量id
        Integer  moreId= batchRecordService.setBatchRecord(batchRecord);
        Response response = new Response();
        int count = 0;
        int i = 0;
        List<BatchWayBillParam> batchWayBill = param.getBatchWayBill();

        Map<String,Object> result = new HashMap<String,Object>();
        List<InsertBatchWayBillError> errorMsgList = new ArrayList<>();
        boolean flag = false;


        int index = 0;
        List<USPSApi.Address> srcs = new ArrayList<>();

        for(BatchWayBillParam batchParam:batchWayBill){
            index++;
            // 必须检验地址
            //batchParam.setCheckAddress(true);

            if (batchParam.getCheckAddress()) {
                USPSApi.Address address = new USPSApi.Address();
                address.state = batchParam.getReceiveProvince();
                address.city = batchParam.getReceiveCity();
                address.zipCode5 = batchParam.getReceivePostalCode();
                address.zipCode4 = batchParam.getReceivePostalCodet();
                address.address1 = batchParam.getReceiveAddressOne();
                address.address2 = batchParam.getReceiveAddressTwo();
                address.index = index;
                srcs.add(address);
            }
        }
        List<USPSApi.Address> addresses = USPSApi.batchValidateAddress(srcs);


        for (int j=0;j<addresses.size();j++){
            InsertBatchWayBillError errorMsg = new InsertBatchWayBillError();
            if (!addresses.get(j).isValid) {
                errorMsg.setErrorCode(311);
                errorMsgList.add(errorMsg);
                continue;
            }
            batchWayBill.get(j).setReceiveCarrierRoute(addresses.get(j).getCarrierRoute());
            batchWayBill.get(j).setReceiveDeliveryPoint(addresses.get(j).getDeliveryPoint());
        }

        long begin = System.currentTimeMillis();
        for(BatchWayBillParam batchParam:batchWayBill){
            InsertBatchWayBillError errorMsg = new InsertBatchWayBillError();
            i++;
            batchParam.setMoreId(moreId);

            /*
             *   ------多线程 ------
             */
            try {
                ListenableFuture<String> waybillString = wayBillVoService.batchCreateWaybill(param.getSender(),batchParam,param.getChannel(),batchParam.getCheckAddress(),loginUserId);
                String code = waybillString.get();
                Response res = wayBillVoService.statusCodeMsg(code);
                if(200==res.getStatusCode()){
                    errorMsg.setErrorCode(res.getStatusCode());
                    count++;
                }
                if(181==res.getStatusCode()){
                    flag= true;
                    errorMsg.setErrorCode(res.getStatusCode());
                    errorMsg.setErrorIndex(i-1);
                    errorMsg.setErrorMessage("表格第"+i+"条(Excel第"+(i+2)+"行)运单错误："+res.getMsg());
                    errorMsgList.add(errorMsg);
                    batchRecord.setBatchSuccess(count);
                    batchRecord.setBatchSum(batchWayBill.size());
                    batchRecord.setBatchCreateTime(new Date());
                    batchRecordService.updateById(batchRecord);
                    result.put("batchSum",batchWayBill.size());
                    result.put("success",count);
                    result.put("failure",(batchWayBill.size()-count));
                    result.put("errorMsg",errorMsgList);
                    response.setMsg("");
                    response.setData(result);
                    response.setStatusCode(220);
                    return response;
                }
                if(200!=res.getStatusCode()){
                    flag= true;
                    errorMsg.setErrorCode(res.getStatusCode());
                    String msg = res.getMsg();
                    errorMsg.setErrorMessage("表格第"+i+"条(Excel第"+(i+2)+"行)运单错误："+msg);
                    errorMsgList.add(errorMsg);
                }
            } catch (TaskRejectedException e) {
                log.info("线程池满"+e.toString());
                Thread.sleep(1000);
            }
            if (flag){
                errorMsg.setErrorIndex(i-1);
            }
            flag=false;
            //----多线程结束----

        }
        long end = System.currentTimeMillis();
        log.info("批量插入时间----->>>>>>"+(end-begin));
        int failure = batchWayBill.size()-count;
        if(failure==0){
            batchRecord.setBatchSuccess(count);
            batchRecord.setBatchSum(batchWayBill.size());
            batchRecord.setBatchCreateTime(new Date());
            batchRecordService.updateById(batchRecord);

            response.setStatusCode(200);
            result.put("batchSum",batchWayBill.size());
            result.put("success",count);
            result.put("failure",failure);
            InsertBatchWayBillError errorMsg = new InsertBatchWayBillError();
            errorMsg.setErrorMessage("批量打单成功！");
            errorMsgList.add(errorMsg);
            result.put("errorMsg",errorMsgList);
            response.setData(result);
            response.setMsg("批量打单成功！");
            return response;
        }
        batchRecord.setBatchSuccess(count);
        batchRecord.setBatchSum(batchWayBill.size());
        batchRecord.setBatchCreateTime(new Date());
        batchRecordService.updateById(batchRecord);
        response.setStatusCode(220);
        result.put("batchSum",batchWayBill.size());
        result.put("success",count);
        result.put("failure",failure);
        result.put("errorMsg",errorMsgList);
        response.setMsg("");
        response.setData(result);
        return response;
    }




    @ApiOperation("导出批量模板Excel")
    @GetMapping(value = "/exportBatchWayBillTemplateExcel")
    public ResponseEntity exportBatchWayBillTemplateExcel() {
        List<String> headRow1 = Lists.newArrayList("收件人信息","","","","","","","","","","包裹信息","","","","","","","","","", "面单备注","", "物品列表","","","","","","","");
        List<String> headRow2 = Lists.newArrayList("姓名（必填）", "公司（选填）", "国家（必填）", "州/省（必填）", "城市（必填）", "主要地址（必填）"
                , "门牌号（选填）", "邮编（必填)", "电话所属国家（必填）", "手机号（必填）","重量（磅）（必填）","长度（英寸）（必填）","宽度（英寸）（必填）","高度（英寸）（必填）"
                ,"英文包裹描述（必填）","包裹形状（必填）","物品类型（必填）","其他说明(选填)","是否为规则长方体（true,false：不填默认为true）","是否为软包裹(true,false：不填默认为false）","备注1（选填,长度不得超过40）","备注2（选填,长度不得超过40）","中文名称（必填）","英文名称（必填）","单位价格（美元）","单位重量(磅) (必填)","数量(必填)","产地(必填)"
                ,"HS编码(选填)","申报要素(选填，中文，使用’|’分隔各项要素)");
        ExcelWriter writer = ExcelUtil.getWriter();

        //合并单元格
        writer.renameSheet(0, "批量导入模板");
        writer.merge(0,0,0,10,headRow1,true);
        writer.merge(0,0,11,20,headRow1,true);
        writer.merge(0,0,21,22,headRow1,true);
        writer.merge(0,0,23,30,headRow1,true);
        writer.writeHeadRow(headRow1);
        writer.writeHeadRow(headRow2);

        //设置单元格格式为文本格式
        Sheet sheet = writer.getSheet();
        Workbook workbook = sheet.getWorkbook();
        HSSFCellStyle textStyle = (HSSFCellStyle) workbook.createCellStyle();
        HSSFDataFormat format = (HSSFDataFormat) workbook.createDataFormat();
        textStyle.setDataFormat(format.getFormat("@"));
        //设置第七列单元格格式为文本
        sheet.setDefaultColumnStyle(7, textStyle);

        for (int i = 0; i < headRow2.size(); i++) {
            writer.autoSizeColumn(i);
            writer.setColumnWidth(i,30);
        }

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "批量导入模版.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiOperation(value = "批量导入运单")
    @PostMapping("/insertBatchWayBill")
    public Response insertBatchWayBill(MultipartFile excelFile){

        Response response = new Response();
        List<BatchWayBillParam> insertEntityList=null;
        if (excelFile == null) {
            response.setStatusCode(600);
            response.setMsg("Excel文件丢失");
            return response;
        }

        List<InsertBatchWayBillError> errorMsgList = new ArrayList<>();
        List<List<Object>> lines = new ArrayList<>();

        try {
            lines = ExcelUtil.getReader(excelFile.getInputStream()).read();
        } catch (Exception e) {
            log.error("parse error:", e);
            response.setStatusCode(601);
            response.setMsg("文件解析异常，请检查excel文件格式是否正确!");
            return response;
        }
        insertEntityList = Lists.newArrayList();
        int nid = 0;
        int f = 0;
        boolean failure = false;
        List<Integer> errorIndex = new ArrayList<>();
        for (int i = 2; i < lines.size(); i++) {
            nid++;
            List<Object> line = lines.get(i);
            //InsertBatchWayBillError errorMsg = new InsertBatchWayBillError();
            int index = 0;


            BatchWayBillParam batchWayBillParam = new BatchWayBillParam();
            batchWayBillParam.setNid(nid);
                /*
                  收件人
                */
            String receiveName = "";
            //姓名
            try {
                receiveName = String.valueOf(line.get(index++));
                if("null".equals(receiveName)||""==receiveName){
                    receiveName = "";
                    throw  new NullPointerException("NullPointerException");
                }

            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }
            //公司
            String receiveCompany = String.valueOf(line.get(index++));
            if ("null".equals(receiveCompany)){
                receiveCompany="";
            }
            //国家
            String receiveCountries = "";
            try {
                receiveCountries = String.valueOf(line.get(index++));
                if("null".equals(receiveCountries)||""==receiveCountries){
                    receiveCountries = "";
                    throw  new NullPointerException("NullPointerException");
                }
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }
            //省份
            String receiveProvince = "";
            try {
                receiveProvince = String.valueOf(line.get(index++));
                if(""==receiveProvince){
                    receiveProvince="";
                    throw  new NullPointerException("NullPointerException");
                }else{
                    List<Province> provinces = provinceService.selectList(null);
                    boolean flag = true;
                    for (Province province:provinces){
                        String proEname = province.getProEname();//简称
                        String proEnglish = province.getProEnglish();//英文全称
                        if (proEname.equalsIgnoreCase(receiveProvince)||proEnglish.equalsIgnoreCase(receiveProvince)){
                            flag = false;
                            receiveProvince=proEname;
                            break;
                        }
                    }
                    if (flag){
                        throw  new ArithmeticException("ArithmeticException ");
                    }
                }
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }
            //城市
            String receiveCity = "";
            try {
                receiveCity = String.valueOf(line.get(index++));
                if("null".equals(receiveCity)||""==receiveCity){
                    receiveCity = "";
                    throw  new NullPointerException("NullPointerException");
                }
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }

            //地二
            String receiveAddressTwo = "";
            try {
                receiveAddressTwo = String.valueOf(line.get(index++));
                if("null".equals(receiveAddressTwo)||""==receiveAddressTwo){
                    receiveAddressTwo = "";
                    throw  new NullPointerException("NullPointerException");
                }
            }catch (Exception e){
                failure = true;

                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }

            //地址一
            String receiveAddressOne = String.valueOf(line.get(index++));
            if ("null".equals(receiveAddressOne)){
                receiveAddressOne="";
            }

            //邮政编码
            String receivePostalCodes = "";
            String receivePostalCode = "";
            String receivePostalCodet = "";
            try {
                receivePostalCodes = String.valueOf(line.get(index++));
                if("null".equals(receivePostalCodes)||""==receivePostalCodes){
                    receivePostalCodes = "";
                    throw  new NullPointerException("NullPointerException");
                }
                String[] str=receivePostalCodes.split("-");
                if (str.length>1){
                    receivePostalCode = str[0];
                    receivePostalCodet = str[1];
                }else {
                    receivePostalCode = receivePostalCodes;
                }
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }

            //手机号所属国家
            String receivePhoneCountries = "";
            try {
                receivePhoneCountries = String.valueOf(line.get(index++));
                if("null".equals(receivePhoneCountries)||""==receivePhoneCountries){
                    receivePhoneCountries = "";
                    throw  new NullPointerException("NullPointerException");
                }
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }

            //手机号
            String receivePhone = "";
            try {
                receivePhone = String.valueOf(line.get(index++));
                if("null".equals(receivePhone)||""==receivePhone){
                    receivePhone = "";
                    throw  new NullPointerException("NullPointerException");
                }
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }

            /*
              包裹
            */
            //包裹重量
            double parcelBillWeight = 0;
            try {
                String s = String.valueOf(line.get(index++));
                if("null".equals(s)||""==s){
                    throw  new NullPointerException("NullPointerException");
                }
                parcelBillWeight = Double.parseDouble(s);
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }
            //包裹长度
            float parcelLengths = 0;
            try {
                String s = String.valueOf(line.get(index++));
                if("null".equals(s)||""==s){
                    throw  new NullPointerException("NullPointerException");
                }
                parcelLengths = Float.parseFloat(s);
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }
            //包裹宽度
            float parcelWidth = 0;
            try {
                String s = String.valueOf(line.get(index++));
                if("null".equals(s)||""==s){
                    throw  new NullPointerException("NullPointerException");
                }
                parcelWidth = Float.parseFloat(s);
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }
            //包裹高度
            float parcelHeight = 0;
            try {
                String s = String.valueOf(line.get(index++));
                if("null".equals(s)||""==s){
                    throw  new NullPointerException("NullPointerException");
                }
                parcelHeight = Float.parseFloat(s);
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }

            //包裹中物品描述
            String parcelAritcleDescribe = "";
            try {
                parcelAritcleDescribe = String.valueOf(line.get(index++));
                if("null".equals(parcelAritcleDescribe)||""==parcelAritcleDescribe){
                    parcelAritcleDescribe = "";
                    throw  new NullPointerException("NullPointerException");
                }
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }

            //包裹形状
            String parcelShape = "";
            try {
                parcelShape = String.valueOf(line.get(index++));
                if("null".equals(parcelShape)||""==parcelShape){
                    parcelShape = "";
                    throw  new NullPointerException("NullPointerException");
                }
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }

            String parceItmeCategory = "";
            try {
                parceItmeCategory = String.valueOf(line.get(index++));
                if("null".equals(parceItmeCategory)||""==parceItmeCategory){
                    parceItmeCategory = "";
                    throw  new NullPointerException("NullPointerException");
                }
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }


            String otherDescription = String.valueOf(line.get(index++));
            if ("null".equals(otherDescription)){
                otherDescription="";
            }

            String parcelIsCoubid = String.valueOf(line.get(index++));
            if(!"false".equalsIgnoreCase(parcelIsCoubid)){
                parcelIsCoubid = "true";
            }

            String parcelIsSoft = String.valueOf(line.get(index++));
            if(!"true".equalsIgnoreCase(parcelIsSoft)){
                parcelIsSoft = "false";
            }
            String parcelCommentOne = String.valueOf(line.get(index++));
            if ("null".equals(parcelCommentOne)){
                parcelCommentOne="-";
            }
            String parcelCommentTwo = String.valueOf(line.get(index++));
            if ("null".equals(parcelCommentTwo)){
                parcelCommentTwo="-";
            }




            /*
              物品
            */

            String articleCDescribe = "";
            try {
                articleCDescribe = String.valueOf(line.get(index++));
                if("null".equals(articleCDescribe)||""==articleCDescribe){
                    articleCDescribe = "";
                    throw  new NullPointerException("NullPointerException");
                }
            }catch (Exception e){
                failure = true;

                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }


            String articleEDescribe = "";
            try {
                articleEDescribe = String.valueOf(line.get(index++));
                if("null".equals(articleEDescribe)||""==articleEDescribe){
                    articleEDescribe = "";
                    throw  new NullPointerException("NullPointerException");
                }
            }catch (Exception e){
                failure = true;

                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }


            float articlePrice = 0;
            try {
                String s = String.valueOf(line.get(index++));
                if("null".equals(s)||""==s){
                    throw  new NullPointerException("NullPointerException");
                }
                articlePrice = Float.parseFloat(s);

                if (articlePrice<=0){
                    throw  new SecurityException("SecurityException");
                }
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }


            float articleWeight = 0;
            try {
                String s = String.valueOf(line.get(index++));
                if("null".equals(s)||""==s){
                    throw  new NullPointerException("NullPointerException");
                }
                articleWeight = Float.parseFloat(s);
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }

            Integer articleNumber = 0;
            try {
                String s = String.valueOf(line.get(index++));
                if("null".equals(s)||""==s){
                    throw  new NullPointerException("NullPointerException");
                }
                articleNumber = Integer.parseInt(s);
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }



            String articlePlace = "";
            try {
                articlePlace = String.valueOf(line.get(index++));
                if("null".equals(articlePlace)||""==articlePlace){
                    articlePlace = "";
                    throw  new NullPointerException("NullPointerException");
                }
            }catch (Exception e){
                failure = true;
                InsertBatchWayBillError error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }



            String articleHsEncode = "";
            String articleDeclaration = "";
            if(line.size()>index){
                articleHsEncode = line.get(index++).toString();
            }
            if(line.size()>index){
                articleDeclaration = line.get(index).toString();
            }

            batchWayBillParam.setArticleCDescribe(articleCDescribe);
            batchWayBillParam.setArticleEDescribe(articleEDescribe);
            if(parcelBillWeight>=1){
                batchWayBillParam.setService("P");
            }else{
                batchWayBillParam.setService("F");
            }
            /*batchWayBillParam.setEntrySite("上海");
            batchWayBillParam.setExportCity("JFK");*/

            if ("US".equalsIgnoreCase(receivePhoneCountries)){
                batchWayBillParam.setReceivePhonePrefix("1");
            }
            if ("CA".equalsIgnoreCase(receivePhoneCountries)){
                batchWayBillParam.setReceivePhonePrefix("86");
            }
            batchWayBillParam.setReceiveName(receiveName);
            batchWayBillParam.setReceiveCompany(receiveCompany);
            batchWayBillParam.setReceiveCountries(receiveCountries);
            batchWayBillParam.setReceiveProvince(receiveProvince);
            batchWayBillParam.setReceiveCity(receiveCity);
            batchWayBillParam.setReceivePostalCode(receivePostalCode);
            if(null==receivePostalCodet){
                receivePostalCodet = "";
            }
            batchWayBillParam.setReceivePostalCodet(receivePostalCodet);
            batchWayBillParam.setReceiveAddressOne(receiveAddressOne);
            batchWayBillParam.setReceiveAddressTwo(receiveAddressTwo);
            batchWayBillParam.setReceivePhone(receivePhone);

            batchWayBillParam.setParcelBillWeight(parcelBillWeight);
            batchWayBillParam.setParcelWidth(parcelWidth);
            batchWayBillParam.setParcelLengths(parcelLengths);
            batchWayBillParam.setParcelHeight(parcelHeight);
            batchWayBillParam.setParcelIsCoubid(parcelIsCoubid);
            batchWayBillParam.setParcelIsSoft(parcelIsSoft);
            batchWayBillParam.setParcelAritcleDescribe(parcelAritcleDescribe);
            batchWayBillParam.setParcelCommentOne(parcelCommentOne);
            batchWayBillParam.setParcelCommentTwo(parcelCommentTwo);
            batchWayBillParam.setParcelShape(parcelShape);
            batchWayBillParam.setParcelItmeCategory(parceItmeCategory);
            batchWayBillParam.setOtherDescription(otherDescription);

            batchWayBillParam.setArticleEDescribe(articleEDescribe);
            batchWayBillParam.setArticleCDescribe(articleCDescribe);
            batchWayBillParam.setArticlePrice(articlePrice);
            batchWayBillParam.setArticleWeight(articleWeight);
            batchWayBillParam.setArticleNumber(articleNumber);
            batchWayBillParam.setArticlePlace(articlePlace);
            batchWayBillParam.setArticleHsEncode(articleHsEncode);
            batchWayBillParam.setArticleDeclaration(articleDeclaration);

            if (failure){
                f++;
                errorIndex.add(i-2);
            }
            failure=false;
            insertEntityList.add(batchWayBillParam);
        }


        if (errorMsgList.size()>0) {
            response.setStatusCode(602);
            response.setMsg("运单数据有误，请检查数据是否正确!");
            Map<Object,Object> result = new HashMap<>();
            result.put("insertEntityList",insertEntityList);
            result.put("errorMsgList",errorMsgList);
            result.put("batchSum",lines.size()-2);
            result.put("success",lines.size()-2-f);
            result.put("failure",f);
            result.put("errorIndex",errorIndex);
            response.setData(result);
            return response;
        }
        response.setStatusCode(200);
        response.setMsg("导入成功");
        response.setData(insertEntityList);
        return response;
    }

    //批量导入异常捕获
    public InsertBatchWayBillError tryCatch(Exception error,int i,int index){
        InsertBatchWayBillError errorMsg = new InsertBatchWayBillError();
        try {
            throw error;
        } catch (ArrayIndexOutOfBoundsException e) {
            errorMsg.setErrorLocation(String.format("第%s行只有%s列", i + 1, index));
        } catch (Exception e) {
            String es = e.toString();
            log.info("批量导入表格错误信息-------->>>"+es);
            boolean NumberFormatException = es.contains("NumberFormatException");
            boolean illegalArgumentException = es.contains("IllegalArgumentException");
            boolean nullPointerException = es.contains("NullPointerException");
            boolean indexOutOfBoundsException = es.contains("IndexOutOfBoundsException");
            boolean ArithmeticException = es.contains("ArithmeticException");
            boolean SecurityException = es.contains("SecurityException");
            if(illegalArgumentException||NumberFormatException){
                errorMsg.setErrorMessage("填写格式错误");
            }
            if(nullPointerException){
                errorMsg.setErrorMessage("必填项不能为空");
            }
            if(indexOutOfBoundsException){
                errorMsg.setErrorMessage("必填项不能为空");
            }
            if(ArithmeticException){
                errorMsg.setErrorMessage("该省/州不存在");
            }
            if(SecurityException){
                errorMsg.setErrorMessage("此处所填不得小于 0");
            }
            errorMsg.setErrorLocation(String.format("第%s行第%s列解析失败", i + 1, index)+"("+errorMsg.getColumName(index)+")："+errorMsg.getErrorMessage());
            return errorMsg;
        }
        return null;
    }


    @ApiOperation(value = "取消运单")
    @PostMapping("/cancelWayBill")
    public Response cancelWayBill(@RequestBody CancelWayBillPrarm param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        try {
            response = wayBillVoService.cancelWayBill(param);
        } catch (MyException e) {
            response.setResponseByErrorMsg(e.getMessage());
        }
        return response;
    }


    @ApiOperation(value = "批量取消运单")
    @PostMapping("/batchCancelWayBill")
    public Response batchCancelWayBill(@RequestBody BatchCancelWayBillPrarm param) {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        try {
            List<Response> responseList = wayBillVoService.batchCancelWayBill(param);
            List<InsertBatchWayBillError> errorList = new ArrayList<>();
            int success = 0;
            int index = 0;
            for (Response res : responseList) {
                InsertBatchWayBillError error = new InsertBatchWayBillError();
                index++;
                if (200==res.getStatusCode()){
                    success++;
                    continue;
                }
                if (300==res.getStatusCode()){
                    error.setErrorCode(300);
                    error.setErrorIndex(index);
                    error.setErrorMessage(res.getMsg());
                    errorList.add(error);
                    continue;
                }
                if (301==res.getStatusCode()){
                    error.setErrorCode(301);
                    error.setErrorIndex(index);
                    error.setErrorMessage(res.getMsg());
                    errorList.add(error);
                    continue;
                }
                if (302==res.getStatusCode()){
                    error.setErrorCode(302);
                    error.setErrorIndex(index);
                    error.setErrorMessage(res.getMsg());
                    errorList.add(error);
                }
            }
            Map<Object,Object> result = new HashMap<>();
            if (success==responseList.size()){
                response.setStatusCode(200);
                response.setMsg("批量取消成功");
                result.put("batchSum",responseList.size());
                result.put("success",success);
                result.put("failure",responseList.size()-success);
                result.put("errorMsg",errorList);
                response.setData(result);
                return response;
            }
            response.setStatusCode(201);
            response.setMsg("批量取消");
            result.put("batchSum",responseList.size());
            result.put("success",success);
            result.put("failure",responseList.size()-success);
            result.put("errorMsg",errorList);
            response.setData(result);
        } catch (MyException e) {
            response.setResponseByErrorMsg(e.getMessage());
        }
        return response;
    }

    @ApiOperation(value = "恢复运单")
    @PostMapping("/reopenWayBill")
    public Response reopenWayBill(@RequestBody CancelWayBillPrarm param) {
        ShiroUtil.getLoginUserId();
        return wayBillVoService.reopenWayBill(param);
    }

    @ApiOperation(value = "运费估算")
    @PostMapping("/freightCostEstimate")
    public Response freightCostEstimate(@RequestBody FreightCostEstimateParam param) {
        Response response = new Response();

        if (null==param.getReceivePostalCode()||param.getReceivePostalCode().length()<3){
            response.setStatusCode(201);
            response.setMsg("发件人邮件编码有误");
            return response;
        }
        if (null==param.getSenderPostalCode()||param.getSenderPostalCode().length()<3){
            response.setStatusCode(202);
            response.setMsg("收件人邮件编码有误");
            return response;
        }

        DecimalFormat keepDecimal = new DecimalFormat("0.00");

        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            double price2 = wayBillVoService.onTokenGetPrice(param.getBillWeight());
            if(-1==price2){
                response.setMsg("该重量无法计算！");
                response.setStatusCode(182);
                return response;
            }
            String format2 = keepDecimal.format(price2);
            response.setStatusCode(200);
            response.setMsg("预估运费："+format2);
            return response;
        }
        ZoneDto zoneDto = zoneService.calculateZone(param.getReceivePostalCode(), ShiroUtil.getLoginUserId());
        double price = wayBillVoService.getOnePrice(param.getBillWeight(),zoneDto.getZone(),"HGUPS",29L);
        String format = keepDecimal.format(price);
        if(-1==price){
            response.setMsg("该重量无法计算！");
            response.setStatusCode(182);
            return response;
        }

        response.setStatusCode(200);
        response.setMsg("预估运费："+format);
        return response;
    }

    @ApiOperation(value = "进行运单预上线")
    @PostMapping("/uploadShipPartnerState")
    public Response uploadShipPartnerState(@RequestBody ShipPartnerStateParam param) {
        Response resp = new Response();
        log.info(" uploadShipPartnerState param: " + param);
        try {
            boolean  result = wayBillService.uploadShipPartnerState(param);
            if(!result) {
                resp.setStatusCode(ResponseCode.SHIP_FILE_NOT_MATCH_BILL_CODE);
            }
        } catch (MyException e) {
            resp.setResponseByErrorMsg(e.getMessage());
        }
        return resp;
    }


    @ApiOperation(value = "管理员对后程批次预上线")
    @PostMapping("/uploadProcessShipPartnerState")
    public Response uploadProcessShipPartnerState(@RequestBody ProcessShipPartnerStateParam param) {
        Response resp = new Response();
        log.info(" uploadShipPartnerState param: " + String.valueOf(param));
        Integer batchId = param.getBatchId();
        ShippingBatch shippingBatch = shippingBatchService.selectById(batchId);
        if (shippingBatch==null){
            resp.setStatusCode(202);
            resp.setMsg("未找到该批次");
            return resp;
        }
        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.eq("shipping_batch_id",param.getBatchId());
        List<WayBill> wayBillList = wayBillService.selectList(wrapper);
        StringBuilder waybillIds = new StringBuilder();
        for (WayBill wayBill : wayBillList) {
            String s = String.valueOf(wayBill.getTrackingNumber())+",";
            waybillIds.append(s);
        }
        ShipPartnerStateParam sp = new ShipPartnerStateParam();
        sp.setState(param.getState());
        sp.setWayBillNumber(waybillIds.toString());
        try {
            boolean  result = wayBillService.uploadShipPartnerState(sp);
            if(!result) {
                resp.setStatusCode(ResponseCode.SHIP_FILE_NOT_MATCH_BILL_CODE);
            }else {
                shippingBatch.setIsApplyEvent(0);
                shippingBatch.setSpEventState(param.getState());
                shippingBatchService.updateById(shippingBatch);
            }
            resp.setStatusCode(200);
            resp.setMsg("预上线成功");
        } catch (MyException e) {
            resp.setResponseByErrorMsg(e.getMessage());
        }
        return resp;
    }



    @ApiOperation(value = "进行运单的SSF")
    @PostMapping("/updateSSF")
    public Response updateSSF(@RequestBody ShipServiceParam param) {
        Response resp = new Response();
        log.info(" updateSSF param: " + param);
        boolean result = wayBillService.updateSSF(param);
        if(!result) {
            resp.setStatusCode(ResponseCode.SHIP_FILE_NOT_MATCH_BILL_CODE);
        }
        return resp;
    }

    @ApiOperation(value = "后程申请预上线API")
    @PostMapping("/applyEvent")
    public Response applyEvent(@RequestBody IdParam param) {
        Response resp = new Response();
        ShippingBatch shippingBatch = shippingBatchService.selectById(param.getId());
        if(shippingBatch==null){
            resp.setStatusCode(202);
            resp.setMsg("未找到该批次");
            return resp;
        }
        shippingBatch.setIsApplyEvent(1);
        boolean b = shippingBatchService.updateById(shippingBatch);
        if (b){
            resp.setStatusCode(200);
            resp.setMsg("申请预上线成功");
            return resp;
        }
        resp.setStatusCode(201);
        resp.setMsg("申请预上线失败");
        return resp;
    }
}
