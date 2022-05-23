package com.hgups.express.controller.waybillmg;

import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.hgups.express.domain.DealDetail;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.UserAccount;
import com.hgups.express.domain.WayBill;
import com.hgups.express.domain.param.InsertBatchWayBillError;
import com.hgups.express.domain.param.TrackingNumberWeightParam;
import com.hgups.express.service.usermgi.DealDetailService;
import com.hgups.express.service.waybillmgi.UserAccountService;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.service.waybillmgi.WayBillVoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fanc
 * 2020/11/16-15:44
 */
@Api(description = "退费接口")
@Slf4j
@RestController
@RequestMapping("/refund")
public class WayBillRefundController {

    @Resource
    private WayBillService wayBillService;
    @Resource
    private WayBillVoService wayBillVoService;
    @Resource
    private UserAccountService userAccountService;
    @Resource
    private DealDetailService dealDetailService;


    @ApiOperation("Excel导入运单号退费API")
    @PostMapping(value = "/batchRefund")
    public Response batchRefund(MultipartFile excelFile) {
        if (excelFile == null) {
            return new Response(600, "Excel文件丢失", null);
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
                    TrackingNumberWeightParam excelData = new TrackingNumberWeightParam();
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
                wrapper.eq("tracking_number", tw.getTrackingNumber());
                WayBill wayBill = wayBillService.selectOne(wrapper);
                if (null != wayBill) {
                    boolean b = wayBillService.updateRefund(wayBill);
                    if (b) {
                        x++;
                        log.info("成功----->>");
                    } else {
                        x++;
                        error.setErrorIndex(x);
                        error.setErrorMessage("退费失败");
                        log.info("批量退费----error--->" + error.toString());
                        errorList.add(error);
                    }
                } else {
                    x++;
                    error.setErrorIndex(x);
                    error.setErrorMessage("运单未找到,修改失败");
                    log.info("批量更新重量----error--->" + error.toString());
                    errorList.add(error);
                }

            }
        } catch (Exception e) {
            log.error("parse error:", e);
            return new Response(601, "文件解析异常，请检查excel文件格式是否正确!", null);
        }

        if (StringUtils.isNotBlank(errorMsg.toString())) {
            return new Response(602, "文件解析异常，请检查excel文件格式是否正确!具体错误信息:" + errorMsg.toString(), null);
        }
        if (errorList.size() > 0) {
            return new Response(201, "修改", errorList);
        }
        return new Response(200, "修改", errorList);
    }


    @ApiOperation("Excel导入运单号修改状态为已取消API")
    @PostMapping(value = "/batchCancel")
    public Response batchCancel(MultipartFile excelFile) {
        if (excelFile == null) {
            return new Response(600, "Excel文件丢失", null);
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
                    TrackingNumberWeightParam excelData = new TrackingNumberWeightParam();
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
                wrapper.eq("tracking_number", tw.getTrackingNumber());
                WayBill wayBill = wayBillService.selectOne(wrapper);
                if (null != wayBill) {
                    wayBill.setState(2);
                    boolean b = wayBillService.updateById(wayBill);
                    if (b) {
                        x++;
                        log.info("成功----->>");
                    } else {
                        x++;
                        error.setErrorIndex(x);
                        error.setErrorMessage("退费失败");
                        log.info("批量退费----error--->" + error.toString());
                        errorList.add(error);
                    }
                } else {
                    x++;
                    error.setErrorIndex(x);
                    error.setErrorMessage("运单未找到,修改失败");
                    log.info("批量更新重量----error--->" + error.toString());
                    errorList.add(error);
                }

            }
        } catch (Exception e) {
            log.error("parse error:", e);
            return new Response(601, "文件解析异常，请检查excel文件格式是否正确!", null);
        }

        if (StringUtils.isNotBlank(errorMsg.toString())) {
            return new Response(602, "文件解析异常，请检查excel文件格式是否正确!具体错误信息:" + errorMsg.toString(), null);
        }
        if (errorList.size() > 0) {
            return new Response(201, "修改", errorList);
        }
        return new Response(200, "修改", errorList);
    }


    @ApiOperation("Excel导入运单号多退少补API")
    @PostMapping(value = "/batchDifference")
    public Response batchDifference(MultipartFile excelFile) {
        if (excelFile == null) {
            return new Response(600, "Excel文件丢失", null);
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
                    TrackingNumberWeightParam excelData = new TrackingNumberWeightParam();
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
                wrapper.eq("tracking_number", tw.getTrackingNumber());
                WayBill wayBill = wayBillService.selectOne(wrapper);
                if (null == wayBill) {
                    x++;
                    error.setErrorIndex(x);
                    error.setErrorMessage("运单未找到,修改失败");
                    log.info("批量更新重量----error--->" + error.toString());
                    errorList.add(error);
                    continue;
                }
                if (wayBill.getWareWeight() == 0) {
                    x++;
                    error.setErrorIndex(x);
                    error.setErrorMessage("荷重为0,修改失败:" + wayBill.getTrackingNumber());
                    log.info("批量更新重量----error--->" + error.toString());
                    errorList.add(error);
                    continue;
                }
                if (wayBill.getWarePrice() == 0) {
                    x++;
                    error.setErrorIndex(x);
                    error.setErrorMessage("荷重价格为0,修改失败:" + wayBill.getTrackingNumber());
                    log.info("批量更新重量----error--->" + error.toString());
                    errorList.add(error);
                    continue;
                }
                //核重重量
                double wareWeight = wayBill.getWareWeight();
                String zone = wayBill.getZone();
                String channel = wayBill.getChannel();
                if (StringUtils.isEmpty(channel)){
                    channel = "HGUPS";
                }
                long userId = wayBill.getUserId();
                //之前核重价格（算错为全程核重）
                double warePrice = wayBill.getWarePrice();
                //当前核重价格
                double onePrice = wayBillVoService.getOnePrice(wareWeight, zone, channel, userId);
                DecimalFormat format1 = new DecimalFormat("#.00");
                double wPrice = Double.parseDouble(format1.format(onePrice));
                wayBill.setWarePrice(wPrice);
                wayBillService.updateById(wayBill);
                if (warePrice==wPrice){
                    continue;
                }
                if (warePrice>onePrice){
                    EntityWrapper wrapper1 = new EntityWrapper();
                    wrapper1.eq("user_id",wayBill.getUserId());
                    UserAccount userAccount = userAccountService.selectOne(wrapper1);
                    log.info("当前用户账户userAccount--->>"+userAccount);
                    //退还差价
                    double refund = warePrice - onePrice;
                    userAccount.setBalance(userAccount.getBalance() + refund);
                    userAccountService.updateById(userAccount);
                    DealDetail dealDetail = new DealDetail();
                    dealDetail.setBalance(userAccount.getBalance());
                    dealDetail.setDealAmount(refund);
                    dealDetail.setDealType(3);//1:扣费,2：充值,3：退款 4:补扣
                    dealDetail.setState(1);//交易状态
                    dealDetail.setUserId(wayBill.getUserId());
                    dealDetail.setWayBillId(wayBill.getId());
                    dealDetailService.insert(dealDetail);
                    log.info("交易记录多退-->", refund);
                }else {
                    EntityWrapper wrapper1 = new EntityWrapper();
                    wrapper1.eq("user_id",wayBill.getUserId());
                    UserAccount userAccount = userAccountService.selectOne(wrapper1);
                    log.info("当前用户账户userAccount--->>"+userAccount);
                    //退还差价
                    double refund = onePrice - warePrice;
                    userAccount.setBalance(userAccount.getBalance() - refund);
                    userAccountService.updateById(userAccount);
                    DealDetail dealDetail = new DealDetail();
                    dealDetail.setBalance(userAccount.getBalance());
                    dealDetail.setDealAmount(refund);
                    dealDetail.setDealType(4);//1:扣费,2：充值,3：退款 4:补扣
                    dealDetail.setState(1);//交易状态
                    dealDetail.setUserId(wayBill.getUserId());
                    dealDetail.setWayBillId(wayBill.getId());
                    dealDetailService.insert(dealDetail);
                    log.info("交易记录多退-->", refund);
                }
            }

        } catch (Exception e) {
            log.error("parse error:", e);
            return new Response(601, "文件解析异常，请检查excel文件格式是否正确!", null);
        }

        if (StringUtils.isNotBlank(errorMsg.toString())) {
            return new Response(602, "文件解析异常，请检查excel文件格式是否正确!具体错误信息:" + errorMsg.toString(), null);
        }
        if (errorList.size() > 0) {
            return new Response(201, "修改", errorList);
        }
        return new Response(200, "修改", errorList);
    }


}
