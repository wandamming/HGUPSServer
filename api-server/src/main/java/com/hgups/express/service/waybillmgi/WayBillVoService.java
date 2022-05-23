package com.hgups.express.service.waybillmgi;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.business.dhl.DHLService;
import com.hgups.express.business.dhl.label.LabelResponse;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.exception.MyException;
import com.hgups.express.exception.UspsApiException;
import com.hgups.express.mapper.*;
import com.hgups.express.service.usermgi.*;
import com.hgups.express.service.warehousemgi.DhlCostService;
import com.hgups.express.util.*;
import com.hgups.express.vo.WayBillVo;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * @author fanc
 * 2020/6/9 0009-10:46
 */


@Service
@Slf4j
public class WayBillVoService extends ServiceImpl<WayBillVoMapper, WayBillVo> {

    @Resource
    private ParcelMapper parcelMapper;
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private WayBillMapper wayBillMapper;
    @Resource
    private WayBillService wayBillService;
    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private UserAccountService userAccountService;
    @Resource
    private CostBudgetService costBudgetService;
    @Resource
    private DealDetailService dealDetailService;
    @Resource
    private ConfigService configService;
    @Resource
    private WayBillContactService wayBillContactService;
    @Resource
    private ItemCategoryService itemCategoryService;
    @Resource
    private ZoneService zoneService;
    @Resource
    private PortContactService portContactService;
    @Resource
    private UserCostService userCostService;
    @Resource
    private RightsManagementService rightsManagementService;
    @Resource
    private LateCostService lateCostService;
    @Resource
    private IncidentalService incidentalService;
    @Resource
    private UserService userService;
    @Resource
    private HandleCostService handleCostService;
    @Resource
    private PointScanRecordService pointScanRecordService;
    @Resource
    private DHLService dHLService;
    @Resource
    private DhlCostService dhlCostService;

    //用于同步处理：设置账户余额的排他锁
    private final Object batchFreeLocked = new Object();
    private final Object freeLocked = new Object();

    public synchronized Response createWaybillSync(WayBillVo wayBill) throws MyException {
        return createWaybill(wayBill);
    }

    @Transactional(rollbackFor = Exception.class)
    public Response createWaybill(WayBillVo wayBill) throws MyException {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        if (null == wayBill.getSender()) {
            response.setStatusCode(230);
            return response;
        }
        if (null == wayBill.getReceive()) {
            response.setStatusCode(231);
            return response;
        }
        if (null == wayBill.getParcel()) {
            response.setStatusCode(232);
            return response;
        }
        if (null == wayBill.getArticleList()) {
            response.setStatusCode(233);
            return response;
        }
        if (null == wayBill.getWayBill()) {
            response.setStatusCode(234);
            return response;
        }

        if (StringUtils.isEmpty(wayBill.getSender().getPostalCode())
                || wayBill.getSender().getPostalCode().length() != 5) {
            response.setStatusCode(202);
            return response;
        }

        if (!StringUtils.isEmpty(wayBill.getSender().getPostalCodet())
                && wayBill.getSender().getPostalCodet().length() != 4) {
            response.setStatusCode(203);
            return response;
        }

        if (wayBill.getSender().getName().length() > 48) {
            response.setStatusCode(220);
            return response;
        }


        if (wayBill.getSender().getAddressTwo().length() > 50 || wayBill.getSender().getAddressTwo().length() < 1) {
            response.setStatusCode(205);
            return response;
        }
        if (wayBill.getSender().getProvinceEname().length() != 2) {
            response.setStatusCode(206);
            return response;
        }
        if (wayBill.getSender().getCityEname().length() > 28) {
            response.setStatusCode(207);
            return response;
        }

        String rCode = wayBill.getReceive().getPostalCode();
        if (StringUtils.isEmpty(rCode) || wayBill.getReceive().getPostalCode().length() != 5) {
            response.setStatusCode(208);
            return response;
        }

        if (!StringUtils.isEmpty(wayBill.getReceive().getPostalCodet())
                && wayBill.getReceive().getPostalCodet().length() != 4) {
            response.setStatusCode(203);
            return response;
        }
        if (wayBill.getReceive().getName().length() > 48) {
            response.setStatusCode(221);
            return response;
        }

        if (wayBill.getReceive().getAddressTwo().length() > 50 || wayBill.getReceive().getAddressTwo().length() < 1) {
            response.setStatusCode(211);
            return response;
        }
        if (wayBill.getReceive().getProvinceEname().length() != 2) {
            response.setStatusCode(212);
            return response;
        }
        if (wayBill.getReceive().getCityEname().length() > 28) {
            response.setStatusCode(213);
            return response;
        }

        if (wayBill.getParcel().getBillWeight() > 70 || wayBill.getParcel().getBillWeight() <= 0) {
            response.setStatusCode(214);
            return response;
        }
        //当前版本先不进行长宽高的限制
//        if (wayBill.getParcel().getLengths() > 21 || wayBill.getParcel().getWidth() > 21 || wayBill.getParcel().getHeight() > 21) {
//            response.setStatusCode(215);
//            return response;
//        }

        //检查收件人，通过USPS的接口
        /*USPSApi.Address address = new USPSApi.Address();
        address.state = wayBill.getReceive().getProvinceEname();
        address.city = wayBill.getReceive().getCityEname();
        address.zipCode5 = wayBill.getReceive().getPostalCode();
        address.zipCode4 = wayBill.getReceive().getPostalCodet();
        address.address1 = wayBill.getReceive().getAddressOne();
        address.address2 = wayBill.getReceive().getAddressTwo();
        USPSApi.Address realAddress = USPSApi.validateAddress(address);

        if(!realAddress.isValid) {
            response.setStatusCode(311);
            return response;
        }
        String cr = realAddress.getCarrierRoute();
        String dp = realAddress.getDeliveryPoint();*/
        try {
            String cr = "";
            String dp = "";
            // 必须校验地址
            //wayBill.setCheckAddress(true);

            if (wayBill.getCheckAddress()) {
                USPSApi.Address address = new USPSApi.Address();
                address.state = wayBill.getReceive().getProvinceEname();
                address.city = wayBill.getReceive().getCityEname();
                address.zipCode5 = wayBill.getReceive().getPostalCode();
                address.zipCode4 = wayBill.getReceive().getPostalCodet();
                address.address1 = wayBill.getReceive().getAddressOne();
                address.address2 = wayBill.getReceive().getAddressTwo();
                USPSApi.Address realAddress = USPSApi.validateAddress(address);

                if (!realAddress.isValid) {
                    response.setStatusCode(311);
                    return response;
                }
                cr = realAddress.getCarrierRoute();
                dp = realAddress.getDeliveryPoint();
            }

            wayBill.getWayBill().setCarrierRoute(cr);
            wayBill.getWayBill().setDeliveryPoint(dp);
            log.info(" create way bill cr: " + cr + ", dp: " + dp);
            int processRole = rightsManagementService.isProcessRole(loginUserId);
            String channel = wayBill.getWayBill().getChannel();
            wayBill.getWayBill().setChannel(channel.toLowerCase());
            ZoneDto zoneDto = null;
            if (processRole == 1) {
                if ("DHL".equalsIgnoreCase(channel)) {
                    zoneDto = zoneService.calculateDHLZone(rCode, loginUserId);
                } else {
                    zoneDto = zoneService.calculateZone(rCode, loginUserId);
                }
            } else {
                zoneDto = zoneService.calculateZone(rCode, loginUserId);
            }
            if (zoneDto == null) {
                response.setStatusCode(278);
                return response;
            }
            String zone = zoneDto.getZone();
            String portEntryName = zoneDto.getPortEntryName();
            wayBill.getWayBill().setZone(zone);
            wayBill.getWayBill().setEntrySite(portEntryName);
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("port_id", zoneDto.getPortEntryId());
            PortContact portContact = portContactService.selectOne(wrapper);
            if ("".equals(portEntryName) || "".equals(zone) || "".equals(portContact.getConName()) || "".equals(portContact.getConAddressTwo()) || "".equals(portContact.getConCode())) {
                response.setStatusCode(219);
                return response;
            }
            wayBill.getWayBill().setPortId(portContact.getId());
            System.out.println("入境口岸中间表------------))))))" + portContact);
            wayBill.setPortContact(portContact);


            wayBill.getWayBill().setDhlPackageId(DHLService.MAKE + DHLService.getOrderNo());
            //后程用户打单方式
            LabelUtils.Label label = null;

            //是否是后程用户
            String dhlTrackingNumber = "";
            String labelData = "";
            String url = "";
            if (processRole == 1) {
                if ("HGUPS".equalsIgnoreCase(channel)) {
                    label = LabelUtils.createLabel(wayBill);
                } else if ("DHL".equalsIgnoreCase(channel)) {
                    LabelResponse labelFull = dHLService.createLabelFull(wayBill, zoneDto.getPortEntryId());
                    List<LabelResponse.DataBean.ShipmentsBean> shipments = labelFull.getData().getShipments();
                    for (LabelResponse.DataBean.ShipmentsBean shipment : shipments) {
                        List<LabelResponse.DataBean.ShipmentsBean.PackagesBean> packages = shipment.getPackages();
                        for (LabelResponse.DataBean.ShipmentsBean.PackagesBean aPackage : packages) {
                            //DHL运单号
                            dhlTrackingNumber = aPackage.getResponseDetails().getTrackingNumber();
                            dhlTrackingNumber = dhlTrackingNumber.substring(8, dhlTrackingNumber.length());
                            List<LabelResponse.DataBean.ShipmentsBean.PackagesBean.ResponseDetailsBean.LabelDetailsBean> labelDetails = aPackage.getResponseDetails().getLabelDetails();
                            for (LabelResponse.DataBean.ShipmentsBean.PackagesBean.ResponseDetailsBean.LabelDetailsBean labelDetail : labelDetails) {
                                //DHL面单编码
                                labelData = labelDetail.getLabelData();
                                String imgPath = PathUtils.resDir + System.currentTimeMillis() + "_" + labelDetail.hashCode() + ".png";
                                String pdfPath = PathUtils.resDir + System.currentTimeMillis() + "_" + shipment.hashCode() + ".pdf";
                                boolean b = LabelUtils.base64ToFile(labelData, imgPath, pdfPath);
                                if (b) {
                                    File pdfFile = new File(pdfPath);
                                    if (!pdfFile.exists()) {
                                        pdfFile.getParentFile().mkdirs();
                                        pdfFile.createNewFile();
                                    }
                                    labelData = PDFUtils.getPDFBinary(pdfFile);
                                    pdfFile.delete();
                                    pdfFile.getParentFile().delete();
                                }
                                //DHL面单url地址
                                url = labelDetail.getUrl();
                            }
                        }
                    }
                }
            } else {
                label = LabelUtils.createLabel(wayBill);
            }


            if (label == null || labelData == null) {
                response.setStatusCode(101);
                return response;
            }
            wayBill.getWayBill().setDhlCodingUrl(url);
            wayBill.getReceive().setReceiveCarrierRoute(cr);
            wayBill.getReceive().setReceiveDeliveryPoint(dp);
            if ("DHL".equalsIgnoreCase(channel)) {
                wayBill.getWayBill().setCoding(labelData);//DHL base64编码
            } else if ("HGUPS".equalsIgnoreCase(channel)) {
                wayBill.getWayBill().setCoding(label.base64);//base64编码
            }
            wayBill.getWayBill().setZone(zoneDto.getZone());//区域
            wayBill.getWayBill().setEntrySite(zoneDto.getPortEntryName());
            if ("DHL".equalsIgnoreCase(channel)) {
                wayBill.getWayBill().setTrackingNumber(dhlTrackingNumber);//DHL追踪号码
            } else if ("HGUPS".equalsIgnoreCase(channel)) {
                wayBill.getWayBill().setTrackingNumber(label.trackNo);//追踪号码
            }
            wayBill.getWayBill().setSenderName(wayBill.getSender().getName());
            wayBill.getWayBill().setReceiveName(wayBill.getReceive().getName());
            wayBill.getWayBill().setReceiveId(wayBill.getReceive().getId());
            wayBill.getWayBill().setSenderId(wayBill.getSender().getId());
            wayBill.getWayBill().setCommentOne(wayBill.getParcel().getCommentOne());
            wayBill.getWayBill().setCommentTwo(wayBill.getParcel().getCommentTwo());
            String Fee = setWay(wayBill);
            if ("181".equals(Fee)) {
                response.setStatusCode(181);
                return response;
            } else if ("180".equals(Fee)) {
                response.setStatusCode(180);
                return response;
            } else if ("189".equals(Fee)) {
                response.setStatusCode(189);
                return response;
            }
        } catch (NullPointerException e) {
            log.error(e.toString());
            e.printStackTrace();
            /*response.setStatusCode(188);
            response.setMsg("连接超时，请稍后重试!");
            return response;*/
            response.setStatusCode(188);
            return response;
        } catch (UspsApiException e) {
            log.error("单个打单地址校验异常--->>>" + e.toString());
            e.printStackTrace();
            /*response.setStatusCode(188);
            response.setMsg("连接超时，请稍后重试!");
            return response;*/
            response.setStatusCode(311);
            return response;
        } catch (Exception e) {
            System.out.println("单个打单未知异常--->>" + e.toString());
            e.printStackTrace();
            log.error("单个打单未知异常--->>" + e.toString());
            /*response.setStatusCode(188);
            response.setMsg("连接超时请稍后重试!");
            return response;*/
            response.setStatusCode(188);
            return response;
        }

        /*response.setStatusCode(200);
        response.setMsg("创建成功!");
        return response;*/
        WayBillVo wayBillDetails = wayBillService.getWayBillDetails(wayBill.getWayBill().getId());
//        PointScanRecord pointScanRecord = new PointScanRecord();
//        pointScanRecord.setOrderTrackingNumber(wayBillDetails.getWayBill().getTrackingNumber());
//        pointScanRecord.setPointType(1);//运单类型
//        pointScanRecord.setSysRecord(1);//系统生成状态
//        pointScanRecord.setScanUserName(ShiroUtil.getLoginUser().getUsername());
//        pointScanRecord.setPointScanName("运单已创建");
//        pointScanRecord.setScanTime(wayBillDetails.getWayBill().getCreateTime());
//        pointScanRecordService.insert(pointScanRecord);
        // 创建订单轨迹
        pointScanRecordService.addSysRecord(1, wayBillDetails.getWayBill().getTrackingNumber(), "create", null, wayBillDetails.getWayBill().getCreateTime(), null);
        response.setStatusCode(200);
        response.setData(wayBillDetails);
        return response;
    }


    //获取面单base64编码
    public static String testCreateBill(String strUrl) {
        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        URL url = null;
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URI uri = null;

        try {
            uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            HttpGet httpGet = new HttpGet(uri);
            /*RequestConfig config = RequestConfig.custom().setConnectTimeout(300000) //连接超时时间
                    .setConnectionRequestTimeout(300000) //从连接池中取的连接的最长时间
                    .setSocketTimeout(300 *1000) //数据传输的超时时间
                    .build();
            //设置请求配置时间
            httpGet.setConfig(config);*/
            httpGet.setHeader("Content-Type", "text/xml");
            httpGet.setHeader("charset", "utf-8");
            client = HttpClients.createDefault();
            response = client.execute(httpGet);
            String res = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            log.info("返回数据\n{}", res);

            return res;
        } catch (Exception e) {
            System.out.println("返回数据失败:1: " + e.toString());
            return null;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    System.out.println("返回数据失败:2");
                    e.printStackTrace();
                }
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    System.out.println("返回数据失败:3");
                    e.printStackTrace();
                }
            }
        }
    }


    //单个打单传入重量（lb）获取对应价格
    public double onTokenGetPrice(double weight) {
        List<Config> configs = configService.selectList(null);
        float chainPrice = Float.parseFloat(configs.get(0).getV());//报关单价
        float aviationPrice = Float.parseFloat(configs.get(1).getV());//航空单价
        float exchangeRate = Float.parseFloat(configs.get(2).getV());//汇率
        //Float kgConversion = Float.parseFloat(configs.get(3).getV());//磅转千克
        Float gConversion = Float.parseFloat(configs.get(4).getV());//磅转克
        double gWeight = weight * gConversion;
        CostBudget cost = costBudgetService.getPriceBill(gWeight);
        if (null == cost) {
            CostBudget costBudget = costBudgetService.getMaxWeight();
            double maxKgWeight = costBudget.getWeight() / 1000;
            return maxKgWeight * chainPrice + maxKgWeight * aviationPrice + (costBudget.getAmericaSendPrice() + costBudget.getAmericaPrice()) * exchangeRate;
        }
        double calculateWeight = cost.getWeight();
        double kgWeight = calculateWeight / 1000;
        return kgWeight * chainPrice + kgWeight * aviationPrice + (cost.getAmericaSendPrice() + cost.getAmericaPrice()) * exchangeRate;
    }


    //传入重量、zone获取后程用户运单价格
    public double getLateUserPrice(long loginUserId, double weight, String zone) {
        LateCost lateCost = lateCostService.getUserWaybillPrice(weight);
        if (null == lateCost) {
            lateCost = lateCostService.getMaxPrice();
        }
        double lateUserPrice = getLateUserPrice(lateCost, zone);
        User user = userService.selectById(loginUserId);
        HandleCost handleCost = handleCostService.selectById(user.getHandleId());
        List<Incidental> incidentals = incidentalService.selectList(null);
        List<Config> configs = configService.selectList(null);
        double price = lateUserPrice * Double.parseDouble(configs.get(5).getV());//乘以后程汇率
        if (handleCost != null) {
            price = price + handleCost.getHandlePrice();
        }

        if (null != incidentals && incidentals.size() > 0) {
            Incidental incidental = incidentals.get(0);
            if (user.isCustomsPrice()) { //需要计算海关费用
                price = price + incidental.getCustomsPrice() + incidental.getReservedOne() + incidental.getReservedTwo() + incidental.getReservedThree();
                System.out.println(price);
                return price;
            } else { //不需要计算海关费用
                price = price + incidental.getReservedOne() + incidental.getReservedTwo() + incidental.getReservedThree();
            }
        }

        return price;
    }


    //传入重量、zone获取DHL后程用户运单价格
    public double getDhlLateUserPrice(long loginUserId, double weight, String zone) {
        DhlCost dhlCost = dhlCostService.getUserWaybillPrice(weight);
        if (null == dhlCost) {
            dhlCost = dhlCostService.getMaxPrice();
        }
        double lateUserPrice = getDhlLateUserPrice(dhlCost, zone);
        User user = userService.selectById(loginUserId);

        HandleCost handleCost = handleCostService.selectById(user.getHandleId());
        List<Incidental> incidentals = incidentalService.selectList(null);
        List<Config> configs = configService.selectList(null);
        double price = lateUserPrice * Double.parseDouble(configs.get(7).getV());//乘以DHL汇率
        if (handleCost != null) {
            price = price + handleCost.getHandlePrice();
        }

        if (null != incidentals && incidentals.size() > 0) {
            Incidental incidental = incidentals.get(0);
            if (user.isCustomsPrice()) { //需要计算海关费用
                price = price + incidental.getCustomsPrice() + incidental.getReservedOne() + incidental.getReservedTwo() + incidental.getReservedThree();
                System.out.println(price);
                return price;
            } else { //不需要计算海关费用
                price = price + incidental.getReservedOne() + incidental.getReservedTwo() + incidental.getReservedThree();
            }
        }

        return price;
    }


    //单个打单传入重量（lb）获取对应价格
    public double getOnePrice(double weight, String zone, String channel, Long loginUser) {
        //如果是后称用户直接返回后程用户价格
        int processRole = rightsManagementService.isProcessRole(loginUser);
        if (processRole == 1) {
            if ("HGUPS".equalsIgnoreCase(channel)) {
                return getLateUserPrice(loginUser, weight, zone);
            } else if ("DHL".equalsIgnoreCase(channel)) {
                return getDhlLateUserPrice(loginUser, weight, zone);
            }
        }
        List<Config> configs = configService.selectList(null);
        float chainPrice = Float.parseFloat(configs.get(0).getV());//报关单价
        float aviationPrice = Float.parseFloat(configs.get(1).getV());//航空单价
        float exchangeRate = Float.parseFloat(configs.get(2).getV());//汇率
        //Float kgConversion = Float.parseFloat(configs.get(3).getV());//磅转千克
        Float gConversion = Float.parseFloat(configs.get(4).getV());//磅转克
        double gWeight = weight * gConversion;
        CostBudget cost = costBudgetService.getPriceBill(gWeight);
        if (null == cost) {
            CostBudget costBudget = costBudgetService.getMaxWeight();
            double maxKgWeight = costBudget.getWeight() / 1000;
            return maxKgWeight * chainPrice + maxKgWeight * aviationPrice + (costBudget.getAmericaSendPrice() + costBudget.getAmericaPrice()) * exchangeRate;
        }
        double calculateWeight = cost.getWeight();
        double kgWeight = calculateWeight / 1000;
        return kgWeight * chainPrice + kgWeight * aviationPrice + (cost.getAmericaSendPrice() + cost.getAmericaPrice()) * exchangeRate;
    }

    //批量打单传入重量（lb）获取对应价格
    public double getPrice(double weight, long userId, String zone, String channel) {
        //如果是后称用户直接返回后程用户价格
        int processRole = rightsManagementService.isProcessRole(userId);
        if (processRole == 1) {
            if ("HGUPS".equalsIgnoreCase(channel)) {
                return getLateUserPrice(userId, weight, zone);
            } else if ("DHL".equalsIgnoreCase(channel)) {
                return getDhlLateUserPrice(userId, weight, zone);
            }
        }
        List<Config> configs = configService.selectList(null);
        float chainPrice = Float.parseFloat(configs.get(0).getV());//报关单价
        float aviationPrice = Float.parseFloat(configs.get(1).getV());//航空单价
        float exchangeRate = Float.parseFloat(configs.get(2).getV());//汇率
        //Float kgConversion = Float.parseFloat(configs.get(3).getV());//磅转千克
        Float gConversion = Float.parseFloat(configs.get(4).getV());//磅转克
        double gWeight = weight * gConversion;
        CostBudget cost = costBudgetService.getPriceBill(gWeight);
        if (null == cost) {
            CostBudget costBudget = costBudgetService.getMaxWeight();
            double maxKgWeight = costBudget.getWeight() / 1000;
            return maxKgWeight * chainPrice + maxKgWeight * aviationPrice + (costBudget.getAmericaSendPrice() + costBudget.getAmericaPrice()) * exchangeRate;
        }
        double calculateWeight = cost.getWeight();
        double kgWeight = calculateWeight / 1000;
        return kgWeight * chainPrice + kgWeight * aviationPrice + (cost.getAmericaSendPrice() + cost.getAmericaPrice()) * exchangeRate;
    }


    @Transactional
    public String setWay(WayBillVo vo) {
        Long id = ShiroUtil.getLoginUserId();
        double billWeight = vo.getParcel().getBillWeight();
        WayBill wayBill = vo.getWayBill();
        String Fee = verifyAccount(billWeight, id, wayBill.getZone(), wayBill.getChannel());
        if (Fee.equals("180")) {
            return "180";
        } else if (Fee.equals("181")) {
            return "181";
        }
        double dealAmount = getOnePrice(billWeight, wayBill.getZone(), wayBill.getChannel(), id);

        Sender senderVo = vo.getSender();
        Receive receiveVo = vo.getReceive();
        Parcel parcel = vo.getParcel();
        UserCost userCost = userCostService.getUserWaybillPrice(billWeight);
        if (null == userCost) {
            UserCost userCost1 = userCostService.getMaxPrice();
            double userPrice = getUserPrice(userCost1, wayBill.getZone());
            wayBill.setUserWaybillPrice(userPrice);
        } else {
            double userPrice = getUserPrice(userCost, vo.getWayBill().getZone());
            wayBill.setUserWaybillPrice(userPrice);
        }
        wayBill.setPrice(dealAmount);
        wayBill.setMoreId("---");
        wayBill.setUserId(id);
        wayBill.setBillWeight(billWeight);
        wayBill.setState(1);
        if (parcel.getBillWeight() < 1) {
            parcel.setService("F");
            wayBill.setService("F");
        } else {
            parcel.setService("P");
            wayBill.setService("P");
        }
        int flag3 = wayBillMapper.insert(wayBill);


        parcel.setWaybillId(wayBill.getId());
        parcel.setUserId(id);
        parcel.setIsCoubid(vo.getParcel().getIsCoubid());
        parcel.setIsSoft(vo.getParcel().getIsSoft());
        parcel.setSenderId(senderVo.getId());
        parcel.setReceivetId(receiveVo.getId());
        int flag1 = parcelMapper.insert(parcel);
        Integer pid = parcel.getId();

        boolean flag2 = true;
        List<Article> articleList = vo.getArticleList();
        for (Article article : articleList) {
            article.setParcleId(pid);
            article.setWaybillId(wayBill.getId());
            article.setArticleType(parcel.getItmeCategory());
            String articleType = article.getArticleType();
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("name", articleType);
            ItemCategory itemCategory = itemCategoryService.selectOne(wrapper);
            if (StringUtils.isEmpty(article.getHsEncode())) {
                if (null == itemCategory) {
                    article.setHsEncode("");
                } else {
                    article.setHsEncode(itemCategory.getHs());
                }
            }
            if (StringUtils.isEmpty(article.getHtsEncode())) {
                if (null == itemCategory) {
                    article.setHtsEncode("");
                } else {
                    article.setHtsEncode(itemCategory.getHs());
                }
            }
            Integer insert = articleMapper.insert(article);
            if (insert < 0) {
                flag2 = false;
                break;
            }
        }

        WaybillContact waybillContact = new WaybillContact();
        waybillContact.setSenderName(senderVo.getName());
        waybillContact.setSenderCompany(senderVo.getCompany());
        waybillContact.setSenderCountries(senderVo.getCountries());
        waybillContact.setSenderProvince(senderVo.getProvinceEname());
        waybillContact.setSenderCity(senderVo.getCityEname());
        waybillContact.setSenderAddressOne(senderVo.getAddressOne());
        waybillContact.setSenderAddressTwo(senderVo.getAddressTwo());
        waybillContact.setSenderPostalCode(senderVo.getPostalCode());
        waybillContact.setSenderPostalCodet(senderVo.getPostalCodet());
        waybillContact.setSenderPhone(senderVo.getPhone());
        waybillContact.setSenderPhonePrefix(senderVo.getPhonePrefix());
        waybillContact.setSenderEmail(senderVo.getEmail());
        waybillContact.setSenderCarrierRoute(senderVo.getSenderCarrierRoute());
        waybillContact.setSenderDeliveryPoint(senderVo.getSenderDeliveryPoint());

        waybillContact.setReceiveCarrierRoute(receiveVo.getReceiveCarrierRoute());
        waybillContact.setReceiveDeliveryPoint(receiveVo.getReceiveDeliveryPoint());
        waybillContact.setReceiveName(receiveVo.getName());
        waybillContact.setReceiveCompany(receiveVo.getCompany());
        waybillContact.setReceiveCountries(receiveVo.getCountries());
        waybillContact.setReceiveProvince(receiveVo.getProvinceEname());
        waybillContact.setReceiveCity(receiveVo.getCityEname());
        waybillContact.setReceiveAddressOne(receiveVo.getAddressOne());
        waybillContact.setReceiveAddressTwo(receiveVo.getAddressTwo());
        waybillContact.setReceivePostalCode(receiveVo.getPostalCode());
        waybillContact.setReceivePostalCodet(receiveVo.getPostalCodet());
        waybillContact.setReceivePhone(receiveVo.getPhone());
        waybillContact.setReceivePhonePrefix(receiveVo.getPhonePrefix());
        waybillContact.setReceiveEmail(receiveVo.getEmail());

        waybillContact.setWayBillId(wayBill.getId());

        boolean isWaybillContact = wayBillContactService.insert(waybillContact);


        if (flag1 > 0 && flag2 && flag3 > 0 && isWaybillContact) {
            int Fee1 = accountFee(billWeight, wayBill.getZone(), wayBill.getChannel());
            if (Fee1 == -1) {
                try {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "131";
                }
            }
            DealDetail dealDetail = dealDetailService.selectById(Fee1);
            dealDetail.setWayBillId(wayBill.getId());
            dealDetailService.updateById(dealDetail);
            return "200";
        } else {
            System.out.println("插入失败------》》》》》》");
            return "189";
        }
    }


    //多线程测试
    @Async
    public ListenableFuture<String> batchCreateWaybill(Sender sender, BatchWayBillParam param, String channel, boolean checkAddress, long userId) {
        String res = "";
        if (null == sender) {
            res = "230";
            return new AsyncResult<>(res);
        }
        if (null == param) {
            res = "235";
            return new AsyncResult<>(res);
        }


        if (sender.getPostalCode().length() != 5) {
            /*response.setStatusCode(201);
            response.setMsg("发件人ZIP5邮政编码无效");
            return response;*/
            res = "202";
            return new AsyncResult<>(res);
        }
        if (null == sender.getPostalCodet() || "null".equals(sender.getPostalCodet()) || "".equals(sender.getPostalCodet())) {
            sender.setPostalCodet("");
        } else {
            if (sender.getPostalCodet().length() != 4) {
                /*response.setStatusCode(201);
                response.setMsg("发件人ZIP4邮政编码无效");
                return response;*/
                res = "203";
                return new AsyncResult<>(res);
            }
        }
        if (sender.getName().length() > 48) {
           /* response.setStatusCode(201);
            response.setMsg("发件人电话必须是10位数字");
            return response;*/
            res = "220";
            return new AsyncResult<>(res);
        }


        if (sender.getAddressTwo().length() > 50 || sender.getAddressTwo().length() < 1) {
            /*response.setStatusCode(201);
            response.setMsg("发件人详细地址不能为空且不得超过50个字符");
            return response;*/
            res = "205";
            return new AsyncResult<>(res);
        }

        if (sender.getProvinceEname().length() != 2) {
            /*response.setStatusCode(201);
            response.setMsg("发件人省/州只能缩写且是两个字符");
            return response;*/
            res = "206";
            return new AsyncResult<>(res);
        }
        if (sender.getCityEname().length() > 28) {
            /*response.setStatusCode(201);
            response.setMsg("发件人城市名不得超过28个字符");
            return response;*/
            res = "207";
            return new AsyncResult<>(res);
        }
        String rCode = param.getReceivePostalCode();
        if (rCode.length() != 5) {
            /*response.setStatusCode(201);
            response.setMsg("收件人ZIP5邮政编码无效");
            return response;*/
            res = "208";
            return new AsyncResult<>(res);
        }

        if ("null".equals(param.getReceivePostalCodet()) || null == param.getReceivePostalCodet() || "".equals(param.getReceivePostalCodet())) {
            param.setReceivePostalCodet("");
        } else {
            if (param.getReceivePostalCodet().length() != 4) {
                /*response.setStatusCode(201);
                response.setMsg("收件人ZIP4邮政编码无效");
                return response;*/
                res = "209";
                return new AsyncResult<>(res);
            }
        }


        if (param.getReceiveName().length() > 48) {
            /*response.setStatusCode(201);
            response.setMsg("收件人电话必须是10位数字");
            return response;*/
            res = "221";
            return new AsyncResult<>(res);
        }

        if (param.getReceiveAddressTwo().length() > 50 || param.getReceiveAddressTwo().length() < 1) {
            /*response.setStatusCode(201);
            response.setMsg("收件人详细地址不能为空，且不得超过50个字符");
            return response;*/
            res = "211";
            return new AsyncResult<>(res);
        }
        if (param.getReceiveProvince().length() != 2) {
            /*response.setStatusCode(201);
            response.setMsg("收件人省/州名只能为缩写（两个字符）");
            return response;*/
            res = "212";
            return new AsyncResult<>(res);
        }
        if (param.getReceiveCity().length() > 28) {
            /*response.setStatusCode(201);
            response.setMsg("收件人城市名不得大于28个字符");
            return response;*/
            res = "213";
            return new AsyncResult<>(res);
        }

        if (param.getParcelBillWeight() > 70 || param.getParcelBillWeight() <= 0) {
            /*response.setStatusCode(201);
            response.setMsg("收件人包裹重量不得大于70磅");
            return response;*/
            res = "214";
            return new AsyncResult<>(res);
        }
//        if (param.getParcelLengths() > 21 || param.getParcelWidth() > 21 || param.getParcelHeight() > 21) {
//            /*response.setStatusCode(201);
//            response.setMsg("收件人包裹长宽高不得大于21英尺");
//            return response;*/
//            res = "215";
//            return new AsyncResult<>(res);
//        }

        try {
            WayBillVo wayBill = new WayBillVo();
            ZoneDto zoneDto = null;
            int processRole = rightsManagementService.isProcessRole(userId);

            if (processRole == 1) {
                if ("DHL".equalsIgnoreCase(channel)) {
                    zoneDto = zoneService.calculateDHLZone(rCode, userId);
                } else {
                    zoneDto = zoneService.calculateZone(rCode, userId);
                }
            } else {
                zoneDto = zoneService.calculateZone(rCode, userId);
            }

            if (zoneDto == null) {
                res = "278";
                return new AsyncResult<>(res);
            }
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("port_id", zoneDto.getPortEntryId());
            PortContact portContact = portContactService.selectOne(wrapper);
            if ("".equals(zoneDto.getPortEntryName()) || "".equals(zoneDto.getZone()) || "".equals(portContact.getConName()) || "".equals(portContact.getConAddressTwo()) || "".equals(portContact.getConCode())) {
                res = "219";
                return new AsyncResult<>(res);
            }


            Parcel parcel = new Parcel();
            parcel.setBillWeight(param.getParcelBillWeight());
            parcel.setHeight(param.getParcelHeight());
            parcel.setWidth(param.getParcelWidth());
            parcel.setLengths(param.getParcelLengths());
            parcel.setParcelShape(param.getParcelShape());
            parcel.setCommentOne(param.getParcelCommentOne());
            parcel.setCommentTwo(param.getParcelCommentTwo());
            parcel.setAritcleDescribe(param.getParcelAritcleDescribe());
            parcel.setItmeCategory(param.getParcelItmeCategory());
            parcel.setIsSoft(param.getParcelIsSoft());
            parcel.setIsCoubid(param.getParcelIsCoubid());

            List<Article> articleList = new ArrayList<>();
            Article article = new Article();
            article.setDeclaration(param.getArticleDeclaration());
            article.setEDescribe(param.getArticleEDescribe());
            article.setWeight(param.getArticleWeight());
            article.setPrice(param.getArticlePrice());
            article.setCDescribe(param.getArticleCDescribe());
            article.setNumber(param.getArticleNumber());
            String articleHsEncode = param.getArticleHsEncode();


            article.setHsEncode(articleHsEncode);
            article.setPlace(param.getArticlePlace());
            articleList.add(article);

            Receive receive = new Receive();
            receive.setPhonePrefix(param.getReceivePhonePrefix());
            receive.setPhone(param.getReceivePhone());
            receive.setAddressTwo(param.getReceiveAddressTwo());
            String receiveAddressOne = param.getReceiveAddressOne();
            if ("null".equals(receiveAddressOne) || null == receiveAddressOne) {
                receiveAddressOne = "";
            }
            receive.setAddressOne(receiveAddressOne);
            receive.setPostalCode(param.getReceivePostalCode());
            receive.setPostalCodet(param.getReceivePostalCodet());
            receive.setCityEname(param.getReceiveCity());
            receive.setProvinceEname(param.getReceiveProvince());
            String receiveCompany = param.getReceiveCompany();
            if ("null".equals(receiveCompany) || null == receiveCompany) {
                receiveCompany = "";
            }
            receive.setCompany(receiveCompany);
            receive.setName(param.getReceiveName());
            receive.setCountries(param.getReceiveCountries());
            receive.setReceiveCarrierRoute(param.getReceiveCarrierRoute());
            receive.setReceiveDeliveryPoint(param.getReceiveDeliveryPoint());

            WayBill wayBill1 = new WayBill();
            wayBill1.setUserId(userId);
            wayBill1.setZone(zoneDto.getZone());
            wayBill1.setEntrySite(zoneDto.getPortEntryName());
            wayBill1.setCarrierRoute(param.getReceiveCarrierRoute());
            wayBill1.setDeliveryPoint(param.getReceiveDeliveryPoint());
            wayBill1.setChannel(channel.toLowerCase());
            wayBill1.setDhlPackageId(DHLService.MAKE + DHLService.getOrderNo());
            wayBill.setArticleList(articleList);
            wayBill.setReceive(receive);
            wayBill.setParcel(parcel);
            wayBill.setSender(sender);
            wayBill.setPortContact(portContact);
            wayBill.setWayBill(wayBill1);
            if (param.getParcelBillWeight() < 1) {
                parcel.setService("F");
                wayBill1.setService("F");
                param.setService("F");
            } else {
                parcel.setService("P");
                wayBill1.setService("P");
                param.setService("P");
            }


            //是否是后程用户
            LabelUtils.Label label = null;
            String dhlTrackingNumber = "";
            String labelData = "";
            String url = "";
            if (processRole == 1) {
                if ("HGUPS".equalsIgnoreCase(channel)) {
                    label = LabelUtils.createLabel(wayBill);
                } else if ("DHL".equalsIgnoreCase(channel)) {
                    LabelResponse labelFull = dHLService.createLabelFull(wayBill, zoneDto.getPortEntryId());
                    List<LabelResponse.DataBean.ShipmentsBean> shipments = labelFull.getData().getShipments();
                    for (LabelResponse.DataBean.ShipmentsBean shipment : shipments) {
                        List<LabelResponse.DataBean.ShipmentsBean.PackagesBean> packages = shipment.getPackages();
                        for (LabelResponse.DataBean.ShipmentsBean.PackagesBean aPackage : packages) {
                            //DHL运单号
                            dhlTrackingNumber = aPackage.getResponseDetails().getTrackingNumber();
                            dhlTrackingNumber = dhlTrackingNumber.substring(8, dhlTrackingNumber.length());
                            List<LabelResponse.DataBean.ShipmentsBean.PackagesBean.ResponseDetailsBean.LabelDetailsBean> labelDetails = aPackage.getResponseDetails().getLabelDetails();
                            for (LabelResponse.DataBean.ShipmentsBean.PackagesBean.ResponseDetailsBean.LabelDetailsBean labelDetail : labelDetails) {
                                //DHL面单编码
                                labelData = labelDetail.getLabelData();
                                String imgPath = PathUtils.resDir + System.currentTimeMillis() + "_" + labelDetail.hashCode() + ".png";
                                String pdfPath = PathUtils.resDir + System.currentTimeMillis() + "_" + shipment.hashCode() + ".pdf";
                                boolean b = LabelUtils.base64ToFile(labelData, imgPath, pdfPath);
                                if (b) {
                                    File pdfFile = new File(pdfPath);
                                    if (!pdfFile.exists()) {
                                        pdfFile.getParentFile().mkdirs();
                                        pdfFile.createNewFile();
                                    }
                                    labelData = PDFUtils.getPDFBinary(pdfFile);
                                    pdfFile.delete();
                                    pdfFile.getParentFile().delete();
                                }
                                //DHL面单url地址
                                url = labelDetail.getUrl();
                            }
                        }
                    }
                }
            } else {
                label = LabelUtils.createLabel(wayBill);
            }

            if (StringUtils.isEmpty(label) && StringUtils.isEmpty(labelData)) {
                res = "101";
                return new AsyncResult<>(res);
            }
            String Fee = "";
            if ("HGUPS".equalsIgnoreCase(channel)) {
                Fee = setBatchWay(channel, param, label.base64, label.trackNo, sender, zoneDto, portContact, userId, url);
            } else if ("DHL".equalsIgnoreCase(channel)) {
                Fee = setBatchWay(channel, param, labelData, dhlTrackingNumber, sender, zoneDto, portContact, userId, url);
            }

            if ("181".equals(Fee)) {
                res = "181";
                return new AsyncResult<>(res);
            } else if ("180".equals(Fee)) {
                res = "180";
                return new AsyncResult<>(res);
            } else if ("189".equals(Fee)) {
                res = "189";
                return new AsyncResult<>(res);
            } else if ("131".equals(Fee)) {
                res = "131";
                return new AsyncResult<>(res);
            } else if ("214".equals(Fee)) {
                res = "214";
                return new AsyncResult<>(res);
            }
        } catch (UspsApiException e) {
            log.error("批量打单地址校验失败----->>>" + e.toString());
            e.printStackTrace();
            /*response.setStatusCode(311);
            response.setMsg("连接超时请稍后重试!");
            return response;*/
            res = "311";
            return new AsyncResult<>(res);
        } catch (Exception e) {
            log.error("批量打单未知异常----->>>>>" + e.toString());
            e.printStackTrace();
            /*response.setStatusCode(188);
            response.setMsg("连接超时请稍后重试!");
            return response;*/
            res = "188";
            return new AsyncResult<>(res);
        }

        /*response.setStatusCode(200);
        response.setMsg("创建成功!");
        return response;*/
        res = "200";
        return new AsyncResult<>(res);
    }


    //批量多线程
   /* @Async
    public ListenableFuture<String> batchCreateWaybill(Sender sender, BatchWayBillParam param,Integer checkAddress) {
        String res = "";
        if (null == sender){
            res = "230";
            return new AsyncResult<>(res);
        }
        if (null == param){
            res = "235";
            return new AsyncResult<>(res);
        }


        if (sender.getPostalCode().length() != 5) {
            *//*response.setStatusCode(201);
            response.setMsg("发件人ZIP5邮政编码无效");
            return response;*//*
            res = "202";
            return new AsyncResult<>(res);
        }
        if (null == sender.getPostalCodet() || "null".equals(sender.getPostalCodet()) || "".equals(sender.getPostalCodet())) {
            sender.setPostalCodet("");
        } else {
            if (sender.getPostalCodet().length() != 4) {
                *//*response.setStatusCode(201);
                response.setMsg("发件人ZIP4邮政编码无效");
                return response;*//*
                res = "203";
                return new AsyncResult<>(res);
            }
        }
        if (sender.getName().length() > 48) {
           *//* response.setStatusCode(201);
            response.setMsg("发件人电话必须是10位数字");
            return response;*//*
            res = "220";
            return new AsyncResult<>(res);
        }


        if (sender.getAddressTwo().length() > 50 || sender.getAddressTwo().length() < 1) {
            *//*response.setStatusCode(201);
            response.setMsg("发件人详细地址不能为空且不得超过50个字符");
            return response;*//*
            res = "205";
            return new AsyncResult<>(res);
        }

        if (sender.getProvinceEname().length() != 2) {
            *//*response.setStatusCode(201);
            response.setMsg("发件人省/州只能缩写且是两个字符");
            return response;*//*
            res = "206";
            return new AsyncResult<>(res);
        }
        if (sender.getCityEname().length() > 28) {
            *//*response.setStatusCode(201);
            response.setMsg("发件人城市名不得超过28个字符");
            return response;*//*
            res = "207";
            return new AsyncResult<>(res);
        }
        String rCode = param.getReceivePostalCode();
        if (rCode.length() != 5) {
            *//*response.setStatusCode(201);
            response.setMsg("收件人ZIP5邮政编码无效");
            return response;*//*
            res = "208";
            return new AsyncResult<>(res);
        }

        if ("null".equals(param.getReceivePostalCodet()) || null == param.getReceivePostalCodet() || "".equals(param.getReceivePostalCodet())) {
            param.setReceivePostalCodet("");
        } else {
            if (param.getReceivePostalCodet().length() != 4) {
                *//*response.setStatusCode(201);
                response.setMsg("收件人ZIP4邮政编码无效");
                return response;*//*
                res = "209";
                return new AsyncResult<>(res);
            }
        }


        if (param.getReceiveName().length() > 48) {
            *//*response.setStatusCode(201);
            response.setMsg("收件人电话必须是10位数字");
            return response;*//*
            res = "221";
            return new AsyncResult<>(res);
        }

        if (param.getReceiveAddressTwo().length() > 50 || param.getReceiveAddressTwo().length() < 1) {
            *//*response.setStatusCode(201);
            response.setMsg("收件人详细地址不能为空，且不得超过50个字符");
            return response;*//*
            res = "211";
            return new AsyncResult<>(res);
        }
        if (param.getReceiveProvince().length() != 2) {
            *//*response.setStatusCode(201);
            response.setMsg("收件人省/州名只能为缩写（两个字符）");
            return response;*//*
            res = "212";
            return new AsyncResult<>(res);
        }
        if (param.getReceiveCity().length() > 28) {
            *//*response.setStatusCode(201);
            response.setMsg("收件人城市名不得大于28个字符");
            return response;*//*
            res = "213";
            return new AsyncResult<>(res);
        }

        if (param.getParcelBillWeight() > 70 || param.getParcelBillWeight()<=0) {
            *//*response.setStatusCode(201);
            response.setMsg("收件人包裹重量不得大于70磅");
            return response;*//*
            res = "214";
            return new AsyncResult<>(res);
        }
        if (param.getParcelLengths() > 21 || param.getParcelWidth() > 21 || param.getParcelHeight() > 21) {
            *//*response.setStatusCode(201);
            response.setMsg("收件人包裹长宽高不得大于21英尺");
            return response;*//*
            res = "215";
            return new AsyncResult<>(res);
        }

        try {
            WayBillVo wayBill = new WayBillVo();
            USPSApi.Address realAddress = null;
            String cr = "";
            String dp = "";
            if (null==checkAddress||"".equals(checkAddress)){
                checkAddress=0;
            }
            if (0!=checkAddress){
                USPSApi.Address address = new USPSApi.Address();
                address.state = param.getReceiveProvince();
                address.city = param.getReceiveCity();
                address.zipCode5 = param.getReceivePostalCode();
                address.zipCode4 = param.getReceivePostalCodet();
                address.address1 = param.getReceiveAddressOne();
                address.address2 = param.getReceiveAddressTwo();
                realAddress = USPSApi.validateAddress(address);

                if(!realAddress.isValid) {
                    res = "311";
                    return new AsyncResult<>(res);
                }
                cr = realAddress.getCarrierRoute();
                dp = realAddress.getDeliveryPoint();
            }

            param.setReceiveCarrierRoute(cr);
            param.setReceiveDeliveryPoint(dp);

            ZoneDto zoneDto = zoneService.calculateZone(rCode);
            EntityWrapper wrapper = new EntityWrapper();
            System.out.println("入境口岸ID------》》》》"+zoneDto.getPortEntryId());
            wrapper.eq("port_id",zoneDto.getPortEntryId());
            PortContact portContact = portContactService.selectOne(wrapper);
            System.out.println("入境口岸中间表----》》》"+portContact);
            if ("".equals(zoneDto.getPortEntryName())||"".equals(zoneDto.getZone())||"".equals(portContact.getConName())||"".equals(portContact.getConAddressTwo())||"".equals(portContact.getConCode())){
                res = "219";
                return new AsyncResult<>(res);
            }


            Parcel parcel = new Parcel();
            parcel.setBillWeight(param.getParcelBillWeight());
            parcel.setHeight(param.getParcelHeight());
            parcel.setWidth(param.getParcelWidth());
            parcel.setLengths(param.getParcelLengths());
            parcel.setParcelShape(param.getParcelShape());
            parcel.setCommentOne(param.getParcelCommentOne());
            parcel.setCommentTwo(param.getParcelCommentTwo());
            parcel.setAritcleDescribe(param.getParcelAritcleDescribe());
            parcel.setItmeCategory(param.getParcelItmeCategory());
            parcel.setService(param.getService());
            parcel.setIsSoft(param.getParcelIsSoft());
            parcel.setIsCoubid(param.getParcelIsCoubid());

            List<Article> articleList = new ArrayList<>();
            Article article = new Article();
            article.setDeclaration(param.getArticleDeclaration());
            article.setEDescribe(param.getArticleEDescribe());
            article.setWeight(param.getArticleWeight());
            article.setPrice(param.getArticlePrice());
            article.setCDescribe(param.getArticleCDescribe());
            article.setNumber(param.getArticleNumber());
            String articleHsEncode = param.getArticleHsEncode();


            article.setHsEncode(articleHsEncode);
            article.setPlace(param.getArticlePlace());
            articleList.add(article);

            Receive receive = new Receive();
            receive.setPhonePrefix(param.getReceivePhonePrefix());
            receive.setPhone(param.getReceivePhone());
            receive.setAddressTwo(param.getReceiveAddressTwo());
            String receiveAddressOne = param.getReceiveAddressOne();
            if ("null".equals(receiveAddressOne) || null == receiveAddressOne) {
                receiveAddressOne = "";
            }
            receive.setAddressOne(receiveAddressOne);
            receive.setPostalCode(param.getReceivePostalCode());
            receive.setPostalCodet(param.getReceivePostalCodet());
            receive.setCityEname(param.getReceiveCity());
            receive.setProvinceEname(param.getReceiveProvince());
            String receiveCompany = param.getReceiveCompany();
            if ("null".equals(receiveCompany) || null == receiveCompany) {
                receiveCompany = "";
            }
            receive.setCompany(receiveCompany);
            receive.setName(param.getReceiveName());
            receive.setCountries(param.getReceiveCountries());
            receive.setReceiveCarrierRoute(cr);
            receive.setReceiveDeliveryPoint(dp);

            WayBill wayBill1 = new WayBill();
            wayBill1.setZone(zoneDto.getZone());
            wayBill1.setEntrySite(zoneDto.getPortEntryName());
            wayBill1.setCarrierRoute(cr);
            wayBill1.setDeliveryPoint(dp);
            wayBill.setArticleList(articleList);
            wayBill.setReceive(receive);
            wayBill.setParcel(parcel);
            wayBill.setSender(sender);
            wayBill.setPortContact(portContact);
            wayBill.setWayBill(wayBill1);


            LabelUtils.LabelParameter label = LabelUtils.createLabel(wayBill);
            if (label == null) {
                res = "101";
                return new AsyncResult<>(res);
            }


            String Fee = setBatchWay(param, label, sender,zoneDto,portContact);
            if ("181".equals(Fee)) {
                *//*response.setStatusCode(181);
                response.setMsg("账户余额不足!");
                return response;*//*
                res = "181";
                return new AsyncResult<>(res);
            } else if ("180".equals(Fee)) {
               *//* response.setStatusCode(180);
                response.setMsg("账户未开通!");
                return response;*//*
                res = "180";
                return new AsyncResult<>(res);
            } else if ("189".equals(Fee)) {
                *//*response.setStatusCode(189);
                response.setMsg("创建失败!");
                return response;*//*
                res = "189";
                return new AsyncResult<>(res);
            } else if ("182".equals(Fee)) {
                *//*response.setStatusCode(182);
                response.setMsg("该重量无法计算!");
                return response;*//*
                res = "182";
                return new AsyncResult<>(res);
            } else if ("131".equals(Fee)) {
                *//*response.setStatusCode(131);
                response.setMsg("账户异常!");
                return response;*//*
                res = "131";
                return new AsyncResult<>(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
            *//*response.setStatusCode(188);
            response.setMsg("连接超时请稍后重试!");
            return response;*//*
            res = "188";
            return new AsyncResult<>(res);
        }

        *//*response.setStatusCode(200);
        response.setMsg("创建成功!");
        return response;*//*
        res = "200";
        return new AsyncResult<>(res);
    }*/

    //获取用户zone区价格
    public double getUserPrice(UserCost userCost, String zone) {
        double price = 0;
        switch (zone) {
            case "1":
                price = userCost.getZoneOne();
                break;
            case "2":
                price = userCost.getZoneTwo();
                break;
            case "3":
                price = userCost.getZoneThree();
                break;
            case "4":
                price = userCost.getZoneFour();
                break;
            case "5":
                price = userCost.getZoneFive();
                break;
            case "6":
                price = userCost.getZoneSix();
                break;
            case "7":
                price = userCost.getZoneSeven();
                break;
            case "8":
                price = userCost.getZoneEight();
                break;
            case "9":
                price = userCost.getZoneNine();
        }
        return price;
    }

    //获取后程用户zone区价格
    public double getLateUserPrice(LateCost lateCost, String zone) {
        double price = 0;
        switch (zone) {
            case "1":
                price = lateCost.getZoneOne();
                break;
            case "2":
                price = lateCost.getZoneTwo();
                break;
            case "3":
                price = lateCost.getZoneThree();
                break;
            case "4":
                price = lateCost.getZoneFour();
                break;
            case "5":
                price = lateCost.getZoneFive();
                break;
            case "6":
                price = lateCost.getZoneSix();
                break;
            case "7":
                price = lateCost.getZoneSeven();
                break;
            case "8":
                price = lateCost.getZoneEight();
                break;
            case "9":
                price = lateCost.getZoneNine();
        }
        return price;
    }

    //获取后程用户zone区价格
    public double getDhlLateUserPrice(DhlCost dhlCost, String zone) {
        double price = 0;
        switch (zone) {
            case "1":
                price = dhlCost.getZoneOne();
                break;
            case "2":
                price = dhlCost.getZoneTwo();
                break;
            case "3":
                price = dhlCost.getZoneThree();
                break;
            case "4":
                price = dhlCost.getZoneFour();
                break;
            case "5":
                price = dhlCost.getZoneFive();
                break;
            case "6":
                price = dhlCost.getZoneSix();
                break;
            case "7":
                price = dhlCost.getZoneSeven();
                break;
            case "8":
                price = dhlCost.getZoneEight();
                break;
            case "9":
                price = dhlCost.getZoneNine();
        }
        return price;
    }

    //批量
    @Transactional
    public String setBatchWay(String channel, BatchWayBillParam param, String base64, String trackNo, Sender sender, ZoneDto zoneDto, PortContact portContact, long userId, String url) {
        /*Long id = ShiroUtil.getLoginUserId();*/
        double billWeight = param.getParcelBillWeight();
        String Fee = verifyAccount(billWeight, userId, zoneDto.getZone(), channel);
        if (Fee.equals("180")) {
            return "180";
        } else if (Fee.equals("181")) {
            return "181";
        }
        double dealAmount = getPrice(billWeight, userId, zoneDto.getZone(), channel);

        WayBill wayBill = new WayBill();
        wayBill.setDhlCodingUrl(url);
        wayBill.setCoding(base64);//base64编码
        wayBill.setZone(zoneDto.getZone());//区域：如 08
        //wayBill.setZone(stringMap.get("Zone"));//区域：如 08
        wayBill.setTrackingNumber(trackNo);//追踪号码
        //获取用户运单价格
        UserCost userCost = userCostService.getUserWaybillPrice(billWeight);
        if (null == userCost) {
            UserCost userCost1 = userCostService.getMaxPrice();
            double userPrice = getUserPrice(userCost1, zoneDto.getZone());
            wayBill.setUserWaybillPrice(userPrice);
        } else {
            double userPrice = getUserPrice(userCost, zoneDto.getZone());
            wayBill.setUserWaybillPrice(userPrice);
        }
        wayBill.setSenderName(sender.getName());
        wayBill.setState(1);
        wayBill.setEntrySite(zoneDto.getPortEntryName());
        wayBill.setService(param.getService());
        wayBill.setReceiveName(param.getReceiveName());
        wayBill.setCommentOne(param.getParcelCommentOne());
        wayBill.setCommentTwo(param.getParcelCommentTwo());
        wayBill.setCarrierRoute(param.getReceiveCarrierRoute());
        wayBill.setDeliveryPoint(param.getReceiveDeliveryPoint());
        wayBill.setChannel(channel);

        wayBill.setPrice(dealAmount);
        int moreId = param.getMoreId();//批量id
        wayBill.setMoreId(String.valueOf(moreId));

        wayBill.setUserId(userId);
        wayBill.setPortId(portContact.getId());
        wayBill.setBillWeight(billWeight);
        int flag3 = 0;
        try {
            flag3 = wayBillMapper.insert(wayBill);
            portContactService.updateById(portContact);

//            PointScanRecord pointScanRecord = new PointScanRecord();
//            pointScanRecord.setOrderTrackingNumber(wayBill.getTrackingNumber());
//            pointScanRecord.setPointType(1);//运单类型
//            pointScanRecord.setSysRecord(1);//系统生成状态
//            pointScanRecord.setScanUserName(ShiroUtil.getLoginUser().getUsername());
//            pointScanRecord.setPointScanName("运单已创建");
//            pointScanRecord.setScanTime(wayBill.getCreateTime());
//            pointScanRecordService.insert(pointScanRecord);
            pointScanRecordService.addSysRecord(1, wayBill.getTrackingNumber(), "create", null, wayBill.getCreateTime(), null);
        } catch (Exception e) {
            log.info("插入运单（WayBill）表--->>>" + wayBill);
            System.out.println("插入运单（WayBill）表--->>>" + wayBill);
            e.printStackTrace();
        }


        Parcel parcel = new Parcel();
        parcel.setWaybillId(wayBill.getId());
        parcel.setMoreId(String.valueOf(moreId));
        parcel.setUserId(userId);
        parcel.setIsCoubid(param.getParcelIsCoubid());
        parcel.setIsSoft(param.getParcelIsSoft());
        parcel.setService(param.getService());
        parcel.setItmeCategory(param.getParcelItmeCategory());
        parcel.setAritcleDescribe(param.getParcelAritcleDescribe());
        parcel.setCommentTwo(param.getParcelCommentTwo());
        parcel.setCommentOne(param.getParcelCommentOne());
        parcel.setParcelShape(param.getParcelShape());
        parcel.setLengths(param.getParcelLengths());
        parcel.setWidth(param.getParcelWidth());
        parcel.setHeight(param.getParcelHeight());
        parcel.setBillWeight(param.getParcelBillWeight());
        parcel.setUserId(userId);
        int flag1 = 0;
        try {
            flag1 = parcelMapper.insert(parcel);
        } catch (Exception e) {
            log.info("插入包裹（parcel）表--->>>" + parcel);
            System.out.println("插入包裹（parcel）表--->>>" + parcel);
            e.printStackTrace();
        }

        /*parcel.setSenderId(sender.getId());
        parcel.setReceivetId(receiveVo.getId());*/

        Integer pid = parcel.getId();

        Article article = new Article();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("name", parcel.getItmeCategory());
        ItemCategory itemCategory = itemCategoryService.selectOne(wrapper);
        if (null == itemCategory) {
            article.setHsEncode("");
            article.setHtsEncode("");
        } else {
            article.setHsEncode(itemCategory.getHs());
            article.setHtsEncode(itemCategory.getHts());
        }
        article.setParcleId(pid);
        article.setWaybillId(wayBill.getId());
        article.setArticleType(parcel.getItmeCategory());
        article.setDeclaration(param.getArticleDeclaration());
        String articleHsEncode = param.getArticleHsEncode();
        if ("".equals(articleHsEncode) || "null".equals(articleHsEncode)) {

        } else {
            article.setHsEncode(articleHsEncode);
        }
        article.setPlace(param.getArticlePlace());
        article.setNumber(param.getArticleNumber());
        article.setCDescribe(param.getArticleCDescribe());
        article.setEDescribe(param.getArticleEDescribe());
        article.setWeight(param.getArticleWeight());

        /*List<Article> articleList = vo.getArticleList();
        for (Article article : articleList) {
            article.setParcleId(pid);
            article.setWaybillId(wayBill.getId());
            article.setArticleType(parcel.getItmeCategory());
            Integer insert = articleMapper.insert(article);
            if (insert < 0) {
                flag2 = false;
                break;
            }
        }*/


        WaybillContact waybillContact = new WaybillContact();
        waybillContact.setSenderName(sender.getName());
        waybillContact.setSenderCompany(sender.getCompany());
        waybillContact.setSenderCountries(sender.getCountries());
        waybillContact.setSenderProvince(sender.getProvinceEname());
        waybillContact.setSenderCity(sender.getCityEname());
        waybillContact.setSenderAddressOne(sender.getAddressOne());
        waybillContact.setSenderAddressTwo(sender.getAddressTwo());
        waybillContact.setSenderPostalCode(sender.getPostalCode());
        waybillContact.setSenderPostalCodet(sender.getPostalCodet());
        waybillContact.setSenderPhone(sender.getPhone());
        waybillContact.setSenderPhonePrefix(sender.getPhonePrefix());
        waybillContact.setSenderEmail(sender.getEmail());
        waybillContact.setSenderCarrierRoute(sender.getSenderCarrierRoute());
        waybillContact.setSenderDeliveryPoint(sender.getSenderDeliveryPoint());

        waybillContact.setReceiveCarrierRoute(param.getReceiveCarrierRoute());
        waybillContact.setReceiveDeliveryPoint(param.getReceiveDeliveryPoint());
        waybillContact.setReceiveName(param.getReceiveName());
        waybillContact.setReceiveCompany(param.getReceiveCompany());
        waybillContact.setReceiveCountries(param.getReceiveCountries());
        waybillContact.setReceiveProvince(param.getReceiveProvince());
        waybillContact.setReceiveCity(param.getReceiveCity());
        waybillContact.setReceiveAddressOne(param.getReceiveAddressOne());
        waybillContact.setReceiveAddressTwo(param.getReceiveAddressTwo());
        waybillContact.setReceivePostalCode(param.getReceivePostalCode());
        waybillContact.setReceivePostalCodet(param.getReceivePostalCodet());
        waybillContact.setReceivePhone(param.getReceivePhone());
        waybillContact.setReceivePhonePrefix(param.getReceivePhonePrefix());
        waybillContact.setWayBillId(wayBill.getId());

        Integer flag2 = 0;

        boolean isWaybillContact = false;

        try {
            flag2 = articleMapper.insert(article);
        } catch (Exception e) {
            log.info("插入物品（article）表--->>>" + article);
            System.out.println("插入物品（article）表--->>>" + article);
            e.printStackTrace();
        }
        try {
            isWaybillContact = wayBillContactService.insert(waybillContact);
        } catch (Exception e) {
            log.info("插入运单联系人中间（waybillContact）表--->>>" + waybillContact);
            System.out.println("插入运单联系人中间（waybillContact）表--->>>" + waybillContact);
            //e.printStackTrace();
        }
        if (flag1 > 0 && flag2 > 0 && flag3 > 0 && isWaybillContact) {
            int Fee1 = batchAccountFee(billWeight, userId, zoneDto.getZone(), channel);
            if (Fee1 == -1) {
                try {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                } catch (Exception e) {
                    log.error("账户异常---->>>" + e.toString());
                    e.printStackTrace();
                    return "131";
                }
            }
            DealDetail dealDetail = dealDetailService.selectById(Fee1);
            dealDetail.setWayBillId(wayBill.getId());
            dealDetailService.updateById(dealDetail);
            return "200";
        } else {
            return "189";
        }
            /*try {

                int flag1 = parcelMapper.insert(parcel);
                Integer flag2 = articleMapper.insert(article);
                int flag3 = wayBillMapper.insert(wayBill);
                boolean isWaybillContact = wayBillContactService.insert(waybillContact);
            }catch (Exception e){
               // TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                System.out.println("........");
            }*/


             /*if (flag1 > 0 && flag2>0 && flag3 > 0 && isWaybillContact) {
                System.out.println("插入失败进行回滚-----》》》》》");
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return "189";
            }*/


        /*@Transactional(rollbackFor = Exception.class)
        public String aaa(Parcel parcel,Article article,WayBill wayBill,WaybillContact waybillContact){
            try {
                parcelMapper.insert(parcel);
                articleMapper.insert(article);
                wayBillMapper.insert(wayBill);
                wayBillContactService.insert(waybillContact);
            }catch (Exception e){
                //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
                return "200";
        }*/


    }

    public String verifyAccount(double billWeight, long userId, String zone, String channel) {


        List<Config> configs = configService.selectList(null);
        float chainPrice = Float.parseFloat(configs.get(0).getV());//报关单价
        float aviationPrice = Float.parseFloat(configs.get(1).getV());//航空单价
        float exchangeRate = Float.parseFloat(configs.get(2).getV());//汇率
        //Float kgConversion = Float.parseFloat(configs.get(3).getV());//磅转千克
        Float gConversion = Float.parseFloat(configs.get(4).getV());//磅转克

        double gWeight = billWeight * gConversion;

        EntityWrapper<UserAccount> wrapper1 = new EntityWrapper<>();
        wrapper1.eq("user_id", userId);
        UserAccount userAccount = userAccountService.selectOne(wrapper1);
        if (null == userAccount) {
            return "180";
        }
        int processRole = rightsManagementService.isProcessRole(userId);//是否是后程用户1：是，其他：否
        CostBudget cost = costBudgetService.getPriceBill(gWeight);

        if (null != cost) {
            double kgWeight = cost.getWeight() / 1000;
            double dealAmount = kgWeight * chainPrice + kgWeight * aviationPrice + (cost.getAmericaSendPrice() + cost.getAmericaPrice()) * exchangeRate;
            if (processRole == 1) {
                if ("HGUPS".equalsIgnoreCase(channel)) {
                    dealAmount = getLateUserPrice(userId, billWeight, zone);
                } else if ("DHL".equalsIgnoreCase(channel)) {
                    dealAmount = getDhlLateUserPrice(userId, billWeight, zone);
                }
                log.info("后程用户扣费---dealAmount---------------" + String.valueOf(dealAmount));
            }
            if (dealAmount > userAccount.getBalance()) {
                log.info("-------余额不足-------");
                log.info("运单价格---->>>>>" + dealAmount);
                log.info("账户余额" + userAccount.getBalance());
                log.info("当前用户id" + userId);
                return "181";
            } else {
                log.info("-----余额充足------");
                log.info("运单价格---->>>>>" + dealAmount);
                log.info("当前用户id" + userId);
                return "200";
            }
        } else {
            CostBudget costBudget = costBudgetService.getMaxWeight();
            double maxKgWeight = costBudget.getWeight() / 1000;
            double dealAmount = maxKgWeight * chainPrice + maxKgWeight * aviationPrice + (costBudget.getAmericaSendPrice() + costBudget.getAmericaPrice()) * exchangeRate;
            if (processRole == 1) {
                if ("HGUPS".equalsIgnoreCase(channel)) {
                    dealAmount = getLateUserPrice(userId, billWeight, zone);
                } else if ("DHL".equalsIgnoreCase(channel)) {
                    dealAmount = getDhlLateUserPrice(userId, billWeight, zone);
                }
                log.info("后程用户扣费---dealAmount---------------" + String.valueOf(dealAmount));
            }
            if (dealAmount > userAccount.getBalance()) {
                log.info("-------余额不足-------");
                log.info("运单价格---->>>>>" + dealAmount);
                log.info("账户余额" + userAccount.getBalance());
                log.info("当前用户id" + userId);
                return "181";
            } else {
                log.info("-----余额充足------");
                log.info("运单价格---->>>>>" + dealAmount);
                log.info("当前用户id" + userId);
                return "200";
            }
        }
    }

    //批量打单扣费
    @Transactional
    public int batchAccountFee(double billWeight, long userId, String zone, String channel) {
        List<Config> configs = configService.selectList(null);
        float chainPrice = Float.parseFloat(configs.get(0).getV());//报关单价
        float aviationPrice = Float.parseFloat(configs.get(1).getV());//航空单价
        float exchangeRate = Float.parseFloat(configs.get(2).getV());//汇率
        //Float kgConversion = Float.parseFloat(configs.get(3).getV());//磅转千克
        Float gConversion = Float.parseFloat(configs.get(4).getV());//磅转克
        double gWeight = billWeight * gConversion;

        int processRole = rightsManagementService.isProcessRole(userId);//是否是后程用户1：是，其他：否
        CostBudget cost = costBudgetService.getPriceBill(gWeight);

        double dealAmount = 0;
        if (cost == null) {
            CostBudget costBudget = costBudgetService.getMaxWeight();
            double maxKgWeight = costBudget.getWeight() / 1000;
            dealAmount = maxKgWeight * chainPrice + maxKgWeight * aviationPrice + (costBudget.getAmericaSendPrice() + costBudget.getAmericaPrice()) * exchangeRate;
            if (processRole == 1) {
                if ("HGUPS".equalsIgnoreCase(channel)) {
                    dealAmount = getLateUserPrice(userId, billWeight, zone);
                } else if ("DHL".equalsIgnoreCase(channel)) {
                    dealAmount = getDhlLateUserPrice(userId, billWeight, zone);
                }
                log.info("后程用户扣费---dealAmount---------------" + String.valueOf(dealAmount));
            }
        } else {
            double kgWeight = cost.getWeight() / 1000;
            dealAmount = kgWeight * chainPrice + kgWeight * aviationPrice + (cost.getAmericaSendPrice() + cost.getAmericaPrice()) * exchangeRate;
            if (processRole == 1) {
                if ("HGUPS".equalsIgnoreCase(channel)) {
                    dealAmount = getLateUserPrice(userId, billWeight, zone);
                } else if ("DHL".equalsIgnoreCase(channel)) {
                    dealAmount = getDhlLateUserPrice(userId, billWeight, zone);
                }
                log.info("后程用户扣费---dealAmount---------------" + String.valueOf(dealAmount));
            }
        }
        try {

            synchronized (batchFreeLocked) {
                EntityWrapper<UserAccount> wrapper1 = new EntityWrapper<>();
                wrapper1.eq("user_id", userId);
                UserAccount userAccount = userAccountService.selectOne(wrapper1);
                userAccount.setBalance(userAccount.getBalance() - dealAmount);
                userAccountMapper.updateById(userAccount);

                DealDetail dealDetail = new DealDetail();
                dealDetail.setBalance(userAccount.getBalance());
                dealDetail.setDealAmount(dealAmount);
                dealDetail.setDealType(1);//1:扣费,2：充值,3：退款
                dealDetail.setState(1);//交易状态
                dealDetail.setUserId(userId);
                dealDetailService.insert(dealDetail);
                log.info("交易记录出账-->", dealAmount);
                return dealDetail.getId();
            }

        } catch (Exception e) {
            log.error("账户异常--->>>" + e.toString());
            return -1;
        }


    }


    //单个扣费
    @Transactional
    public int accountFee(double billWeight, String zone, String channel) {
        Long id = ShiroUtil.getLoginUserId();

        List<Config> configs = configService.selectList(null);
        float chainPrice = Float.parseFloat(configs.get(0).getV());//报关单价
        float aviationPrice = Float.parseFloat(configs.get(1).getV());//航空单价
        float exchangeRate = Float.parseFloat(configs.get(2).getV());//汇率
        Float kgConversion = Float.parseFloat(configs.get(3).getV());//磅转千克
        Float gConversion = Float.parseFloat(configs.get(4).getV());//磅转克
        double gWeight = billWeight * gConversion;

        CostBudget cost = costBudgetService.getPriceBill(gWeight);

        double dealAmount = 0;

        int processRole = rightsManagementService.isProcessRole(id);//是否是后程用户1：是，其他：否
        if (cost == null) {
            CostBudget costBudget = costBudgetService.getMaxWeight();
            double maxKgWeight = costBudget.getWeight() / 1000;
            dealAmount = maxKgWeight * chainPrice + maxKgWeight * aviationPrice + (costBudget.getAmericaSendPrice() + costBudget.getAmericaPrice()) * exchangeRate;

            //后程用户费用
            if (processRole == 1) {
                if ("HGUPS".equalsIgnoreCase(channel)) {
                    dealAmount = getLateUserPrice(id, billWeight, zone);
                } else if ("DHL".equalsIgnoreCase(channel)) {
                    dealAmount = getDhlLateUserPrice(id, billWeight, zone);
                }
                log.info("后程用户扣费---dealAmount---------------" + String.valueOf(dealAmount));
            }
        } else {
            double kgWeight = cost.getWeight() / 1000;
            dealAmount = kgWeight * chainPrice + kgWeight * aviationPrice + (cost.getAmericaSendPrice() + cost.getAmericaPrice()) * exchangeRate;

            //后程用户费用
            if (processRole == 1) {
                if ("HGUPS".equalsIgnoreCase(channel)) {
                    dealAmount = getLateUserPrice(id, billWeight, zone);
                } else if ("DHL".equalsIgnoreCase(channel)) {
                    dealAmount = getDhlLateUserPrice(id, billWeight, zone);
                }
                log.info("后程用户扣费---dealAmount---------------" + String.valueOf(dealAmount));
            }
        }
        try {

            EntityWrapper<UserAccount> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("user_id", id);
            UserAccount userAccount = userAccountService.selectOne(wrapper1);
            userAccount.setBalance(userAccount.getBalance() - dealAmount);
            userAccountMapper.updateById(userAccount);

            DealDetail dealDetail = new DealDetail();
            dealDetail.setBalance(userAccount.getBalance());
            dealDetail.setDealAmount(dealAmount);
            dealDetail.setDealType(1);//1:扣费,2：充值,3：退款
            dealDetail.setState(1);//交易状态
            dealDetail.setUserId(id);
            dealDetailService.insert(dealDetail);
            log.info("交易记录出账-->", dealAmount);
            return dealDetail.getId();

        } catch (Exception e) {
            log.error("账户异常--->>>" + e.toString());
            return -1;
        }
    }

    //退货service
    @Transactional
    public Response cancelWayBill(CancelWayBillPrarm param) throws MyException {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        WayBill wayBill = wayBillMapper.selectById(param.getWayBillId());
        if (null == wayBill) {
            response.setStatusCode(300);
            response.setMsg("运单异常,未找到该运单:" + param.getWayBillId());
            return response;
        }
        if (1 != wayBill.getState()) {
            response.setStatusCode(301);
            response.setMsg("取消失败,非已创建状态不可取消:" + param.getWayBillId());
            return response;
        }

        //修改运单状态
        wayBill.setState(2);//运单状态：取消
        double price = wayBill.getPrice();
        Integer isWayBill = wayBillMapper.updateById(wayBill);

        //修改余额（退费）
        EntityWrapper<UserAccount> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id", loginUserId);
        UserAccount userAccount = userAccountService.selectOne(wrapper);
        double balance = userAccount.getBalance();
        userAccount.setBalance(balance + price);
        boolean isUserAccount = userAccountService.updateById(userAccount);

        //添加交易记录
        DealDetail dealDetail = new DealDetail();
        dealDetail.setBalance(userAccount.getBalance());
        dealDetail.setDealAmount(price);
        dealDetail.setDealType(3);//1:扣费,2：充值,3：退款
        dealDetail.setState(1);//交易状态
        dealDetail.setUserId(loginUserId);
        dealDetail.setWayBillId(param.getWayBillId());
        boolean insert = dealDetailService.insert(dealDetail);
        log.info("交易记录退款-->", price);
        if (isWayBill > 0 && isUserAccount && insert) {
            // 创建取消订单记录
            pointScanRecordService.addSysRecord(1, wayBill.getTrackingNumber(), "cancel", null, new Date(), null);
            response.setStatusCode(200);
            response.setMsg("取消成功,运费已退还！");
            return response;
        } else {
            response.setStatusCode(302);
            response.setMsg("取消失败,数据异常：" + param.getWayBillId());
            return response;
        }
    }

    //批量删除
    @Transactional
    public List<Response> batchCancelWayBill(BatchCancelWayBillPrarm param) throws MyException {
        ShiroUtil.getLoginUserId();
        Response response = new Response();
        List<CancelWayBillPrarm> cancelWayBillParams = param.getCancelWayBillParams();

        List<Response> responseList = new ArrayList<>();
        for (CancelWayBillPrarm cancelWayBillParam : cancelWayBillParams) {
            Response response1 = cancelWayBill(cancelWayBillParam);
            responseList.add(response1);
        }
        return responseList;
    }





        /*StringBuilder strUrl = new StringBuilder();
        strUrl.append("https://stg-secure.shippingapis.com/ShippingAPI.dll?API=eVSCancel&XML=<eVSCancelRequest USERID=\"707HGUPS0501\">");
        strUrl.append("<BarcodeNumber>"+param.getTrackingNumber()+"</BarcodeNumber></eVSCancelRequest>");

        String cancelXml = testCreateBill(strUrl.toString());
        System.out.println("运单xml----->>"+cancelXml);
        Map<String, String> stringMap;
        try {
            stringMap = XmlUtils.xmlToMap(cancelXml);
            String status = stringMap.get("Status");//取消订单返回的状态
            String reason = stringMap.get("Reason");//返回状态的原因
            String error = stringMap.get("Description");//返回状态的原因

            if(("Cancelled".equals(status)&&"OrderInfo Cancelled Successfully".equals(reason))||("Invalid BarcodeNumber length.".equals(error))){
                //修改运单状态
                wayBill.setState(2);//运单状态：取消
                double price = wayBill.getPrice();
                Integer isWayBill = wayBillMapper.updateById(wayBill);

                //修改余额（退费）
                EntityWrapper<UserAccount> wrapper = new EntityWrapper<>();
                wrapper.eq("user_id",loginUserId);
                UserAccount userAccount = userAccountService.selectOne(wrapper);
                double balance = userAccount.getBalance();
                userAccount.setBalance(balance+price);
                boolean isUserAccount = userAccountService.updateById(userAccount);

                //添加交易记录
                DealDetail dealDetail = new DealDetail();
                dealDetail.setBalance(userAccount.getBalance());
                dealDetail.setDealAmount(price);
                dealDetail.setDealType(3);//1:扣费,2：充值,3：退款
                dealDetail.setState(1);//交易状态
                dealDetail.setUserId(loginUserId);
                dealDetail.setWayBillId(param.getWayBillId());
                boolean insert = dealDetailService.insert(dealDetail);
                logger.info("交易记录退款-->", price);
                if(isWayBill>0&&isUserAccount&&insert){
                    response.setStatusCode(200);
                    response.setMsg("取消成功,运费已退还！");
                    return response;
                }else {
                    response.setMsg("取消失败！");
                    response.setStatusCode(302);
                    return response;
                }
            }else if("Not Cancelled".equals(status)&&"OrderInfo Already Cancelled".equals(reason)){
                response.setMsg("该运单已被取消，请勿重复操作！");
                response.setStatusCode(300);
                return response;
            }else if("Not Cancelled".equals(status)&&"OrderInfo Not Found".equals(reason)){
                response.setMsg("取消失败，未找到该订单！");
                response.setStatusCode(301);
                return response;
            }else if("Not Cancelled".equals(status)&&"OrderInfo Already Manifested".equals(reason)){
                response.setMsg("取消失败，运单已被签收！");
                response.setStatusCode(303);
                return response;
            } else {
                response.setStatusCode(183);
                response.setMsg("其它错误");
                return response;
            }

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("---退款异常---->>>>"+e.toString());
            log.info("取消运单异常---->>>"+e);
            response.setStatusCode(182);
            response.setMsg("连接超时请稍后重试!");
            return response;
        }*/


    //运单恢复已创建状态
    @Transactional
    public Response reopenWayBill(CancelWayBillPrarm param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        WayBill wayBill = wayBillMapper.selectById(param.getWayBillId());

        if (null == wayBill) {
            response.setStatusCode(300);
            response.setMsg("运单异常");
            return response;
        }
        if (2 != wayBill.getState()) {
            response.setStatusCode(301);
            response.setMsg("取消失败,非取消状态不可恢复");
            return response;
        }

        wayBill.setState(1);//运单状态： 已创建
        double price = wayBill.getPrice();
        Integer isWayBill = wayBillMapper.updateById(wayBill);

        //修改余额（恢复运单：扣费）
        EntityWrapper<UserAccount> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id", loginUserId);
        UserAccount userAccount = userAccountService.selectOne(wrapper);
        double balance = userAccount.getBalance();
        userAccount.setBalance(balance + price);
        boolean isUserAccount = userAccountService.updateById(userAccount);

        //添加交易记录
        DealDetail dealDetail = new DealDetail();
        dealDetail.setBalance(userAccount.getBalance());
        dealDetail.setDealAmount(price);
        dealDetail.setDealType(1);//1:扣费,2：充值,3：退款
        dealDetail.setState(1);//交易状态
        dealDetail.setUserId(loginUserId);
        dealDetail.setWayBillId(param.getWayBillId());
        boolean insert = dealDetailService.insert(dealDetail);
        log.info("交易记录扣款-->", price);
        if (isWayBill > 0 && isUserAccount && insert) {
            response.setStatusCode(200);
            response.setMsg("恢复成功");
            return response;
        } else {
            response.setMsg("恢复失败！");
            response.setStatusCode(302);
            return response;
        }
    }

    //根据状态码返回对应Response信息
    public Response statusCodeMsg(String code) {
        Response response = new Response();
        if ("200".equals(code)) {
            response.setStatusCode(200);
            response.setMsg("运单创建成功！");
            return response;
        } else if ("311".equals(code)) {
            response.setStatusCode(311);
            response.setMsg("收件人地址信息有误，请重新输入");
            return response;
        } else if ("201".equals(code)) {
            response.setStatusCode(201);
            response.setMsg("必填参数不能为空");
            return response;
        } else if ("181".equals(code)) {
            response.setStatusCode(181);
            response.setMsg("账户余额不足!");
            return response;
        } else if ("202".equals(code)) {
            response.setStatusCode(202);
            response.setMsg("发件人ZIP5邮政编码无效");
            return response;
        } else if ("203".equals(code)) {
            response.setStatusCode(203);
            response.setMsg("发件人ZIP4邮政编码无效");
            return response;
        } else if ("204".equals(code)) {
            response.setStatusCode(204);
            response.setMsg("发件人电话必须是10位数字");
            return response;
        } else if ("205".equals(code)) {
            response.setStatusCode(205);
            response.setMsg("发件人详细地址不能为空且不得超过50个字符");
            return response;
        } else if ("206".equals(code)) {
            response.setStatusCode(206);
            response.setMsg("发件人省/州只能缩写且是两个字符");
            return response;
        } else if ("207".equals(code)) {
            response.setStatusCode(207);
            response.setMsg("发件人城市名不得超过28个字符");
            return response;
        } else if ("208".equals(code)) {
            response.setStatusCode(208);
            response.setMsg("收件人ZIP5邮政编码无效");
            return response;
        } else if ("209".equals(code)) {
            response.setStatusCode(209);
            response.setMsg("收件人ZIP4邮政编码无效");
            return response;
        } else if ("210".equals(code)) {
            response.setStatusCode(210);
            response.setMsg("收件人电话必须是10位数字");
            return response;
        } else if ("211".equals(code)) {
            response.setStatusCode(211);
            response.setMsg("收件人详细地址不能为空，且不得超过50个字符");
            return response;
        } else if ("212".equals(code)) {
            response.setStatusCode(212);
            response.setMsg("收件人省/州名只能为缩写（两个字符）");
            return response;
        } else if ("213".equals(code)) {
            response.setStatusCode(213);
            response.setMsg("收件人城市名不得大于28个字符");
            return response;
        } else if ("214".equals(code)) {
            response.setStatusCode(214);
            response.setMsg("包裹重量不得大于70磅且不得小于等于0");
            return response;
        } else if ("215".equals(code)) {
            response.setStatusCode(215);
            response.setMsg("包裹长宽高不得大于21英尺");
            return response;
        } else if ("216".equals(code)) {
            response.setStatusCode(216);
            response.setMsg("ZIP5邮政编码无效");
            return response;
        } else if ("217".equals(code)) {
            response.setStatusCode(217);
            response.setMsg("ZIP4邮政编码无效");
            return response;
        } else if ("219".equals(code)) {
            response.setStatusCode(219);
            response.setMsg("入境口岸联系人为空");
            return response;
        } else if ("180".equals(code)) {
            response.setStatusCode(180);
            response.setMsg("账户未开通!");
            return response;
        } else if ("189".equals(code)) {
            response.setStatusCode(189);
            response.setMsg("运单创建失败!");
            return response;
        } else if ("101".equals(code)) {
            response.setStatusCode(101);
            response.setMsg("创建面单失败：面单为空!");
            return response;
        } else if ("131".equals(code)) {
            response.setStatusCode(131);
            response.setMsg("账户异常!");
            return response;
        } else if ("171".equals(code)) {
            response.setStatusCode(171);
            response.setMsg("事务出错!");
            return response;
        } else if ("220".equals(code)) {
            response.setStatusCode(220);
            response.setMsg("发件人姓名不得超过48个字符");
            return response;
        } else if ("230".equals(code)) {
            response.setStatusCode(230);
            response.setMsg("发件人信息为空");
            return response;
        } else if ("231".equals(code)) {
            response.setStatusCode(231);
            response.setMsg("收件人信息为空");
            return response;
        } else if ("232".equals(code)) {
            response.setStatusCode(232);
            response.setMsg("包裹信息为空!");
            return response;
        } else if ("233".equals(code)) {
            response.setStatusCode(233);
            response.setMsg("物品信息为空!");
            return response;
        } else if ("234".equals(code)) {
            response.setStatusCode(234);
            response.setMsg("运单信息为空");
            return response;
        } else if ("221".equals(code)) {
            response.setStatusCode(221);
            response.setMsg("收件人姓名不得超过48个字符!");
            return response;
        } else if ("235".equals(code)) {
            response.setStatusCode(235);
            response.setMsg("打单信息为空");
            return response;
        } else if ("278".equals(code)) {
            response.setStatusCode(278);
            response.setMsg("创建失败,无入境口岸可用");
            return response;
        } else if ("188".equals(code)) {
            response.setStatusCode(188);
            response.setMsg("创建失败");
            return response;
        } else {
            response.setStatusCode(185);
            response.setMsg(code);
            return response;
        }
    }

    //定时更新运单追踪状态
    @Async
    public String timingCreateBase(String strUrl) {
        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        URL url = null;
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URI uri = null;

        try {
            uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            HttpGet httpGet = new HttpGet(uri);
            /*RequestConfig config = RequestConfig.custom().setConnectTimeout(300000) //连接超时时间
                    .setConnectionRequestTimeout(300000) //从连接池中取的连接的最长时间
                    .setSocketTimeout(300 *1000) //数据传输的超时时间
                    .build();
            //设置请求配置时间
            httpGet.setConfig(config);*/
            httpGet.setHeader("Content-Type", "text/xml");
            httpGet.setHeader("charset", "utf-8");
            client = HttpClients.createDefault();
            response = client.execute(httpGet);
            String res = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            log.info("返回数据\n{}", res);
            parseXMLThread(res);
            return "error";
        } catch (Exception e) {
            System.out.println("返回数据失败:1: " + e.toString());
            return strUrl;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    System.out.println("返回数据失败:2");
                    e.printStackTrace();
                }
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    System.out.println("返回数据失败:3");
                    e.printStackTrace();
                }
            }
        }
    }

    //运单轨迹xml转对象
    public void parseXMLThread(String xml) throws DocumentException {
        try {
            System.out.println("----------xml-------" + xml);
            Document document = null;
            document = DocumentHelper.parseText(xml);
            Element root = document.getRootElement();
            Iterator elementIterator = root.elementIterator();
            //遍历这个迭代器，，循环出根节点里第一层子节点
            while (elementIterator.hasNext()) {
                //获取根节点的第一个子节点
                Element next = (Element) elementIterator.next();
                XmlParam xmlParam = new XmlParam();
                Attribute id = next.attribute("ID");
                String tracking = id.getValue();
                WayBill wayBill = wayBillService.getWayBill(tracking);
                //获取这个子节点的属性的值
                //System.out.println(next.attributeValue("id"));
                //重复上面的动作，第一个子节点的下子节点的迭代器
                Iterator elementIterator2 = next.elementIterator();
                while (elementIterator2.hasNext()) {
                    //获取来的子节点
                    Element elem = (Element) elementIterator2.next();

                    //打印出元素节点和文本节点
                    if ("TrackSummary".equals(elem.getName())) {
                        if (elem.getText().contains("delivered")) {
                            xmlParam.setWayBillState("已送达");
                            if (wayBill != null) {
                                wayBill.setState(4);
                                wayBill.setWayBillTrace(elem.getText());
                                wayBillService.updateById(wayBill);
                                // 签收
                                pointScanRecordService.addSysRecord(wayBill.getId(), wayBill.getTrackingNumber(), "signFor", null, new Date(), "");
                            }
                        }
                        if (elem.getText().contains("prepared") || elem.getText().contains("picked") || elem.getText().contains("partner")) {
                            if (wayBill != null) {
                                wayBill.setWayBillTrace(elem.getText());
                                wayBill.setState(3);
                                wayBillService.updateById(wayBill);
                            }
                        }
                    }
                    if ("TrackDetail".equals(elem.getName())) {
                        if (!"已送达".equals(xmlParam.getWayBillState())) {
                            if (wayBill != null) {
                                wayBill.setState(3);
                                wayBill.setWayBillTrace(elem.getText());
                                wayBillService.updateById(wayBill);
                                // 派送中
                                pointScanRecordService.addSysRecord(wayBill.getId(), wayBill.getTrackingNumber(), "delivery", null, new Date(), "");
                                break;
                            }
                        }

                    }
                }
            }
        } catch (NullPointerException | MyException e) {
            e.printStackTrace();
        }
    }


}
