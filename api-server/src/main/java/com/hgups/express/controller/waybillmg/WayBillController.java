package com.hgups.express.controller.waybillmg;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.service.usermgi.ConfigService;
import com.hgups.express.service.usermgi.UserService;
import com.hgups.express.service.waybillmgi.UserBatchService;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.util.MyFileUtil;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.util.XmlUtils;
import com.hgups.express.vo.PageParameters;
import com.hgups.express.vo.WayBillVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.hgups.express.service.waybillmgi.WayBillVoService.testCreateBill;

/**
 * @author fanc
 * 2020/6/4 0004-13:40
 */
@Api(description = "获取运单信息接口")
@Slf4j
@RestController
@RequestMapping("/wayBill")
public class WayBillController {

    @Resource
    private WayBillService wayBillServiceImp;
    @Resource
    private UserService userService;
    @Resource
    private UserBatchService userBatchService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Resource
    private ConfigService configService;

    final double volatilityUp = 0.05;//分拣浮动值
    final double volatilityDown = -0.05;//分拣浮动值

    @ApiOperation(value = "根据运单ID获取运单Coding")
    @PostMapping("/getCodingById")
    public Response getCodingById(@RequestBody IdParam param){
        Response response = new Response();
        int id = param.getId();
        String coding = wayBillServiceImp.getCodingById(id);
        if (StringUtils.isEmpty(coding)){
            response.setStatusCode(199);
            response.setMsg("获取面单信息出错");
            return response;
        }
        response.setStatusCode(200);
        response.setData(coding);
        return response;
    }


    @ApiOperation(value = "分拣")
    @PostMapping("/weight")
    public Response weight(@RequestBody SortingParam sorting) {
        Response response = new Response();
        String trackingNumber = sorting.getTrackingNumber();
        if (null!=trackingNumber && trackingNumber.length()>33){
            trackingNumber = trackingNumber.substring(8, 34);
        }
        String weight = sorting.getWeight();
        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.eq("tracking_number", trackingNumber);
        WayBill wayBill = wayBillServiceImp.selectOne(wrapper);
        if (null == wayBill) {
            response.setStatusCode(1030);
            response.setMsg("未找到该运单");
            return response;
        }
        if (1 != wayBill.getState()) {
            response.setStatusCode(1031);
            response.setMsg("运单已取消");
            return response;
        }
        double weight1 = Double.parseDouble(weight);
        List<Config> configs = configService.selectList(null);
        double gConversion = Float.parseFloat(configs.get(4).getV());//磅转克
        double weightLb = weight1 / gConversion;
        if (weightLb > 70) {
            response.setStatusCode(1033);
            response.setMsg("重量超过最大重量 31710 克（70 磅）");
            return response;
        }
        double billWeight = wayBill.getBillWeight();
        double difference = weight1 - billWeight;
        if (difference > volatilityUp || difference < volatilityDown) {
            response.setStatusCode(1032);
            response.setMsg("打单重量与核重重量不符合");
            return response;
        }
        return response;
    }

    @ApiOperation(value = "获取运单信息API")
    @PostMapping("/getWayBill")
    public Response getWayBill(@ApiParam(value = "分页参数") @RequestBody() BatchRecordWayBillParam pageParameters) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        if (null == pageParameters.getSize() || null == pageParameters.getCurrent()) {
            pageParameters.setSize(10);
            pageParameters.setCurrent(1);
        }
        List<String> trackingNumbers = pageParameters.getTrackingNumbers();
        Integer isTrackingNumbers = 0;
        if (trackingNumbers.size()!=0){
            isTrackingNumbers=1;
        }
        //获取更换过面单的运单
        EntityWrapper<WayBill> wrapper2 = new EntityWrapper<>();
        wrapper2.in("tracking_number",trackingNumbers);
        wrapper2.andNew().isNotNull("new_tracking_number").or().ne("new_way_bill_id",-1);
        wrapper2.setSqlSelect("tracking_number");
        List<WayBill> wayBills = wayBillServiceImp.selectList(wrapper2);
        /*EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id", loginUserId);
        String channel = pageParameters.getChannel();
        if (StringUtils.isNotEmpty(channel)){
            wrapper.eq("channel", channel.toLowerCase());//大写转换小写
        }
        if (0 < pageParameters.getMoreId()) {
            wrapper.eq("more_id", pageParameters.getMoreId());
        }
        if (0 < pageParameters.getState()) {
            wrapper.eq("state", pageParameters.getState());
        }
        if (null != trackingNumbers && trackingNumbers.size() > 0 && (!"".equals(trackingNumbers.get(0)))) { //判断是否需要根据单号查询
            wrapper.in("tracking_number", trackingNumbers);
        }
        if (!StringUtils.isEmpty(pageParameters.getUserBatchNumber())) {
            EntityWrapper wrapper1 = new EntityWrapper();
            wrapper1.eq("tracking_number",pageParameters.getUserBatchNumber());
            UserBatch userBatch = userBatchService.selectOne(wrapper1);
            wrapper.eq("user_batch_id", userBatch.getId());
        }
        if ((!"".equals(pageParameters.getCreateTimeBegin()))
                && null != pageParameters.getCreateTimeBegin()
                && (!"".equals(pageParameters.getCreateTimeEnd()))
                && null != pageParameters.getCreateTimeEnd()) {
            wrapper.ge("create_time", pageParameters.getCreateTimeBegin());
            wrapper.le("create_time", pageParameters.getCreateTimeEnd());
        }
        wrapper.orderBy("id", false); //倒序
        ------------------------------------------------------------------*/

        Map<String,Object> map = new HashMap<>();
        map.put("current",(pageParameters.getCurrent()-1)*pageParameters.getSize());
        map.put("size",pageParameters.getSize());
        map.put("userId",loginUserId);
        map.put("channel",pageParameters.getChannel().toLowerCase());
        map.put("moreId",pageParameters.getMoreId());
        map.put("state",pageParameters.getState());
        map.put("trackingNumbers",trackingNumbers);
        map.put("userBatchNumber",pageParameters.getUserBatchNumber());
        map.put("createTimeBegin",pageParameters.getCreateTimeBegin());
        map.put("createTimeEnd",pageParameters.getCreateTimeEnd());
        map.put("isTrackingNumbers",isTrackingNumbers);
        List<WayBill> wayBillList = wayBillServiceImp.getUserWayBillList(map);


        for (int j = 0; j < wayBillList.size();j++) {
            String trackingNumber = wayBillList.get(j).getTrackingNumber();
            for (WayBill bill : wayBills) {
                WayBill wayBill = wayBillList.get(j);
                String trackingNumber1 = bill.getTrackingNumber();
                if (trackingNumber1.equals(trackingNumber)){
                    if (wayBill.getNewWayBillId()==-1){ //新单
                        String newTrackingNumber = wayBill.getNewTrackingNumber();
                        String trackingNumber2 = wayBill.getTrackingNumber();
                        wayBill.setNewTrackingNumber(trackingNumber2);
                        wayBill.setTrackingNumber(newTrackingNumber);
                    }else { //旧单
                        WayBill wayBill1 = wayBillServiceImp.selectById(wayBill.getNewWayBillId());
                        String newTrackingNumber = wayBill1.getTrackingNumber();
                        int wayBillId = wayBill.getId();
                        wayBillList.set(j,wayBill1);
                        wayBillList.get(j).setTrackingNumber(wayBill.getTrackingNumber());
                        wayBillList.get(j).setNewTrackingNumber(newTrackingNumber);
                        wayBillList.get(j).setId(wayBillId);
                    }
                }
            }
        }

        int total = wayBillServiceImp.getUserWayBillListCount(map);
        Map<Object, Object> map1 = new HashMap<>();
        map1.put("total", total);
        map1.put("size", pageParameters.getSize());
        map1.put("pages", (total % pageParameters.getSize()) == 0 ? total / pageParameters.getSize() : total / pageParameters.getSize() + 1);
        map1.put("records", wayBillList);
        response.setData(map1);
        return response;

    }

    //未加入批次的运单
    @ApiOperation(value = "获取未加入用户批次运单信息API")
    @PostMapping("/getBatchWayBill")
    public Response getBatchWayBill(@ApiParam(value = "分页参数") @RequestBody PageParameters pageParameters) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();

        List<UserBatchWayBill> userBatchWayBills = wayBillServiceImp.getNotIntoBatchWayBillList(loginUserId);
        int total = wayBillServiceImp.getNotIntoBatchWayBillListCount(loginUserId);
        Map<Object, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("size", pageParameters.getSize());
        map.put("pages", (total % pageParameters.getSize()) == 0 ? total / pageParameters.getSize() : total / pageParameters.getSize() + 1);
        map.put("records", userBatchWayBills);
        response.setData(map);
        return response;
    }

    @ApiOperation(value = "获取未加入航运批次运单信息API")
    @PostMapping("/getShippingBatchWayBill")
    public Response getShippingBatchWayBill(@ApiParam(value = "分页参数") @RequestBody() PageParameters pageParameters) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();

        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.eq("shipping_batch_id", 0);
        wrapper.eq("shipping_sacks_id", 0);
        wrapper.eq("state", 1);
        wrapper.eq("is_problem_parcel", 0);
        wrapper.eq("is_intercept", 0);

        List<WayBill> wayBillList = wayBillServiceImp.getShippingBatchWayBillList();

        int total = wayBillServiceImp.selectCount(wrapper);
        Map<Object, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("size", pageParameters.getSize());
        map.put("pages", (total % pageParameters.getSize()) == 0 ? total / pageParameters.getSize() : total / pageParameters.getSize() + 1);
        map.put("records", wayBillList);
        response.setData(map);
        return response;

    }


    @ApiOperation(value = "运单详情API")
    @PostMapping("/wayBillDetails")
    public Response getWayBillDetails(@RequestBody WayBillParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        WayBillVo vo = wayBillServiceImp.getWayBillDetails(param.getId());
        if (null == vo) {
            response.setStatusCode(803);
            response.setMsg("运单异常！");
        }
        response.setData(vo);
        return response;
    }

    @ApiOperation(value = "当前批次运单数量及总金额")
    @PostMapping("/getBatchNumberAndTotalAmount")
    public Response getBatchNumberAndTotalAmount(@RequestBody IdParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        Map<Object, Object> map = new HashMap<>();
        map.put("batchId", param.getId());
        map.put("userId", loginUserId);
        float sumPrice = wayBillServiceImp.getSumPrice(map);
        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.eq("user_batch_id", param.getId());
        wrapper.eq("user_id", loginUserId);
        int count = wayBillServiceImp.selectCount(wrapper);

        Map<Object, Object> result = new HashMap<>();
        result.put("sumPrice", sumPrice);
        result.put("sumWayBill", count);
        response.setStatusCode(200);
        response.setData(result);
        return response;

    }

    @ApiOperation(value = "导出运单pdf")
    @GetMapping("/exportWayPDF")
    public ResponseEntity batchExportWayPDF(@RequestParam List wayBillList) {
        File file = new File(wayBillServiceImp.batchExportWayPDF(wayBillList));
        ResponseEntity entity = null;
        try {
            Date date = new Date();

            String exportFileName = "hgups-导出面单" + DateUtil.format(date, "yyyy-MM-dd-HH-mm-ss") + ".pdf";
            entity = MyFileUtil.downloadFile(file, exportFileName, httpServletRequest);
            boolean code = file.delete();
            log.info(" batchExportWayPDF delete file: " + code);
        } catch (IOException e) {
            log.warn(" batchExportWayPDF error: " + String.valueOf(e));
            e.printStackTrace();
        }
        return entity;
    }

    @ApiOperation(value = "导出运单信息Excel")
    @GetMapping("/exportWayBill")
    public ResponseEntity exportWayBill(@RequestParam List wayBillList) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Map<Object, Object> map = new HashMap<>();
        map.put("wids", wayBillList);
        List<WayBillVo> wayBillDetailsList = wayBillServiceImp.getWayBillDetailsList(map);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//设置日期格式
        List<String> headRow1 = Lists.newArrayList("", "", "", "", "发件人信息", "", "", "", "", "", "", "", "", "", "收件人信息", "", "", "", "", "", "", "", "", "", "包裹信息", "", "", "", "", "", "", "", "", "面单备注", "", "物品信息", "", "", "", "", "", "", "");
        List<String> headRow2 = Lists.newArrayList("序号", "运单号", "运单费用(人民币)", "创建日期", "发件人姓名", "发件人公司", "发件人国家", "发件人州/省", "发件人城市", "发件人主要地址"
                , "发件人门牌号", "发件人邮编", "发件人电话所属国家", "发件人手机号", "收件人姓名", "收件人公司", "收件人国家", "收件人州/省", "收件人城市", "收件人主要地址"
                , "收件人门牌号", "收件人邮编", "收件人话所属国家", "收件人手机号", "包裹重量（磅）", "包裹长度（英寸）", "包裹宽度（英寸）", "包裹高度（英寸）"
                , "包裹英文包裹描述", "包裹形状", "包裹物品类型", "包裹是否为规则长方体", "包裹是否为软包裹", "包裹备注1", "包裹备注2"
                , "物品中文名称", "物品英文名称", "物品单位价格（美元）", "物品单位重量(磅)", "物品数量", "物品产地"
                , "物品HS编码", "申报要素");
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.merge(0, 0, 4, 13, headRow1, true);
        writer.merge(0, 0, 14, 23, headRow1, true);
        writer.merge(0, 0, 24, 32, headRow1, true);
        writer.merge(0, 0, 33, 34, headRow1, true);
        writer.merge(0, 0, 35, 42, headRow1, true);
        writer.writeHeadRow(headRow1);
        writer.writeHeadRow(headRow2);
        for (int i = 0; i < headRow2.size(); i++) {
            writer.autoSizeColumn(i);
            if (i == 0) {
                writer.setColumnWidth(i, 12);
            } else if (i == 1) {
                writer.setColumnWidth(i, 40);
            } else {
                writer.setColumnWidth(i, 30);
            }
        }
        List i = new ArrayList();
        wayBillDetailsList.forEach(x -> {
            i.add("");
            List<Object> dataList = Lists.newArrayList();
            WaybillContact waybillContact = x.getWaybillContact();
            dataList.add(i.size());
            dataList.add(x.getWayBill().getTrackingNumber());
            dataList.add(x.getWayBill().getPrice());
            Date createTime = x.getWayBill().getCreateTime();
            dataList.add(df.format(createTime));
            //发件人信息
            dataList.add(waybillContact.getSenderName());
            dataList.add(waybillContact.getSenderCompany());
            dataList.add(waybillContact.getSenderCountries());
            dataList.add(waybillContact.getSenderProvince());
            dataList.add(waybillContact.getSenderCity());
            dataList.add(waybillContact.getSenderAddressTwo());
            dataList.add(waybillContact.getSenderAddressOne());
            if (null != waybillContact.getSenderPostalCode() && "".equals(waybillContact.getSenderPostalCode())) {
                dataList.add(waybillContact.getSenderPostalCode() + "-" + waybillContact.getSenderPostalCodet());
            } else {
                dataList.add(waybillContact.getSenderPostalCode());
            }
            String senderPhonePrefix = waybillContact.getSenderPhonePrefix();
            if ("1".equals(senderPhonePrefix)) {
                dataList.add("US");
            } else if ("86".equals(senderPhonePrefix)) {
                dataList.add("CA");
            } else {
                dataList.add("US");
            }
            dataList.add(waybillContact.getSenderPhone());


            //收件人信息
            dataList.add(waybillContact.getReceiveName());
            dataList.add(waybillContact.getReceiveCompany());
            dataList.add(waybillContact.getReceiveCountries());
            dataList.add(waybillContact.getReceiveProvince());
            dataList.add(waybillContact.getReceiveCity());
            dataList.add(waybillContact.getReceiveAddressTwo());
            dataList.add(waybillContact.getReceiveAddressOne());
            if (null != waybillContact.getReceivePostalCodet() && !"".equals(waybillContact.getReceivePostalCodet())) {
                dataList.add(waybillContact.getReceivePostalCode() + "-" + waybillContact.getReceivePostalCodet());
            } else {
                dataList.add(waybillContact.getReceivePostalCode());
            }
            String receivePhonePrefix = waybillContact.getReceivePhonePrefix();
            if ("1".equals(receivePhonePrefix)) {
                dataList.add("US");
            } else if ("86".equals(receivePhonePrefix)) {
                dataList.add("CA");
            } else {
                dataList.add("US");
            }
            dataList.add(waybillContact.getReceivePhone());


            //包裹信息
            Parcel parcel = x.getParcel();
            dataList.add(String.valueOf(parcel.getBillWeight()));
            dataList.add(String.valueOf(parcel.getLengths()));
            dataList.add(String.valueOf(parcel.getWidth()));
            dataList.add(String.valueOf(parcel.getHeight()));
            dataList.add(parcel.getAritcleDescribe());
            dataList.add(parcel.getParcelShape());
            dataList.add(parcel.getItmeCategory());
            dataList.add(parcel.getIsCoubid());
            dataList.add(parcel.getIsSoft());
            dataList.add(parcel.getCommentOne());
            dataList.add(parcel.getCommentTwo());


            //物品信息
            List<Article> articleList = x.getArticleList();
            Article article = articleList.get(0);
            dataList.add(article.getCDescribe());
            dataList.add(article.getEDescribe());
            dataList.add(String.valueOf(article.getPrice()));
            dataList.add(String.valueOf(article.getWeight()));
            dataList.add(String.valueOf(article.getNumber()));
            dataList.add(article.getPlace());
            dataList.add(article.getHsEncode());
            dataList.add(article.getDeclaration());

            writer.writeRow(dataList);
        });

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "运单列表.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @ApiOperation(value = "更换面单导出运单信息Excel")
    @GetMapping("/exportSingle")
    public ResponseEntity exportSingle(@RequestParam List<Integer> wayBillList) {
        ShiroUtil.getLoginUserId();
        List<WayBill> wayBillDetailsList = null;
        if (null != wayBillList && wayBillList.size() > 0 && !"".equals(wayBillList.get(0))) {
            EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
            wrapper.orderBy("create_time", false);
            wrapper.in("id", wayBillList);
            wayBillDetailsList = wayBillServiceImp.selectList(wrapper);
        }

        List<String> headRow2 = Lists.newArrayList("序号", "客户名称", "新单号", "旧单号", "重量(LB)", "换单时间");
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.writeHeadRow(headRow2);
        for (int i = 0; i < headRow2.size(); i++) {
            writer.setColumnWidth(i, 30);
        }
        List i = new ArrayList();
        wayBillDetailsList.forEach(x -> {
            i.add("");
            List<Object> dataList = Lists.newArrayList();

            long userId = x.getUserId();
            User user = userService.selectById(userId);

            dataList.add(i.size());
            dataList.add(user.getUsername());
            //因为WJB数据库错误设计的历史原因，现在临时还是保持这字段错误使用的情况，后续更改
            dataList.add(x.getTrackingNumber());
            dataList.add(x.getNewTrackingNumber());
            dataList.add(x.getWareWeight());
            dataList.add(x.getChangeSingleTime());

            writer.writeRow(dataList);
        });

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "运单列表.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @ApiOperation(value = "运单轨迹")
    @PostMapping("/wayBillTrajectory")
    public Response wayBillTrajectory(@RequestBody WaybillTrajectory param) {
        Response response = new Response();
        List<XmlParam> xmlParamList = null;
        List<ChangeTrackingNumberParam> trackingNumbers = param.getTrackingNumbers();
        StringBuilder strUrl = new StringBuilder();
        strUrl.append("https://secure.shippingapis.com/ShippingAPI.dll?API=TrackV2&XML=<TrackRequest USERID=\"707HGUPS0501\">");
        try {
            for (ChangeTrackingNumberParam pam : trackingNumbers) {
                strUrl.append("<TrackID ID=\"" + pam.getTrackingNumber() + "\"></TrackID>");
            }
            strUrl.append("</TrackRequest>");
            String trajectoryXml = testCreateBill(strUrl.toString());
            xmlParamList = XmlUtils.parseXML(trajectoryXml);
        } catch (DocumentException e) {
            e.printStackTrace();
            response.setStatusCode(204);
            response.setMsg("请求超时，请稍后重试");
            return response;
        }
        response.setData(xmlParamList);
        return response;
    }

    @ApiOperation(value = "运单轨迹返回XMl")
    @PostMapping("/wayBillTrajectoryXml")
    public Response wayBillTrajectoryXml(@RequestBody WayBillTrajectoryParam param) {
        Response response = new Response();
        List<String> trackingNumbers = param.getTrackingNumbers();
        StringBuilder strUrl = new StringBuilder();
        strUrl.append("https://secure.shippingapis.com/ShippingAPI.dll?API=TrackV2&XML=<TrackFieldRequest USERID=\"707HGUPS0501\">");

        for (String pam : trackingNumbers) {
            strUrl.append("<TrackID ID=\"" + pam + "\"></TrackID>");
        }

        strUrl.append("</TrackFieldRequest>");
        String trajectoryXml = testCreateBill(strUrl.toString());
        if (null==trajectoryXml){
            response.setStatusCode(203);
            response.setMsg("网络超时,请重试");
            return response;
        }
        response.setData(trajectoryXml);
        return response;
    }


    //根据运单ID修改运单重量并多退少补
    @ApiOperation(value = "根据运单ID修改运单重量")
    @PostMapping("/updateWeightById")
    public Response updateWeightById(@RequestBody UpdateWeightByIdParam param) {
        Response response = new Response();                 //单位（磅）
        Integer integer = wayBillServiceImp.updateWeightById(param.getWeight(), param.getId());

        if (integer == 0) {
            response.setStatusCode(200);
            response.setMsg("修改成功");
        } else if (integer == -1) {
            response.setStatusCode(320);
            response.setMsg("余额不足无法扣费,修改失败");
        } else if (integer==-2){
            response.setStatusCode(321);
            response.setMsg("修改重量不符合当前服务类型,修改失败");
        } else {
            response.setStatusCode(320);
            response.setMsg("运单异常,修改失败");
        }
        return response;
    }

    @ApiOperation("Excel导入批量修改运单重量API")
    @PostMapping(value = "/batchUpdateWeightByTrackingNumber")
    public Response batchUpdateWeightByTrackingNumber(MultipartFile excelFile) {
        if (excelFile == null) {
            return new Response(600,"Excel文件丢失",null);
        }
        StringBuilder errorMsg = new StringBuilder();
        List<InsertBatchWayBillError> errorList = null;
        try {
            List<List<Object>> lines = ExcelUtil.getReader(excelFile.getInputStream()).read();
            List<TrackingNumberWeightParam> insertEntityList = Lists.newArrayList();
            for (int i = 1; i < lines.size(); i++) {
                List<Object> line = lines.get(i);
                int index = 0;
                try {
                    String trackingNumber = String.valueOf(line.get(index++));
                    Double weight = Double.parseDouble(String.valueOf(line.get(index++)));

                    TrackingNumberWeightParam excelData = new TrackingNumberWeightParam();
                    excelData.setWeight(weight);
                    excelData.setTrackingNumber(trackingNumber);

                    insertEntityList.add(excelData);

                } catch (ArrayIndexOutOfBoundsException e) {
                    errorMsg.append(String.format("第%s行只有%s列", i + 1, index)).append("\n");
                } catch (Exception e) {
                    errorMsg.append(String.format("第%s行第%s列解析失败", i + 1, index)).append("\n");
                }
            }
            int x = 0;
            errorList = new ArrayList<>();
            for (TrackingNumberWeightParam tw : insertEntityList) {
                InsertBatchWayBillError error = new InsertBatchWayBillError();
                EntityWrapper wrapper = new EntityWrapper();
                wrapper.eq("tracking_number",tw.getTrackingNumber());
                WayBill wayBill = wayBillServiceImp.selectOne(wrapper);
                if (null!=wayBill){
                    Integer integer = wayBillServiceImp.updateWeightById(tw.getWeight(), wayBill.getId());
                    if (integer == 0) {
                        x++;
                        log.info("成功----->>");
                    } else if (integer == -1) {
                        x++;
                        error.setErrorIndex(x);
                        error.setErrorMessage("余额不足");
                        log.info("批量更新重量----error--->"+error.toString());
                        errorList.add(error);
                    }  else if (integer == -2) {
                        x++;
                        error.setErrorIndex(x);
                        error.setErrorMessage("修改重量不符合当前服务类型,修改失败");
                        log.info("批量更新重量----error--->"+error.toString());
                        errorList.add(error);
                    } else {
                        x++;
                        error.setErrorIndex(x);
                        error.setErrorMessage("运单异常,修改失败");
                        log.info("批量更新重量----error--->"+error.toString());
                        errorList.add(error);
                    }
                }else {
                    x++;
                    error.setErrorIndex(x);
                    error.setErrorMessage("运单未找到,修改失败");
                    log.info("批量更新重量----error--->"+error.toString());
                    errorList.add(error);
                }

            }
        } catch (Exception e) {
            log.error("parse error:", e);
            return new Response(601,"文件解析异常，请检查excel文件格式是否正确!",null);
        }

        if (StringUtils.isNotBlank(errorMsg.toString())) {
            return new Response(602,"文件解析异常，请检查excel文件格式是否正确!具体错误信息:" + errorMsg.toString(),null);
        }
        if (errorList.size()>0){
            return new Response(201,"修改",errorList);
        }
        return new Response(200,"修改",errorList);
    }



    /*@ApiOperation(value = "多线程测试")
    @PostMapping("/multithreadingTest")
    public Response multithreadingTest() {
        try {
            for (int i = 0; i < 100; i++) {
                wayBillServiceImp.sayHello1(i).get();
                wayBillServiceImp.sayHello2(i).get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        *//*for (int i = 0; i < 100; i++) {
            wayBillServiceImp.executeAysncTask1(i);
            wayBillServiceImp.executeAsyncTask2(i);
        }*//*
        return new Response();
    }


    @ApiOperation(value = "多线程测试")
    @PostMapping("/multithreadingTest001")
    public void testReturn() throws InterruptedException, ExecutionException {
        List<Future<String>> lstFuture = new ArrayList<>();// 存放所有的线程，用于获取结果
        for (int i = 0; i < 100; i++) {
            while (true) {
                try {
                    // 线程池超过最大线程数时，会抛出TaskRejectedException，则等待1s，直到不抛出异常为止
                    Future<String> stringFuture = wayBillServiceImp.excuteValueTask(i);
                    lstFuture.add(stringFuture);
                    break;
                } catch (TaskRejectedException e) {
                    System.out.println("线程池满，等待1S。");
                    Thread.sleep(1000);
                }
            }
        }

        // 获取值.get是阻塞式，等待当前线程完成才返回值
        for (Future<String> future : lstFuture) {
            System.out.println(future.get());
        }

        System.out.println("========主线程执行完毕=========");
    }*/


}
