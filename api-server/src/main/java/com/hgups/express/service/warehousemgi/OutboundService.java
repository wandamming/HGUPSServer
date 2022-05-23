package com.hgups.express.service.warehousemgi;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.business.dhl.DHLService;
import com.hgups.express.business.dhl.label.LabelResponse;
import com.hgups.express.constant.Constant;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.controller.warehousermg.WebSocketServer;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.exception.MyException;
import com.hgups.express.mapper.OutboundDetailsMapper;
import com.hgups.express.mapper.OutboundMapper;
import com.hgups.express.mapper.UserSacksMapper;
import com.hgups.express.service.usermgi.ConfigService;
import com.hgups.express.service.usermgi.DealDetailService;
import com.hgups.express.service.waybillmgi.*;
import com.hgups.express.util.*;
import com.hgups.express.vo.NoticeVo;
import com.hgups.express.vo.WayBillVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.hgups.express.service.waybillmgi.WayBillVoService.testCreateBill;

/**
 * @author fanc
 * 2020/9/19 0019-15:56
 */
@Service
@Slf4j
public class OutboundService extends ServiceImpl<OutboundMapper, Outbound> {

    @Resource
    private OutboundMapper outboundMapper;
    @Resource
    private ZoneService zoneService;
    @Resource
    private ReceiveService receiveService;
    @Resource
    private SenderService senderService;
    @Resource
    private ProductInfoService productInfoService;
    @Resource
    private UserSacksMapper userSacksMapper;
    @Resource
    private PortContactService portContactService;
    @Resource
    private OutboundProductService outboundProductService;
    @Resource
    private WarehouseContactService warehouseContactService;
    @Resource
    private WarehouseCostService warehouseCostService;
    @Resource
    private WarehouseOtherCostService warehouseOtherCostService;
    @Resource
    private WarehouseMaterialCostService warehouseMaterialCostService;
    @Resource
    private UserAccountService userAccountService;
    @Resource
    private DealDetailService dealDetailService;
    @Resource
    private ProductStorageService productStorageService;
    @Resource
    private OutboundDetailsMapper outboundDetailsMapper;
    @Resource
    private ConfigService configService;
    @Resource
    private DHLService dhlService;
    @Resource
    private WayBillService wayBillService;
    @Resource
    private ParcelService parcelService;
    @Resource
    private ArticleService articleService;
    @Resource
    private WayBillContactService wayBillContactService;
    @Resource
    private WarehouseRentCostService warehouseRentCostService;
    @Resource
    private WarehouseHandleCostService warehouseHandleCostService;
    @Resource
    private PointScanRecordService pointScanRecordService;

    public static final Integer freeDay = 30;
    public static final int DHL_LB_MIN = 100;

    //修改已创建、待出库单
    @Transactional
    public OutboundError updateOutbound(AddOutBoundParam param) {
        /*Long loginUserId = ShiroUtil.getLoginUserId();//当前用户ID
        WayBillVo wayBillVo = new WayBillVo();
        Outbound outbound = outboundMapper.selectById(param.getId());
        OutboundError outboundError = new OutboundError();
        //删除中间表数据
        EntityWrapper<OutboundProduct> wrapper1 = new EntityWrapper<>();
        wrapper1.eq("outbound_id", param.getId());
        List<OutboundProduct> outboundProducts1 = outboundProductService.selectList(wrapper1);
        for (OutboundProduct outboundProduct : outboundProducts1) {
            Long productId = outboundProduct.getProductId();
            ProductInfo productInfo = productInfoService.selectById(productId);
            Integer inventoryNumber = productInfo.getInventoryNumber();
            inventoryNumber = inventoryNumber + outboundProduct.getProductNumber();
            productInfo.setInventoryNumber(inventoryNumber);
            productInfoService.updateById(productInfo);
            outboundProductService.deleteById(outboundProduct.getId());
        }
        String exportCity = param.getExportCity();//出口城市
        Integer logisticsMode = param.getLogisticsMode();//物流方式
        List<InventoryProducerParam> producerList = param.getProducerList();//出库产品种类及对应数量
        Integer receiveId = param.getReceiveId();//发件人ID
        Integer senderId = param.getSenderId();//收件人ID
        Receive receive = receiveService.selectById(receiveId);
        Sender sender = senderService.selectById(senderId);
        WaybillContact wc = wayBillContactService.selectById(outbound.getContactId());
        wc.setReceiveAddressOne(receive.getAddressOne());
        wc.setReceiveAddressTwo(receive.getAddressTwo());
        wc.setReceiveCity(receive.getCityCname());
        wc.setReceiveCompany(receive.getCompany());
        wc.setReceiveCountries(receive.getCountries());
        wc.setReceiveEmail(receive.getEmail());
        wc.setReceiveName(receive.getName());
        wc.setReceivePhone(receive.getPhone());
        wc.setReceivePhonePrefix(receive.getPhonePrefix());
        wc.setReceivePostalCode(receive.getPostalCode());
        wc.setReceivePostalCodet(receive.getPostalCodet());
        wc.setReceiveProvince(receive.getProvinceCname());
        wc.setSenderAddressOne(sender.getAddressOne());
        wc.setSenderAddressTwo(sender.getAddressTwo());
        wc.setSenderCity(sender.getCityCname());
        wc.setSenderCompany(sender.getCompany());
        wc.setSenderCountries(sender.getCountries());
        wc.setSenderEmail(sender.getEmail());
        wc.setSenderName(sender.getName());
        wc.setSenderPhone(sender.getPhone());
        wc.setSenderPhonePrefix(sender.getPhonePrefix());
        wc.setSenderPostalCode(sender.getPostalCode());
        wc.setSenderPostalCodet(sender.getPostalCodet());
        wc.setSenderProvince(sender.getProvinceCname());
        wc.setSenderAddressTwo(sender.getAddressTwo());
        wc.setSenderId(senderId);
        wc.setReceiveId(receiveId);
        wayBillContactService.updateById(wc);
        //计算zone及入境口岸
        ZoneDto zoneDto = zoneService.calculateZone(receive.getPostalCode(), loginUserId);
        if (zoneDto == null) {
            outboundError.setCode(300);
            //outboundError.setProduceId(productInfo.getId());
            outboundError.setErrorMsg("入境口岸异常");
            return outboundError;
        }
        String zone = zoneDto.getZone();//zone（1-9）
        String portEntryName = zoneDto.getPortEntryName();//入境口岸
        int productNumberSum = 0;//出库总数
        int productSum = 0;//sku总数
        double productWeightSum = 0;//总重量
        List<OutboundProduct> outboundProducts = new ArrayList<>();
        for (InventoryProducerParam producerParam : producerList) {
            Integer producerNumber = producerParam.getProducerNumber();
            productNumberSum += producerParam.getProducerNumber();
            productSum++;
            Long producerId = producerParam.getProducerId();
            ProductInfo productInfo = productInfoService.selectById(producerId);
            if (null == productInfo) {
                continue;
            }
            productWeightSum += productInfo.getUnitWeight();
            //获取当前产品的库存 - 当前产品此次入库的数量
            Integer inventoryNumber = productInfo.getInventoryNumber();
            if (inventoryNumber<producerNumber){
                outboundError.setCode(301);
                //outboundError.setProduceId(productInfo.getId());
                outboundError.setErrorMsg("产品库存不足");
                return outboundError;
            }
            inventoryNumber = inventoryNumber - producerNumber;
            productInfo.setInventoryNumber(inventoryNumber);
            productInfoService.updateById(productInfo);//保存

            //产品、入库单中间表数据保存
            OutboundProduct outboundProduct = new OutboundProduct();
            outboundProduct.setOutboundId(outbound.getId());//出库库单ID
            outboundProduct.setProductId(producerId);//产品ID
            //此次入库的产品数量
            outboundProduct.setProductNumber(producerParam.getProducerNumber());
            if (producerParam.getProducerNumber() > 0) {
                outboundProducts.add(outboundProduct);
            }
        }


        //封装数据生成面单
        outbound.setExportCity(exportCity);
        outbound.setLogisticsMode(logisticsMode);
        outbound.setZone(zone);
        outbound.setPortEntry(portEntryName);
        outbound.setState(WareHouseState.ALREADY_CREATE_OUTBOUND);
        outbound.setReceiveAddress(receive.getAddressTwo());
        outbound.setReceiveName(receive.getName());
        outbound.setSendAddress(sender.getAddressTwo());
        outbound.setSendName(sender.getName());
        outbound.setSkuOutboundNumber(productSum);
        outbound.setOutboundNumber(productNumberSum);
        outbound.setOutboundWeight(productWeightSum);
        outbound.setOutboundUserId(loginUserId.intValue());
        WayBill wayBill = new WayBill();
        wayBill.setCarrierRoute(receive.getReceiveCarrierRoute());
        wayBill.setDeliveryPoint(receive.getReceiveDeliveryPoint());
        wayBill.setZone(zone);
        wayBill.setEntrySite(portEntryName);
        wayBill.setWareWeight(productWeightSum);

        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("port_id", zoneDto.getPortEntryId());
        PortContact portContact = portContactService.selectOne(wrapper);
        wayBill.setPortId(portContact.getId());
        Parcel parcel = new Parcel();
        parcel.setAritcleDescribe("");
        parcel.setCommentOne("");

        wayBillVo.setParcel(parcel);
        wayBillVo.setPortContact(portContact);
        wayBillVo.setReceive(receive);
        wayBillVo.setSender(sender);
        wayBillVo.setWayBill(wayBill);
        LabelUtils.Label label = LabelUtils.createLabel(wayBillVo);
        if (label == null) {
            outboundError.setCode(302);
            //outboundError.setProduceId(productInfo.getId());
            outboundError.setErrorMsg("创建面单异常");
            return outboundError;
        }
        outbound.setWarehouseWaybillNumber(label.trackNo);
        outbound.setWaybillCode(label.base64);
        outboundMapper.updateById(outbound);
        if (outboundProducts.size() > 0) {
            //批量保存中间表数据
            outboundProductService.insertBatch(outboundProducts);
            outboundError.setCode(200);
            //outboundError.setProduceId(productInfo.getId());
            outboundError.setErrorMsg("修改成功");
            return outboundError;
        }
        outboundError.setCode(199);
        //outboundError.setProduceId(productInfo.getId());
        outboundError.setErrorMsg("修改失败");
        return outboundError;*/
        Long loginUserId = ShiroUtil.getLoginUserId();//当前用户ID
        Long id = param.getId();
        //删除中间表数据
        EntityWrapper<OutboundProduct> wrapper = new EntityWrapper<>();
        wrapper.eq("outbound_id", id);
        List<OutboundProduct> outboundProducts1 = outboundProductService.selectList(wrapper);
        for (OutboundProduct outboundProduct : outboundProducts1) {
            Long productId = outboundProduct.getProductId();//产品ID
            Integer productNumber = outboundProduct.getProductNumber();//产品数量

            ProductInfo productInfo = productInfoService.selectById(productId);
            Integer inventoryNumber = productInfo.getInventoryNumber();//库存数量
            inventoryNumber = inventoryNumber + productNumber;
            productInfo.setInventoryNumber(inventoryNumber);
            productInfoService.updateById(productInfo);
            outboundProductService.deleteById(outboundProduct.getId());
        }
        Outbound outbound = outboundMapper.selectById(id);
        EntityWrapper<WayBill> wrapper1 = new EntityWrapper<>();
        wrapper1.eq("tracking_number", outbound.getWarehouseWaybillNumber());
        wayBillService.delete(wrapper1);
        //创建出库单
        OutboundError outboundError = new OutboundError();

        //代发
        WayBillVo wayBillVo = new WayBillVo();
        String exportCity = param.getExportCity();//出口城市
        Integer logisticsMode = param.getLogisticsMode();//物流方式
        List<InventoryProducerParam> producerList = param.getProducerList();//出库产品种类及对应数量
        Integer receiveId = param.getReceiveId();//发件人ID
        Integer senderId = param.getSenderId();//收件人ID
        Receive receive = receiveService.selectById(receiveId);
        Sender sender = senderService.selectById(senderId);
        //计算zone及入境口岸
        ZoneDto zoneDto = zoneService.calculateDHLZone(receive.getPostalCode(), loginUserId);
        if (zoneDto == null) {
            outboundError.setCode(300);
            outboundError.setErrorMsg("入境口岸异常");
            return outboundError;
        }
        String zone = zoneDto.getZone();//zone（1-9）
        String portEntryName = zoneDto.getPortEntryName();//入境口岸
        int productNumberSum = 0;//出库总数
        int productSum = 0;//sku总数
        double productWeightSum = 0;//总重量
        List<OutboundProduct> outboundProducts = new ArrayList<>();
        for (InventoryProducerParam producerParam : producerList) {
            int sum = 0;
            ProductInfo productInfo = productInfoService.selectById(producerParam.getProducerId());
            log.info(" production info: " + productInfo);
            //递归遍历在本次的出库sku中，是否有相同
            for (InventoryProducerParam producerParam1 : producerList) {
                productNumberSum += producerParam1.getProducerNumber();
                if (producerParam1.getProducerId().equals(producerParam.getProducerId())) {
                    sum += producerParam1.getProducerNumber();
                    if (sum > productInfo.getInventoryNumber()) {
                        outboundError.setCode(301);
                        //outboundError.setProduceId(productInfo.getId());
                        outboundError.setErrorMsg("产品: " + productInfo.getSkuCode() + " 库存不足");
                        return outboundError;
                    }
                }
            }

        }

        //经过上述库存判断后，进行下述真实的库存扣除
        productNumberSum = 0;//出库总数
        for (InventoryProducerParam producerParam : producerList) {
            Integer producerNumber = producerParam.getProducerNumber();
            productNumberSum += producerParam.getProducerNumber();
            productSum++;
            Long producerId = producerParam.getProducerId();
            ProductInfo productInfo = productInfoService.selectById(producerId);
            if (null == productInfo) {
                continue;
            }
            productWeightSum = productWeightSum + productInfo.getUnitWeight() * producerParam.getProducerNumber();

            //获取当前产品的库存 + 当前产品此次出库的数量
            Integer inventoryNumber = productInfo.getInventoryNumber();
            inventoryNumber = inventoryNumber - producerNumber;
            productInfo.setInventoryNumber(inventoryNumber);
            productInfoService.updateById(productInfo);//保存

            //产品、入库单中间表数据保存
            OutboundProduct outboundProduct = new OutboundProduct();
            outboundProduct.setOutboundId(outbound.getId());//出库库单ID
            outboundProduct.setProductId(producerId);//产品ID
            //此次入库的产品数量
            outboundProduct.setProductNumber(producerParam.getProducerNumber());
            if (producerParam.getProducerNumber() > 0) {
                outboundProducts.add(outboundProduct);
            }
        }


        //封装数据生成面单
        outbound.setCreateTime(new Date());
        outbound.setExportCity(exportCity);
        outbound.setLogisticsMode(logisticsMode);
        String outboundOrder = userSacksMapper.getSacksNumber(14);
        outbound.setOutboundOrder(outboundOrder);
        outbound.setZone(zone);
        outbound.setPortEntry(portEntryName);
        outbound.setReceiveAddress(receive.getAddressTwo());
        outbound.setReceiveName(receive.getName());
        outbound.setSendAddress(sender.getAddressTwo());
        outbound.setSendName(sender.getName());
        outbound.setSkuOutboundNumber(productSum);
        outbound.setOutboundNumber(productNumberSum);
        outbound.setOutboundWeight(productWeightSum);
        outbound.setOutboundUserId(loginUserId.intValue());

        WayBill wayBill = new WayBill();
        wayBill.setState(1);//一创建
        wayBill.setCarrierRoute(receive.getReceiveCarrierRoute());
        wayBill.setDeliveryPoint(receive.getReceiveDeliveryPoint());
        wayBill.setZone(zone);
        wayBill.setEntrySite(portEntryName);
        wayBill.setBillWeight(productWeightSum);
        wayBill.setDhlPackageId(DHLService.MAKE + DHLService.getOrderNo());
        wayBill.setUserId(loginUserId);
        wayBill.setReceiveName(receive.getName());
        wayBill.setSenderName(sender.getName());
        wayBill.setPortId(zoneDto.getPortEntryId());
        EntityWrapper wrapper4 = new EntityWrapper();
        wrapper4.eq("port_id", zoneDto.getPortEntryId());
        PortContact portContact = portContactService.selectOne(wrapper4);
        wayBill.setPortId(portContact.getId());

        //保存运单信息
        wayBillService.insert(wayBill);

        //保存联系人中间表数据
        WaybillContact wc = new WaybillContact();
        wc.setReceiveId(receiveId);
        wc.setSenderId(senderId);
        wc.setReceiveAddressOne(receive.getAddressOne());
        wc.setReceiveAddressTwo(receive.getAddressTwo());
        wc.setReceiveCity(receive.getCityCname());
        wc.setReceiveCompany(receive.getCompany());
        wc.setReceiveCountries(receive.getCountries());
        wc.setReceiveEmail(receive.getEmail());
        wc.setReceiveName(receive.getName());
        wc.setReceivePhone(receive.getPhone());
        wc.setReceivePhonePrefix(receive.getPhonePrefix());
        wc.setReceivePostalCode(receive.getPostalCode());
        wc.setReceivePostalCodet(receive.getPostalCodet());
        wc.setReceiveProvince(receive.getProvinceCname());
        wc.setSenderAddressOne(sender.getAddressOne());
        wc.setSenderAddressTwo(sender.getAddressTwo());
        wc.setSenderCity(sender.getCityCname());
        wc.setSenderCompany(sender.getCompany());
        wc.setSenderCountries(sender.getCountries());
        wc.setSenderEmail(sender.getEmail());
        wc.setSenderName(sender.getName());
        wc.setSenderPhone(sender.getPhone());
        wc.setSenderPhonePrefix(sender.getPhonePrefix());
        wc.setSenderPostalCode(sender.getPostalCode());
        wc.setSenderPostalCodet(sender.getPostalCodet());
        wc.setSenderProvince(sender.getProvinceCname());
        wc.setSenderAddressTwo(sender.getAddressTwo());
        wc.setWayBillId(wayBill.getId());
        wayBillContactService.insert(wc);

        outbound.setContactId(wc.getId());

        Parcel parcel = new Parcel();
        parcel.setAritcleDescribe("");
        parcel.setCommentOne("");
        parcel.setBillWeight(productWeightSum);
        parcel.setWaybillId(wayBill.getId());
        parcel.setUserId(loginUserId);
        //保存包裹喜信息
        parcelService.insert(parcel);

        Article article = new Article();
        article.setWaybillId(wayBill.getId());
        article.setParcleId(parcel.getId());
        articleService.insert(article);
        wayBillVo.setParcel(parcel);
        wayBillVo.setPortContact(portContact);
        wayBillVo.setReceive(receive);
        wayBillVo.setSender(sender);
        wayBillVo.setWayBill(wayBill);

        String dhlTrackingNumber = "";
        String labelData = "";
        String url = "";
        if (outbound.getOutboundWeight() >= DHL_LB_MIN) {
            LabelResponse labelFull = dhlService.createLabelFull(wayBillVo, zoneDto.getPortEntryId());
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
                                try {
                                    pdfFile.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            labelData = PDFUtils.getPDFBinary(pdfFile);
                            pdfFile.delete();
                            pdfFile.getParentFile().delete();
                        }
                        //DHL面单url地址
                        url = labelDetail.getUrl();
                        wayBill.setCoding(labelData);
                        wayBill.setTrackingNumber(dhlTrackingNumber);
                        wayBill.setDhlCodingUrl(url);
                        wayBill.setChannel("dhl");
                        wayBill.setBillWeight(productWeightSum);
                        wayBill.setService("P");
                        wayBillService.updateById(wayBill);
                    }
                }
            }
            outbound.setWarehouseWaybillNumber(dhlTrackingNumber);
            outbound.setWaybillCode(labelData);
            outbound.setDhlUrl(url);
            outbound.setChannel("dhl");
            outboundMapper.updateById(outbound);
            if (outboundProducts.size() > 0) {
                //批量保存中间表数据
                outboundProductService.insertBatch(outboundProducts);
                outboundError.setCode(200);
                outboundError.setErrorMsg("创建成功");
                return outboundError;
            }
            //插入数据失败
            outboundError.setCode(201);
            outboundError.setErrorMsg("地址信息错误");
            return outboundError;
        }
        StringBuilder strUrl = new StringBuilder();
        strUrl.append("https://stg-secure.shippingapis.com/ShippingAPI.dll?API=eVS&XML=<eVSRequest USERID=\"872FMUSS6909\">"); //不变
        strUrl.append("<Option></Option>");
        strUrl.append("<Revision></Revision>");
        strUrl.append("<ImageParameters>");
        strUrl.append("<ImageParameter>4x6LABELP</ImageParameter>"); //标签大小
        strUrl.append("<XCoordinate>0</XCoordinate>");   //X坐标
        strUrl.append("<YCoordinate>900</YCoordinate>"); //Y坐标
        strUrl.append("</ImageParameters>");
        strUrl.append("<FromName>" + sender.getName() + "</FromName>"); //发件人姓名
        strUrl.append("<FromFirm>" + (StringUtils.isEmpty(sender.getCompany()) ? "" : sender.getCompany()) + "</FromFirm>");   //发件人公司
        strUrl.append("<FromAddress1>" + (StringUtils.isEmpty(sender.getAddressOne()) ? "" : sender.getAddressOne()) + "</FromAddress1>");  //发件人地址1
        strUrl.append("<FromAddress2>" + sender.getAddressTwo() + "</FromAddress2>"); //发件人地址2
        strUrl.append("<FromCity>" + sender.getCityEname() + "</FromCity>"); //发件人城市
        strUrl.append("<FromState>" + sender.getProvinceEname() + "</FromState>"); //发件人国家
        strUrl.append("<FromZip5>" + sender.getPostalCode() + "</FromZip5>"); //邮政编码
        strUrl.append("<FromZip4>" + sender.getPostalCodet() + "</FromZip4>");  //不变
        strUrl.append("<FromPhone>" + sender.getPhone() + "</FromPhone>"); //发件人电话
        strUrl.append("<AllowNonCleansedOriginAddr>True</AllowNonCleansedOriginAddr>"); //不变
        strUrl.append("<ToName>" + receive.getName() + "</ToName>"); //收件人姓名
        strUrl.append("<ToFirm>" + (StringUtils.isEmpty(receive.getCompany()) ? "" : receive.getCompany()) + "</ToFirm>"); //收件人公司
        strUrl.append("<ToAddress1>" + (StringUtils.isEmpty(receive.getAddressOne()) ? "" : receive.getAddressOne()) + "</ToAddress1>"); //收件人地址1
        strUrl.append("<ToAddress2>" + receive.getAddressTwo() + "</ToAddress2>"); //收件人地址1
        strUrl.append("<ToCity>" + receive.getCityEname() + "</ToCity>"); //收件人城市
        strUrl.append("<ToState>" + receive.getProvinceEname() + "</ToState>"); //收件人国家
        strUrl.append("<ToZip5>" + receive.getPostalCode() + "</ToZip5>"); //收件人邮政编码
        strUrl.append("<ToZip4>" + receive.getPostalCodet() + "</ToZip4>"); //不变
        strUrl.append("<ToPhone>" + receive.getPhone() + "</ToPhone>"); //收件人电话
        strUrl.append("<AllowNonCleansedDestAddr>True</AllowNonCleansedDestAddr>"); //不变
        strUrl.append("<WeightInOunces>" + outbound.getOutboundWeight() + "</WeightInOunces>"); //包裹重量
        if (outbound.getOutboundWeight() < 1) {
            wayBill.setService("F");
            strUrl.append("<ServiceType>FIRST CLASS</ServiceType>"); //服务类型
        } else {
            wayBill.setService("P");
            strUrl.append("<ServiceType>PRIORITY</ServiceType>"); //服务类型
        }
        /*if ("RECTANGULAR".equals(wayBill.getParcel().getParcelShape())) {
            strUrl.append("<Container>RECTANGULAR</Container>"); //物品类型
            strUrl.append("<Width>" + wayBill.getParcel().getWidth() + "</Width>"); //宽
            strUrl.append("<Length>" + wayBill.getParcel().getLengths() + "</Length>"); //长
            strUrl.append("<Height>" + wayBill.getParcel().getHeight() + "</Height>"); //高
        } else {
            //strUrl.append("<Container>PACKAGE SERVICE</Container>"); //物品类型
        }*/

        //strUrl.append("<Machinable>" + wayBill.getParcel().getIsCoubid() + "</Machinable>"); //是否是长方体（true,false）
        strUrl.append("<CustomerRefNo>FC</CustomerRefNo>"); //用户内部使用编号，可变
        strUrl.append("<ExtraServices><ExtraService>155</ExtraService></ExtraServices>"); //可选
        strUrl.append("<ReceiptOption>None</ReceiptOption>"); //不变
        strUrl.append("<ImageType>PDF</ImageType>"); //不变
        strUrl.append("<PrintCustomerRefNo>False</PrintCustomerRefNo>"); //是否打印客户编号
        strUrl.append("</eVSRequest>");
        System.out.println("URL----------------------->" + strUrl);
        Map<String, String> stringMap;
        String coding = "";
        try {
            String ss = testCreateBill(strUrl.toString());
            stringMap = XmlUtils.xmlToMap(ss);

            coding = stringMap.get("LabelImage");//base64编码
            if (coding == null) {
                //数据库回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                //插入数据失败
                outboundError.setCode(201);
                outboundError.setErrorMsg("地址信息错误");
                return outboundError;
            }
            //String zone1 = stringMap.get("Zone");//区域：如 08
            String substring = stringMap.get("BarcodeNumber").substring(8, stringMap.get("BarcodeNumber").length());//追踪号码

            wayBill.setCoding(coding);
            wayBill.setTrackingNumber(substring);
            wayBill.setChannel("fmuss");
            wayBill.setBillWeight(productWeightSum);
            wayBillService.updateById(wayBill);
            outbound.setWarehouseWaybillNumber(substring);
            outbound.setWaybillCode(coding);
            outbound.setChannel("fmuss");
            outboundMapper.updateById(outbound);
            if (outboundProducts.size() > 0) {
                //批量保存中间表数据
                outboundProductService.insertBatch(outboundProducts);
                //成功
                outboundError.setCode(200);
                pointScanRecordService.addSysRecord(5, outbound.getWarehouseWaybillNumber(), null, "updateOutbound", new Date(), "");
                return outboundError;
            }
            //插入数据失败
            outboundError.setCode(201);
            outboundError.setErrorMsg("地址信息错误");
            return outboundError;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //插入数据失败
        outboundError.setCode(201);
        outboundError.setErrorMsg("地址信息错误");
        return outboundError;
    }


    //取消出库
    @Transactional
    public void deleteOutbound(LongIdParam param) throws MyException {
        List<Long> ids = param.getIds();
        for (Long id : ids) {
            EntityWrapper<OutboundProduct> wrapper = new EntityWrapper<>();
            wrapper.eq("outbound_id", id);
            List<OutboundProduct> outboundProducts = outboundProductService.selectList(wrapper);
            for (OutboundProduct outboundProduct : outboundProducts) {
                Long productId = outboundProduct.getProductId();//产品ID
                Integer productNumber = outboundProduct.getProductNumber();//产品数量

                ProductInfo productInfo = productInfoService.selectById(productId);
                Integer inventoryNumber = productInfo.getInventoryNumber();//库存数量
                inventoryNumber = inventoryNumber + productNumber;
                productInfo.setInventoryNumber(inventoryNumber);
                productInfoService.updateById(productInfo);
                outboundProductService.deleteById(outboundProduct.getId());
            }
            Outbound outbound = outboundMapper.selectById(id);
            EntityWrapper<WayBill> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("tracking_number", outbound.getWarehouseWaybillNumber());
            wayBillService.delete(wrapper1);
            outboundMapper.deleteById(id);
            // 取消订单
            pointScanRecordService.addSysRecord(5, outbound.getWarehouseWaybillNumber(), null, "cancel", new Date(), "");
        }

    }

    //批量提交出库单
    public boolean batchSubmitOutbound(List<Long> oid) throws MyException {
        for (Long aLong : oid) {
            boolean b = submitOutbound(aLong);
            if (!b) {
                return false;
            }
        }
        return true;
    }

    //提交出库单
    public boolean submitOutbound(Long oid) throws MyException {
        Outbound outbound = outboundMapper.selectById(oid);
        outbound.setState(WareHouseState.WAIT_OUTBOUND);//待出库
        String outboundPDF = WareHouseLabelUtils.createOutboundPDF(outbound);
        outbound.setOutboundOrderCode(outboundPDF);
        Integer integer = outboundMapper.updateById(outbound);
        //  提交出库、待出库
        pointScanRecordService.addSysRecord(5, outbound.getWarehouseWaybillNumber(), "createOutbound", "commitOutbound", new Date(), "出库单号：" + outbound.getOutboundOrder());
        pointScanRecordService.addSysRecord(5, outbound.getWarehouseWaybillNumber(), "waitOutbound", null, new Date(), "");

        return integer > 0;
    }

    //管理员进行出库
    public boolean adminMarchOutbound(List<Long> ids) throws MyException {
        if (null != ids) {
            for (Long id : ids) {
                Outbound outbound = outboundMapper.selectById(id);
                outbound.setManageTime(new Date());
                outbound.setState(WareHouseState.IN_HAND_OUTBOUND);
                outboundMapper.updateById(outbound);
                // 处理中
                pointScanRecordService.addSysRecord(5, outbound.getWarehouseWaybillNumber(), "processeing", null, new Date(), "");

                //如果是一件打单，再进行更新快递单的状态
                if (Constant.OUTBOUND_TYPE_WAYBILL == outbound.getReplaceSend()) {
                    EntityWrapper<WayBill> wrapper1 = new EntityWrapper<>();
                    wrapper1.eq("tracking_number", outbound.getWarehouseWaybillNumber());
                    WayBill wayBill = wayBillService.selectOne(wrapper1);
                    //8：海外仓运单进行出库后
                    wayBill.setState(8);
                    wayBillService.updateById(wayBill);
                }

            }
            return true;
        }
        return false;
    }

    //管理员批量确定出库
    @Transactional
    public List<BatchOutboundError> adminBatchConfirmOutbound(List<AdminOutboundParam> params) throws Exception {
        List<BatchOutboundError> errors = new ArrayList<>();
        for (AdminOutboundParam param : params) {
            BatchOutboundError error = new BatchOutboundError();
            //出库单ID
            Long id = param.getOutboundId();
            Outbound outbound = outboundMapper.selectById(param.getOutboundId());
            Integer integer = adminConfirmOutbound(param);
            if (integer == 1) {
                //成功
                error.setCode(200);
                error.setErrorMsg("出库成功");
            } else if (integer == -1) {
                //余额不足
                error.setCode(202);
                error.setOutboundOrder(outbound.getOutboundOrder());
                error.setErrorMsg("余额不足");
                errors.add(error);
                return errors;
            } else if (integer == 0) {
                //出库异常
                error.setCode(300);
                error.setErrorMsg("出库异常");
            }
            error.setOutboundOrder(outbound.getOutboundOrder());
            errors.add(error);
        }
        return errors;
    }


    //异常信息类
    @Data
    public class BatchOutboundError {
        //异常出库单号
        private String outboundOrder;
        private Integer code;
        private String errorMsg;
    }

    //管理员确定出库
    @Transactional
    public Integer adminConfirmOutbound(AdminOutboundParam param) throws Exception {
        //管理员出库时间
        long outboundDate = System.currentTimeMillis();
        Long loginUserId = ShiroUtil.getLoginUserId();
        //出库单ID
        Long outboundId = param.getOutboundId();
        //海外仓仓储项目数量
        Integer warehouseProjectNumber = param.getWarehouseProjectNumber();
        //海外仓操作费ID 、操作数量
        List<AdminOutboundHandleParam> handleParams = param.getHandleParams();
        //海外仓包装费ID 、海外仓包装费数量
        List<WarehouseMaterialCostParam> warehouseMaterialCostParams = param.getWarehouseMaterialCostParams();
        //汇率
        List<Config> configs = configService.selectList(null);
        Config config = configs.get(6);//海外仓汇率
        double sumPrice = 0;//出库费用总和
        //出库单信息
        Outbound outbound = outboundMapper.selectById(outboundId);
        //打单用户ID
        Integer userId = outbound.getOutboundUserId();
        /*
         *
         *   第一部分收费（运单费ｚｏｎｅ）
         *
         */
        double warehouseCostPrice = 0;//运单费用
        String zone = outbound.getZone();
        WarehouseCost warehouseCost = warehouseCostService.getUserWaybillPrice(outbound.getOutboundWeight());
        //一件代发---收取运单费
        if (outbound.getReplaceSend() == 1) {
            if (null == warehouseCost) {
                WarehouseCost maxWarehouseCost = warehouseCostService.getMaxPrice();
                warehouseCostPrice = warehouseCostService.getWarehouseCostPrice(maxWarehouseCost, zone);
            } else {
                warehouseCostPrice = warehouseCostService.getWarehouseCostPrice(warehouseCost, zone);
            }
        }
        /*
         *
         *   第二部分收费(包装费)
         *
         */
        double packPrice = 0;//包装费
        if (warehouseMaterialCostParams != null) {
            for (WarehouseMaterialCostParam warehouseMaterialCostParam : warehouseMaterialCostParams) {
                WarehouseMaterialCost warehouseMaterialCost = warehouseMaterialCostService.selectById(warehouseMaterialCostParam.getWarehouseMaterialCostId());
                packPrice = packPrice + warehouseMaterialCost.getPackPrice() * warehouseMaterialCostParam.getWarehouseMaterialCostNumber();
            }
        }
        /*
         *
         *   第三部分收费(操作费)
         *
         */
        double handlePrice = 0;//操作费
        if (handleParams != null) {
            for (AdminOutboundHandleParam adminOutboundHandleParam : handleParams) {
                WarehouseHandleCost wrehouseHandleCost = warehouseHandleCostService.selectById(adminOutboundHandleParam.getHandleId());
                handlePrice = handlePrice + wrehouseHandleCost.getChargePrice() * adminOutboundHandleParam.getHandleNumber();
            }
        }
        /*
         *
         *   第四部分收费(仓储费)
         *
         */
        //海外仓仓储项目ID
        Long warehouseProjectId = param.getWarehouseProjectId();
        double rentPrice = 0;
        if (warehouseProjectId != null && warehouseProjectId != 0) {
            EntityWrapper<WarehouseRentCost> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("charge_project_id", warehouseProjectId);
            //海外仓仓储项目对用的扣费模式
            List<WarehouseRentCost> warehouseRentCosts = warehouseRentCostService.selectList(wrapper1);

            EntityWrapper<OutboundProduct> wrapper = new EntityWrapper<>();
            wrapper.eq("outbound_id", outboundId);
            List<OutboundProduct> outboundProductList = outboundProductService.selectList(wrapper);
            //当前出库产品列表
            boolean flag = false;
            for (OutboundProduct outboundProduct : outboundProductList) {
                Long productId = outboundProduct.getProductId();//出库产品ID
                if (flag) {
                    break;
                }
                Integer productNumber = outboundProduct.getProductNumber();//出库产品数量
                EntityWrapper<ProductStorage> wrapper2 = new EntityWrapper<>();
                wrapper2.eq("product_id", productId);
                wrapper2.orderBy("product_storage_time", true);
                List<ProductStorage> productStorageList = productStorageService.selectList(wrapper2);
                for (ProductStorage productStorage : productStorageList) {
                    //入库数量
                    Integer number = productStorage.getProductNumber();
                    //入库日前
                    long inventoryTime = productStorage.getProductStorageTime().getTime();
                    //存放时间（天）
                    long storageTime = (outboundDate - inventoryTime) / (3600 * 24 * 1000);

                    //仓租费用计算
                    if (warehouseProjectId == 1) {

                        /*
                         *
                         *       仓租项目一
                         *
                         */

                        //走首月仓储计费
                        if (storageTime <= 30) {
                            //未满一天算一天
                            if (storageTime <= 0) {
                                storageTime = 1;
                            }
                            WarehouseRentCost warehouseRentCost = warehouseRentCosts.get(0);
                            //  箱/天
                            Double chargePrice = warehouseRentCost.getChargePrice();
                            //仓储费
                            rentPrice = warehouseProjectNumber * chargePrice * storageTime;
                        } else {
                            //次月仓储计费
                            WarehouseRentCost warehouseRentCost = warehouseRentCosts.get(1);
                            //  箱/天
                            Double chargePrice = warehouseRentCost.getChargePrice();
                            //仓储费
                            rentPrice = warehouseProjectNumber * chargePrice * (storageTime - freeDay);
                        }
                        flag = true;
                        break;
                    } else if (warehouseProjectId == 2) {
                        /*
                         *
                         *       仓租项目二
                         *
                         */
                        //当前月/一号时间
                        Date date = initDateByMonth();
                        //当前月一号之后库存总数
                        List<Long> productIds = outboundProductList.stream().map(o -> o.getProductId()).collect(Collectors.toList());
                        EntityWrapper<ProductStorage> wrapper3 = new EntityWrapper<>();
                        wrapper3.eq("product_id", productIds);
                        wrapper3.ge("product_storage_time", date);
                        List<ProductStorage> productStorages = productStorageService.selectList(wrapper3);
                        double productSumNumber = productStorages.stream().mapToDouble(ProductStorage::getProductNumber).sum();
                        //当前出库总数
                        double outboundProductSumNumber = outboundProductList.stream().mapToDouble(OutboundProduct::getProductNumber).sum();
                        //出库数量大于或等于 50%
                        if ((outboundProductSumNumber / productSumNumber) >= 0.5) {
                            //免费
                            rentPrice = 0;
                        } else {
                            //收费
                            if (storageTime <= 0) {
                                storageTime = 1;
                            }
                            WarehouseRentCost warehouseRentCost = warehouseRentCosts.get(1);
                            rentPrice = warehouseProjectNumber * warehouseRentCost.getChargePrice() * storageTime;
                        }
                        flag = true;
                        break;
                    } else if (warehouseProjectId == 3) {
                        /*
                         *
                         *       仓租项目三
                         *
                         */
                        if (storageTime <= freeDay) {
                            //免费
                            rentPrice = 0;
                        } else {
                            //收费
                            WarehouseRentCost warehouseRentCost = warehouseRentCosts.get(1);
                            WarehouseRentCost warehouseRentCost2 = warehouseRentCosts.get(2);
                            //存放时间
                            long storageTime1 = (outboundDate - inventoryTime) / (3600 * 24 * 1000);
                            long chargeDay = storageTime1 - freeDay;
                            if (chargeDay <= 0) {
                                chargeDay = 1;
                            }
                            if (number < productNumber) {
                                productNumber = productNumber - number;
                                //当前产品仓库存储价格
                                double productWarePrice = 0;
                                if (storageTime <= 60) {
                                    //大于30天小于60天
                                    productWarePrice = number * chargeDay * warehouseRentCost.getChargePrice();
                                } else {
                                    //大于60天
                                    productWarePrice = number * chargeDay * warehouseRentCost2.getChargePrice();
                                }
                                rentPrice += productWarePrice;
                            } else {
                                int endNumber = number - productNumber;
                                //当前产品仓库存储价格
                                double productWarePrice = 0;
                                if (storageTime <= 60) {
                                    //大于30天小于60天
                                    productWarePrice = number * chargeDay * warehouseRentCost.getChargePrice();
                                } else {
                                    //大于60天
                                    productWarePrice = number * chargeDay * warehouseRentCost2.getChargePrice();
                                }
                                rentPrice += productWarePrice;
                                //修改数据之前判断余额是否充足
                                //总费用
                                sumPrice = (warehouseCostPrice + packPrice + handlePrice + rentPrice) * Double.parseDouble(config.getV());
                                EntityWrapper<UserAccount> wrapper3 = new EntityWrapper<>();
                                wrapper3.eq("user_id", userId);
                                UserAccount userAccount = userAccountService.selectOne(wrapper3);
                                double balance = userAccount.getBalance() - sumPrice;
                                if (balance < 0) {
                                    log.info("账户余额不足....交易金额为：" + sumPrice + "....扣费用户为：" + userId);
                                    return -1;
                                }
                                //产品未出完,修改产品库存数据
                                productStorage.setProductNumber(endNumber);
                                productStorageService.updateById(productStorage);
                                break;
                            }
                        }
                    }
                }
            }
        }
        //总费用
        sumPrice = (warehouseCostPrice + packPrice + handlePrice + rentPrice) * Double.parseDouble(config.getV());
        EntityWrapper<UserAccount> wrapper6 = new EntityWrapper<>();
        wrapper6.eq("user_id", userId);
        UserAccount userAccount = userAccountService.selectOne(wrapper6);
        double balance = userAccount.getBalance() - sumPrice;
        if (balance < 0) {
            log.info("账户余额不足....交易金额为：" + sumPrice + "....扣费用户为：" + userId);
            return -1;
        }

        //一件代发
        if (outbound.getReplaceSend() == 1) {
            String warehouseWaybillNumber = outbound.getWarehouseWaybillNumber();
            EntityWrapper<WayBill> wrapper3 = new EntityWrapper<>();
            wrapper3.eq("tracking_number", warehouseWaybillNumber);
            //出库单对应的运单价格
            WayBill wayBill = wayBillService.selectOne(wrapper3);
            double price = warehouseCostPrice * Double.parseDouble(config.getV());
            wayBill.setPrice(price);
            wayBillService.updateById(wayBill);
        }

        userAccount.setBalance(balance);
        boolean b = userAccountService.updateById(userAccount);
        DealDetail dealDetail = new DealDetail();
        dealDetail.setWayBillId(outbound.getId().intValue());
        dealDetail.setBalance(balance);
        dealDetail.setUserId(userId);
        dealDetail.setState(1);//成功
        dealDetail.setDealType(DealDetail.TYPE_CHARGING);//扣费
        dealDetail.setDealAmount(sumPrice);
        dealDetail.setDealTime(new Date());
        dealDetail.setWayBillId(outbound.getId().intValue());
        dealDetail.setTrackingNumber(outbound.getWarehouseWaybillNumber());
        //一件代发
        if (outbound.getReplaceSend() == 1) {
            dealDetail.setFlag(2);
        } else if (outbound.getReplaceSend() == 2) {
            dealDetail.setFlag(3);
        }
        dealDetailService.insert(dealDetail);

        log.info("出库交易成功....交易金额为：" + sumPrice + "....扣费用户为：" + userId);
        outbound.setState(WareHouseState.ALREADY_OUTBOUND);//已出库
        outbound.setOutboundTime(new Date());
        outboundMapper.updateById(outbound);
        if (b) {
            //发送物流通知出库
            NoticeVo noticeVo = new NoticeVo();
            noticeVo.setContent("您的订单已出库");
            noticeVo.setCreateTime(new Date());
            noticeVo.setFromUserId(loginUserId);
            noticeVo.setToUserId(outbound.getUserId());
            noticeVo.setNoticeType(ResponseCode.NOTICE_LOGISTICS);
            noticeVo.setTitle("出库通知");
            noticeVo.setOrderTitle("出库单号");
            noticeVo.setTrackNumber(outbound.getOutboundOrder());
            // 出库
            pointScanRecordService.addSysRecord(5, outbound.getWarehouseWaybillNumber(), "outbounded", null, new Date(), "");
            //发送出库通知..
            try {
                WebSocketServer.sendInfo(JSONObject.toJSONString(noticeVo), String.valueOf(outbound.getUserId()), ResponseCode.NOTICE_LOGISTICS, loginUserId);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //仓库产品预警
            EntityWrapper<ProductInfo> wrapper2 = new EntityWrapper<>();
            wrapper2.eq("user_id", outbound.getUserId());
            List<ProductInfo> productInfos = productInfoService.selectList(wrapper2);
            for (ProductInfo productInfo : productInfos) {
                if (productInfo.getInventoryNumber() <= productInfo.getInventoryWarnNumber()) {
                    NoticeVo noticeVoProduct = new NoticeVo();
                    noticeVoProduct.setNoticeType(ResponseCode.NOTICE_PRODUCT);
                    noticeVoProduct.setTitle("库存预警通知");
                    noticeVoProduct.setCreateTime(new Date());
                    noticeVoProduct.setContent("您的产品数量已达库存预警值");
                    noticeVoProduct.setToUserId(outbound.getUserId());
                    noticeVoProduct.setInventoryNumber(productInfo.getInventoryNumber());
                    noticeVoProduct.setInventoryWarnNumber(productInfo.getInventoryWarnNumber());
                    noticeVoProduct.setSkuCode(productInfo.getSkuCode());
                    noticeVoProduct.setProductName(productInfo.getCName());
                    //发送库存预警通知...
                    try {
                        WebSocketServer.sendInfo(JSONObject.toJSONString(noticeVo), String.valueOf(outbound.getUserId()), ResponseCode.NOTICE_PRODUCT, loginUserId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return 1;
        }
        return 0;
    }

    //管理端出库单列表
    public List<Outbound> adminOutboundList(OutboundListParam param) {
        ShiroUtil.getLoginUserId();
        List<String> warehouseWaybillNumbers = param.getWarehouseWaybillNumbers();
        Page<Outbound> page = new Page<>(param.getCurrent(), param.getSize());
        EntityWrapper<Outbound> wrapper = new EntityWrapper<>();
        wrapper.eq("state", param.getState());
        if (param.getReplaceSend() != null) {
            wrapper.eq("replace_send", param.getReplaceSend());
        }
        //查询条件
        boolean flag = true;
        if (param.getState() == WareHouseState.WAIT_OUTBOUND) {
            if (!StringUtils.isEmpty(param.getCreateBeginTime())) {
                wrapper.ge("create_time", param.getCreateBeginTime());
                wrapper.orderBy("create_time", true);
                flag = false;
            }
        }
        if (param.getState() == WareHouseState.IN_HAND_OUTBOUND) {
            if (!StringUtils.isEmpty(param.getManageTime())) {
                wrapper.ge("manage_time", param.getManageTime());
                wrapper.orderBy("manage_time", true);
                flag = false;
            }
        }
        if (param.getState() == WareHouseState.ALREADY_OUTBOUND || param.getState() == WareHouseState.PROBLEMS_SINGLE) {
            if (!StringUtils.isEmpty(param.getOutboundTime())) {
                wrapper.ge("outbound_time", param.getOutboundTime());
                wrapper.orderBy("outbound_time", true);
                flag = false;
            }
        }

        if (flag) {
            wrapper.orderBy("create_time", false);
        }

        if (null != warehouseWaybillNumbers && warehouseWaybillNumbers.size() > 0 && (!"".equals(warehouseWaybillNumbers.get(0)))) { //判断是否需要根据单号查询
            wrapper.in("warehouse_waybill_number", warehouseWaybillNumbers);
        }
        if (!StringUtils.isEmpty(param.getSendName())) {
            wrapper.eq("receive_name", param.getSendName());
        }
        if (!StringUtils.isEmpty(param.getSkuCode())) {
            EntityWrapper<ProductInfo> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("sku_code", param.getSkuCode());
            List<ProductInfo> productInfos = productInfoService.selectList(wrapper1);
            List<OutboundProduct> outboundProducts = outboundProductService.selectList(null);
            List<Long> ids = new ArrayList<>();
            for (ProductInfo productInfo : productInfos) {
                Long pid = productInfo.getId();
                for (OutboundProduct outboundProduct : outboundProducts) {
                    if (outboundProduct.getProductId() == pid) {
                        ids.add(outboundProduct.getOutboundId());
                    }
                }
            }
            if (ids.size() > 0) {
                wrapper.in("id", ids);
            }
        }

        Page<Outbound> page1 = selectPage(page, wrapper);
        return page1.getRecords();
    }

    //用户端出库单列表
    public List<Outbound> outboundList(OutboundListParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        List<String> warehouseWaybillNumbers = param.getWarehouseWaybillNumbers();
        List<String> outboundOrders = param.getReceiptOrders();
        Page<Outbound> page = new Page<>(param.getCurrent(), param.getSize());
        EntityWrapper<Outbound> wrapper = new EntityWrapper<>();
        wrapper.eq("state", param.getState());
        wrapper.eq("user_id", loginUserId);
        if (param.getReplaceSend() != null) {
            wrapper.eq("replace_send", param.getReplaceSend());
        }
        //查询条件
        boolean flag = true;
        if (param.getState() == WareHouseState.ALREADY_CREATE_OUTBOUND || param.getState() == WareHouseState.WAIT_OUTBOUND) {
            if (!StringUtils.isEmpty(param.getCreateBeginTime())) {
                wrapper.ge("create_time", param.getCreateBeginTime());
                wrapper.orderBy("create_time", true);
                flag = false;
            }
        }
        if (param.getState() == WareHouseState.IN_HAND_OUTBOUND) {
            if (!StringUtils.isEmpty(param.getManageTime())) {
                wrapper.ge("manage_time", param.getManageTime());
                wrapper.orderBy("manage_time", true);
                flag = false;
            }
        }
        if (param.getState() == WareHouseState.ALREADY_OUTBOUND || param.getState() == WareHouseState.PROBLEMS_SINGLE) {
            if (!StringUtils.isEmpty(param.getOutboundTime())) {
                wrapper.ge("outbound_time", param.getOutboundTime());
                wrapper.orderBy("outbound_time", true);
                flag = false;
            }
        }
        if (flag) {
            wrapper.orderBy("create_time", false);
        }
        if (null != outboundOrders && outboundOrders.size() > 0 && (!"".equals(outboundOrders.get(0)))) { //判断是否需要根据单号查询
            wrapper.in("outbound_order", outboundOrders);
        }
        if (null != warehouseWaybillNumbers && warehouseWaybillNumbers.size() > 0 && (!"".equals(warehouseWaybillNumbers.get(0)))) { //判断是否需要根据单号查询
            wrapper.in("warehouse_waybill_number", warehouseWaybillNumbers);
        }

        if (!StringUtils.isEmpty(param.getSkuCode())) {
            EntityWrapper<ProductInfo> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("sku_code", param.getSkuCode());
            List<ProductInfo> productInfos = productInfoService.selectList(wrapper1);
            List<OutboundProduct> outboundProducts = outboundProductService.selectList(null);
            List<Long> ids = new ArrayList<>();
            for (ProductInfo productInfo : productInfos) {
                Long pid = productInfo.getId();
                for (OutboundProduct outboundProduct : outboundProducts) {
                    if (outboundProduct.getProductId() == pid) {
                        ids.add(outboundProduct.getOutboundId());
                    }
                }
            }
            if (ids.size() > 0) {
                wrapper.in("id", ids);
            }
        }

        Page<Outbound> page1 = selectPage(page, wrapper);
        return page1.getRecords();
    }

    //用户端出库单总数
    public int outboundCount(OutboundListParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        List<String> warehouseWaybillNumbers = param.getWarehouseWaybillNumbers();
        List<String> outboundOrders = param.getReceiptOrders();
        EntityWrapper<Outbound> wrapper = new EntityWrapper<>();
        wrapper.eq("state", param.getState());
        wrapper.eq("user_id", loginUserId);
        //查询条件
        boolean flag = true;
        if (param.getState() == WareHouseState.ALREADY_CREATE_OUTBOUND || param.getState() == WareHouseState.WAIT_OUTBOUND) {
            if (!StringUtils.isEmpty(param.getCreateBeginTime())) {
                wrapper.ge("create_time", param.getCreateBeginTime());
                wrapper.orderBy("create_time", true);
                flag = false;
            }
        }
        if (param.getState() == WareHouseState.IN_HAND_OUTBOUND) {
            if (!StringUtils.isEmpty(param.getManageTime())) {
                wrapper.ge("manage_time", param.getManageTime());
                wrapper.orderBy("manage_time", true);
                flag = false;
            }
        }
        if (param.getState() == WareHouseState.ALREADY_OUTBOUND || param.getState() == WareHouseState.PROBLEMS_SINGLE) {
            if (!StringUtils.isEmpty(param.getOutboundTime())) {
                wrapper.ge("outbound_time", param.getOutboundTime());
                wrapper.orderBy("outbound_time", true);
                flag = false;
            }
        }

        if (flag) {
            wrapper.orderBy("create_time", false);
        }
        if (null != outboundOrders && outboundOrders.size() > 0 && (!"".equals(outboundOrders.get(0)))) { //判断是否需要根据单号查询
            wrapper.in("outbound_order", outboundOrders);
        }
        if (null != warehouseWaybillNumbers && warehouseWaybillNumbers.size() > 0 && (!"".equals(warehouseWaybillNumbers.get(0)))) { //判断是否需要根据单号查询
            wrapper.in("warehouse_waybill_number", warehouseWaybillNumbers);
        }

        if (!StringUtils.isEmpty(param.getSkuCode())) {
            EntityWrapper<ProductInfo> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("sku_code", param.getSkuCode());
            List<ProductInfo> productInfos = productInfoService.selectList(wrapper1);
            List<OutboundProduct> outboundProducts = outboundProductService.selectList(null);
            List<Long> ids = new ArrayList<>();
            for (ProductInfo productInfo : productInfos) {
                Long pid = productInfo.getId();
                for (OutboundProduct outboundProduct : outboundProducts) {
                    if (outboundProduct.getProductId() == pid) {
                        ids.add(outboundProduct.getOutboundId());
                    }
                }
            }
            if (ids.size() > 0) {
                wrapper.in("id", ids);
            }
        }

        return outboundMapper.selectCount(wrapper);
    }

    //管理端出库单总数
    public int adminOutboundCount(OutboundListParam param) {
        ShiroUtil.getLoginUserId();
        List<String> warehouseWaybillNumbers = param.getWarehouseWaybillNumbers();
        EntityWrapper<Outbound> wrapper = new EntityWrapper<>();
        wrapper.eq("state", param.getState());
        //查询条件
        boolean flag = true;
        if (param.getState() == WareHouseState.WAIT_OUTBOUND) {
            if (!StringUtils.isEmpty(param.getCreateBeginTime())) {
                wrapper.ge("create_time", param.getCreateBeginTime());
                wrapper.orderBy("create_time", true);
                flag = false;
            }
        }
        if (param.getState() == WareHouseState.IN_HAND_OUTBOUND) {
            if (!StringUtils.isEmpty(param.getManageTime())) {
                wrapper.ge("manage_time", param.getManageTime());
                wrapper.orderBy("manage_time", true);
                flag = false;
            }
        }
        if (param.getState() == WareHouseState.ALREADY_OUTBOUND || param.getState() == WareHouseState.PROBLEMS_SINGLE) {
            if (!StringUtils.isEmpty(param.getOutboundTime())) {
                wrapper.ge("outbound_time", param.getOutboundTime());
                wrapper.orderBy("outbound_time", true);
                flag = false;
            }
        }
        if (flag) {
            wrapper.orderBy("create_time", false);
        }
        if (null != warehouseWaybillNumbers && warehouseWaybillNumbers.size() > 0 && (!"".equals(warehouseWaybillNumbers.get(0)))) { //判断是否需要根据单号查询
            wrapper.in("warehouse_waybill_number", warehouseWaybillNumbers);
        }
        if (!StringUtils.isEmpty(param.getSendName())) {
            wrapper.eq("send_name", param.getSendName());
        }
        if (!StringUtils.isEmpty(param.getSkuCode())) {
            EntityWrapper<ProductInfo> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("sku_code", param.getSkuCode());
            List<ProductInfo> productInfos = productInfoService.selectList(wrapper1);
            List<OutboundProduct> outboundProducts = outboundProductService.selectList(null);
            List<Long> ids = new ArrayList<>();
            for (ProductInfo productInfo : productInfos) {
                Long pid = productInfo.getId();
                for (OutboundProduct outboundProduct : outboundProducts) {
                    if (outboundProduct.getProductId() == pid) {
                        ids.add(outboundProduct.getOutboundId());
                    }
                }
            }
            if (ids.size() > 0) {
                wrapper.in("id", ids);
            }
        }
        return outboundMapper.selectCount(wrapper);
    }

    //批量出库
    public List<OutboundError> batchAddOutbound(BatchAddOutBoundParam param) {
        List<OutboundError> outboundErrors = new ArrayList<>();
        List<OutBoundParam> outBoundParams = param.getOutBoundParams();
        //封装出库单数据
        List<AddOutBoundParam> addOutBoundParams = DomainCopyUtil.mapList(outBoundParams, AddOutBoundParam.class);
        int i = 0;
        for (AddOutBoundParam addOutBoundParam : addOutBoundParams) {
            if (addOutBoundParam == null) {
                OutboundError outboundError = new OutboundError();
                outboundError.setErrorMsg("第" + (i + 1) + "个出库单: 地址信息有误");
                outboundError.setIndex(i);
                outboundError.setCode(311);
                outboundErrors.add(outboundError);
                continue;
            }
            addOutBoundParam.setLogisticsMode(param.getLogisticsMode());
            OutboundResponse response = addOutbound(addOutBoundParam);
            response.outboundError.setErrorMsg("第" + (i + 1) + "个出库单: " + response.outboundError.getErrorMsg());
            response.outboundError.setIndex(i);
            i++;
            if (response.outboundError.getCode() != 200) {
                outboundErrors.add(response.outboundError);
            }
        }
        return outboundErrors;
    }

    //创建出库单异常信息类
    @Data
    public static class OutboundResponse {
        private ResponseObject object = new ResponseObject();
        OutboundError outboundError;
    }

    //创建出库单异常信息类
    @Data
    public static class ResponseObject {
        private String outboundOrder;
        private Date createTime;
        private int skuNumber;
        private long id;
    }

    //创建出库单异常信息类
    @Data
    public static class OutboundError {
        //private Long produceId;
        private Integer code;
        private String errorMsg;
        private Integer index;
    }

    //非一件代发创建出库单
    @Transactional(rollbackFor = Exception.class)
    public OutboundService.OutboundResponse addNotReplaceSendOutbound(AddOutBoundParam param) {
        OutboundService.OutboundResponse outboundResponse = new OutboundResponse();
        Long loginUserId = ShiroUtil.getLoginUserId();//当前用户ID
        OutboundError outboundError = new OutboundError();
        outboundResponse.outboundError = outboundError;
        Outbound outbound = new Outbound();
        //1代发，2非代发
        outbound.setReplaceSend(param.getReplaceSend());
        outboundMapper.insert(outbound);
        String exportCity = param.getExportCity();//出口城市
        Integer logisticsMode = param.getLogisticsMode();//物流方式
        List<InventoryProducerParam> producerList = param.getProducerList();//出库产品种类及对应数量

        int productNumberSum = 0;//出库总数
        int productSum = 0;//sku总数
        double productWeightSum = 0;//总重量
        List<OutboundProduct> outboundProducts = new ArrayList<>();
        for (InventoryProducerParam producerParam : producerList) {
            productNumberSum = 0;
            ProductInfo productInfo = productInfoService.selectById(producerParam.getProducerId());
            if (productInfo == null) {
                continue;
            }
            for (InventoryProducerParam producerParam1 : producerList) {
                if (productInfo.getId() == producerParam1.getProducerId()) {
                    productNumberSum += producerParam1.getProducerNumber();
                    if (productNumberSum > productInfo.getInventoryNumber()) {
                        outboundError.setCode(301);
                        //outboundError.setProduceId(productInfo.getId());
                        outboundError.setErrorMsg("产品库存不足");
                        return outboundResponse;
                    }
                }

            }
        }

        productNumberSum = 0;//出库总数
        for (InventoryProducerParam producerParam : producerList) {
            Integer producerNumber = producerParam.getProducerNumber();
            productNumberSum += producerParam.getProducerNumber();
            productSum++;
            Long producerId = producerParam.getProducerId();
            ProductInfo productInfo = productInfoService.selectById(producerId);
            if (null == productInfo) {
                continue;
            }
            productWeightSum = productWeightSum + productInfo.getUnitWeight() * producerParam.getProducerNumber();

            //获取当前产品的库存 + 当前产品此次入库的数量
            Integer inventoryNumber = productInfo.getInventoryNumber();
            inventoryNumber = inventoryNumber - producerNumber;
            productInfo.setInventoryNumber(inventoryNumber);
            productInfoService.updateById(productInfo);//保存

            //产品、入库单中间表数据保存
            OutboundProduct outboundProduct = new OutboundProduct();
            outboundProduct.setOutboundId(outbound.getId());//出库库单ID
            outboundProduct.setProductId(producerId);//产品ID
            //此次出库的产品数量
            outboundProduct.setProductNumber(producerParam.getProducerNumber());
            if (producerParam.getProducerNumber() > 0) {
                outboundProducts.add(outboundProduct);
            }

        }


        outbound.setCreateTime(new Date());
        outbound.setExportCity(exportCity);
        outbound.setLogisticsMode(logisticsMode);
        String outboundOrder = userSacksMapper.getSacksNumber(14);
        outbound.setOutboundOrder(outboundOrder);
        outbound.setSkuOutboundNumber(productSum);
        outbound.setOutboundNumber(productNumberSum);
        outbound.setOutboundWeight(productWeightSum);
        outbound.setOutboundUserId(loginUserId.intValue());
        outbound.setUserId(loginUserId.intValue());
        outbound.setState(3);
        outboundMapper.updateById(outbound);

        if (outboundProducts.size() > 0) {
            //批量保存中间表数据
            outboundProductService.insertBatch(outboundProducts);
            outboundError.setCode(200);

            outboundResponse.object.createTime = outbound.getCreateTime();
            outboundResponse.object.outboundOrder = outbound.getOutboundOrder();
            outboundResponse.object.skuNumber = param.getProducerList().size();
            outboundResponse.object.id = outbound.getId();

            return outboundResponse;
        }
        outboundError.setCode(300);
        outboundError.setErrorMsg("产品数据异常");
        return outboundResponse;
    }


    //非一件代发创建出库单
    @Transactional(rollbackFor = Exception.class)
    public OutboundError updateNotReplaceSendOutbound(AddOutBoundParam param) {
        Outbound outbound = outboundMapper.selectById(param.getId());
        //删除中间表数据
        EntityWrapper<OutboundProduct> wrapper1 = new EntityWrapper<>();
        wrapper1.eq("outbound_id", param.getId());
        List<OutboundProduct> outboundProducts1 = outboundProductService.selectList(wrapper1);
        for (OutboundProduct outboundProduct : outboundProducts1) {
            Long productId = outboundProduct.getProductId();
            ProductInfo productInfo = productInfoService.selectById(productId);
            Integer inventoryNumber = productInfo.getInventoryNumber();
            inventoryNumber = inventoryNumber + outboundProduct.getProductNumber();
            productInfo.setInventoryNumber(inventoryNumber);
            productInfoService.updateById(productInfo);
            outboundProductService.deleteById(outboundProduct.getId());
        }

        Long loginUserId = ShiroUtil.getLoginUserId();//当前用户ID
        OutboundError outboundError = new OutboundError();

        //1代发，2非代发
        outbound.setReplaceSend(param.getReplaceSend());
        String exportCity = param.getExportCity();//出口城市
        Integer logisticsMode = param.getLogisticsMode();//物流方式
        List<InventoryProducerParam> producerList = param.getProducerList();//出库产品种类及对应数量

        int productNumberSum = 0;//出库总数
        int productSum = 0;//sku总数
        double productWeightSum = 0;//总重量
        List<OutboundProduct> outboundProducts = new ArrayList<>();
        for (InventoryProducerParam producerParam : producerList) {
            ProductInfo productInfo = productInfoService.selectById(producerParam.getProducerId());
            for (InventoryProducerParam producerParam1 : producerList) {
                productNumberSum += producerParam1.getProducerNumber();
                if (productNumberSum > productInfo.getInventoryNumber()) {
                    outboundError.setCode(301);
                    //outboundError.setProduceId(productInfo.getId());
                    outboundError.setErrorMsg("产品库存不足");
                    return outboundError;
                }
            }
        }
        productNumberSum = 0;//出库总数
        for (InventoryProducerParam producerParam : producerList) {
            Integer producerNumber = producerParam.getProducerNumber();
            productNumberSum += producerParam.getProducerNumber();
            productSum++;
            Long producerId = producerParam.getProducerId();
            ProductInfo productInfo = productInfoService.selectById(producerId);
            if (null == productInfo) {
                continue;
            }
            productWeightSum = productWeightSum + productInfo.getUnitWeight() * producerParam.getProducerNumber();

            //获取当前产品的库存 + 当前产品此次入库的数量
            Integer inventoryNumber = productInfo.getInventoryNumber();
            inventoryNumber = inventoryNumber - producerNumber;
            productInfo.setInventoryNumber(inventoryNumber);
            productInfoService.updateById(productInfo);//保存

            //产品、入库单中间表数据保存
            OutboundProduct outboundProduct = new OutboundProduct();
            outboundProduct.setOutboundId(outbound.getId());//出库库单ID
            outboundProduct.setProductId(producerId);//产品ID
            //此次入库的产品数量
            outboundProduct.setProductNumber(producerParam.getProducerNumber());
            if (producerParam.getProducerNumber() > 0) {
                outboundProducts.add(outboundProduct);
            }
        }

        outbound.setCreateTime(new Date());
        outbound.setExportCity(exportCity);
        outbound.setLogisticsMode(logisticsMode);
        String outboundOrder = userSacksMapper.getSacksNumber(14);
        outbound.setOutboundOrder(outboundOrder);
        outbound.setSkuOutboundNumber(productSum);
        outbound.setOutboundNumber(productNumberSum);
        outbound.setOutboundWeight(productWeightSum);
        outbound.setOutboundUserId(loginUserId.intValue());
        outbound.setUserId(loginUserId.intValue());
        outbound.setState(3);
        outboundMapper.updateById(outbound);
        if (outboundProducts.size() > 0) {
            //批量保存中间表数据
            outboundProductService.insertBatch(outboundProducts);
            outboundError.setCode(200);
            return outboundError;
        }
        outboundError.setCode(300);
        outboundError.setErrorMsg("产品数据异常");
        return outboundError;
    }


    //一件代发创建出库单
    public OutboundResponse addOutbound(AddOutBoundParam param) {
        OutboundResponse response = new OutboundResponse();
        Long loginUserId = ShiroUtil.getLoginUserId();//当前用户ID
        OutboundError outboundError = new OutboundError();
        response.outboundError = outboundError;
        Outbound outbound = new Outbound();
        //1代发，2非代发
        outbound.setReplaceSend(param.getReplaceSend());
        WayBillVo wayBillVo = new WayBillVo();
        outboundMapper.insert(outbound);
        String exportCity = param.getExportCity();//出口城市
        Integer logisticsMode = param.getLogisticsMode();//物流方式
        List<InventoryProducerParam> producerList = param.getProducerList();//出库产品种类及对应数量
        Integer receiveId = param.getReceiveId();//发件人ID
        Integer senderId = param.getSenderId();//收件人ID
        Receive receive = receiveService.selectById(receiveId);

        //计算zone及入境口岸
        ZoneDto zoneDto = zoneService.calculateDHLZone(receive.getPostalCode(), loginUserId);
        if (zoneDto == null) {
            outboundError.setCode(300);
            outboundError.setErrorMsg("入境口岸异常");
            return response;
        }
        String zone = zoneDto.getZone();//zone（1-9）
        String portEntryName = zoneDto.getPortEntryName();//入境口岸

        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("port_id", zoneDto.getPortEntryId());
        PortContact portContact = portContactService.selectOne(wrapper);

        Sender sender = new Sender();
        if (senderId != null) {
            sender = senderService.selectById(senderId);
        } else {
            sender.setPostalCodet(portContact.getConCodet());
            sender.setPostalCode(portContact.getConCode());
            sender.setAddressOne(portContact.getConAddressOne());
            sender.setAddressTwo(portContact.getConAddressTwo());
            sender.setCityEname(portContact.getConEcity());
            sender.setCompany(portContact.getConCompany());
            sender.setCountries(portContact.getConCountries());
            sender.setName(portContact.getConName());
            sender.setPhone(portContact.getConPhone());
            sender.setPhonePrefix(portContact.getConPhonePrefix());
            sender.setProvinceEname(portContact.getConEprovince());
            sender.setEmail(portContact.getConEmail());
            sender.setUserId(loginUserId);
            sender.setIsSave("0");
            senderService.insert(sender);
        }

        int productNumberSum = 0;//出库总数
        int productSum = 0;//sku总数
        Set<String> skuSet = new HashSet<>();
        double productWeightSum = 0;//总重量
        List<OutboundProduct> outboundProducts = new ArrayList<>();
        Map<Long, InventoryProducerParam> tempPMap = new HashMap<>();
        log.info(" producerList.size: " + producerList.size());
        for (InventoryProducerParam producerParam : producerList) {
            Long pId = producerParam.getProducerId();
            if (pId == null) {
                if (productNumberSum == 0) {
                    outboundError.setCode(301);
                    //outboundError.setProduceId(productInfo.getId());
                    outboundError.setErrorMsg("产品id：" + producerParam.getProducerId() + " 不存在");
                    return response;
                }
            }
            ProductInfo productInfo = productInfoService.selectById(producerParam.getProducerId());
            if (productInfo == null) {
                outboundError.setCode(301);
                //outboundError.setProduceId(productInfo.getId());
                outboundError.setErrorMsg("产品id：" + producerParam.getProducerId() + " 不存在");
                return response;
            }

            //进行排重
            log.info(" id: " + pId);
            if (tempPMap.containsKey(pId)) {
                InventoryProducerParam ip = tempPMap.get(pId);
                ip.setProducerNumber(ip.getProducerNumber() + producerParam.getProducerNumber());
            } else {
                InventoryProducerParam newIp = DomainCopyUtil.map(producerParam, InventoryProducerParam.class);
                tempPMap.put(pId, newIp);
            }
            log.info(" id: " + pId + ", tempPMap: " + tempPMap);

            productWeightSum += productInfo.getUnitWeight() * producerParam.getProducerNumber();
            productNumberSum += producerParam.getProducerNumber();
            int sum = 0;
            skuSet.add(productInfo.getSkuCode());
            //递归遍历在本次的出库sku中，是否有相同
            for (InventoryProducerParam producerParam1 : producerList) {
                if (producerParam1.getProducerId() == null) {
                    continue;
                }
                if (producerParam1.getProducerId().equals(producerParam.getProducerId())) {
                    sum += producerParam1.getProducerNumber();
                    if (sum > productInfo.getInventoryNumber()) {
                        outboundError.setCode(301);
                        //outboundError.setProduceId(productInfo.getId());
                        outboundError.setErrorMsg("产品: " + productInfo.getSkuCode() + " 库存不足");
                        return response;
                    }

                }
            }

            if (productNumberSum == 0) {
                outboundError.setCode(301);
                //outboundError.setProduceId(productInfo.getId());
                outboundError.setErrorMsg("产品: " + productInfo.getSkuCode() + " 出库数量为0");
                return response;
            }
        }


//        for (InventoryProducerParam producerParam : producerList) {
//            ProductInfo productInfo = productInfoService.selectById(producerParam.getProducerId());
//            productWeightSum = productWeightSum + productInfo.getUnitWeight() * producerParam.getProducerNumber();
//            for (InventoryProducerParam producerParam1 : producerList) {
//                productNumberSum += producerParam1.getProducerNumber();
//                if (productNumberSum > productInfo.getInventoryNumber()) {
//                    outboundError.setCode(301);
//                    //outboundError.setProduceId(productInfo.getId());
//                    outboundError.setErrorMsg("产品库存不足");
//                    return response;
//                }
//            }
//        }

        WayBill wayBill = new WayBill();

        //封装数据生成面单
        outbound.setCreateTime(new Date());
        outbound.setExportCity(exportCity);
        outbound.setLogisticsMode(logisticsMode);
        String outboundOrder = userSacksMapper.getSacksNumber(14);
        outbound.setOutboundOrder(outboundOrder);
        outbound.setZone(zone);
        outbound.setPortEntry(portEntryName);
        outbound.setState(WareHouseState.ALREADY_CREATE_OUTBOUND);
        outbound.setReceiveAddress(receive.getAddressTwo());
        outbound.setReceiveName(receive.getName());
        outbound.setSendAddress(sender.getAddressTwo());
        outbound.setSendName(sender.getName());
        outbound.setSkuOutboundNumber(skuSet.size());
        outbound.setOutboundNumber(productNumberSum);
        outbound.setOutboundWeight(productWeightSum);
        outbound.setOutboundUserId(loginUserId.intValue());
        outbound.setUserId(loginUserId.intValue());

        wayBill.setState(1);//一创建
        wayBill.setCarrierRoute(receive.getReceiveCarrierRoute());
        wayBill.setDeliveryPoint(receive.getReceiveDeliveryPoint());
        wayBill.setZone(zone);
        wayBill.setEntrySite(portEntryName);
        wayBill.setBillWeight(productWeightSum);
        wayBill.setDhlPackageId(DHLService.MAKE + DHLService.getOrderNo());
        wayBill.setUserId(loginUserId);
        wayBill.setReceiveName(receive.getName());
        wayBill.setSenderName(sender.getName());
        wayBill.setPortId(zoneDto.getPortEntryId());

        wayBill.setPortId(portContact.getId());

        //保存运单信息
        wayBillService.insert(wayBill);

        //保存联系人中间表数据
        WaybillContact wc = new WaybillContact();
        wc.setReceiveId(receiveId);
        wc.setSenderId(sender.getId());
        wc.setReceiveAddressOne(receive.getAddressOne());
        wc.setReceiveAddressTwo(receive.getAddressTwo());
        wc.setReceiveCity(receive.getCityCname());
        wc.setReceiveCompany(receive.getCompany());
        wc.setReceiveCountries(receive.getCountries());
        wc.setReceiveEmail(receive.getEmail());
        wc.setReceiveName(receive.getName());
        wc.setReceivePhone(receive.getPhone());
        wc.setReceivePhonePrefix(receive.getPhonePrefix());
        wc.setReceivePostalCode(receive.getPostalCode());
        wc.setReceivePostalCodet(receive.getPostalCodet());
        wc.setReceiveProvince(receive.getProvinceCname());

        wc.setSenderAddressOne(portContact.getConAddressOne());
        wc.setSenderAddressTwo(portContact.getConAddressTwo());
        wc.setSenderCity(portContact.getConEcity());
        wc.setSenderCompany(portContact.getConCompany());
        wc.setSenderCountries(portContact.getConCountries());
        wc.setSenderEmail(portContact.getConEmail());
        wc.setSenderName(portContact.getConName());
        wc.setSenderPhone(portContact.getConPhone());
        wc.setSenderPhonePrefix(portContact.getConPhonePrefix());
        wc.setSenderPostalCode(portContact.getConCode());
        wc.setSenderPostalCodet(portContact.getConCodet());
        wc.setSenderProvince(portContact.getConEprovince());
        wc.setWayBillId(wayBill.getId());
        wayBillContactService.insert(wc);

        outbound.setContactId(wc.getId());

        Parcel parcel = new Parcel();
        parcel.setAritcleDescribe("");
        parcel.setCommentOne("");
        parcel.setBillWeight(productWeightSum);
        parcel.setWaybillId(wayBill.getId());
        parcel.setUserId(loginUserId);
        //保存包裹喜信息
        parcelService.insert(parcel);

        Article article = new Article();
        article.setWaybillId(wayBill.getId());
        article.setParcleId(parcel.getId());
        articleService.insert(article);
        wayBillVo.setParcel(parcel);
        wayBillVo.setPortContact(portContact);
        wayBillVo.setReceive(receive);
        wayBillVo.setSender(sender);
        wayBillVo.setWayBill(wayBill);

        String dhlTrackingNumber = "";
        String labelData = "";
        String url = "";
        producerList = new ArrayList<>(tempPMap.values());
        //如果重量大于 10LB走DHL渠道，不然走EVS
        if (outbound.getOutboundWeight() >= DHL_LB_MIN) {
            List<LabelResponse.DataBean.ShipmentsBean> shipments = null;
            try {
                LabelResponse labelFull = dhlService.createLabelFull(wayBillVo, zoneDto.getPortEntryId());
                log.info(" labelFull: " + String.valueOf(labelFull));
                shipments = labelFull.getData().getShipments();
            } catch (Exception e) {
                log.error(" batchImportMore error: " + String.valueOf(e));
                //删除数据库数据
                wayBillService.deleteById(wayBill.getId());
                articleService.deleteById(article.getId());
                parcelService.deleteById(parcel.getId());
                wayBillContactService.deleteById(wc.getId());
                outboundMapper.deleteById(outbound.getId());
                //插入数据失败
                outboundError.setCode(202);
                outboundError.setErrorMsg("DHL打单错误");
                return response;
            }
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
                                try {
                                    pdfFile.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            labelData = PDFUtils.getPDFBinary(pdfFile);
                            pdfFile.delete();
                            pdfFile.getParentFile().delete();
                        }
                        //DHL面单url地址
                        url = labelDetail.getUrl();
                        wayBill.setCoding(labelData);
                        wayBill.setTrackingNumber(dhlTrackingNumber);
                        wayBill.setDhlCodingUrl(url);
                        wayBill.setChannel("dhl");
                        wayBill.setBillWeight(productWeightSum);
                        wayBill.setService("P");
                        wayBillService.updateById(wayBill);
                    }
                }
            }
            outbound.setWarehouseWaybillNumber(dhlTrackingNumber);
            outbound.setWaybillCode(labelData);
            outbound.setDhlUrl(url);
            outbound.setChannel("dhl");
            outboundMapper.updateById(outbound);

            productNumberSum = 0;//出库总数
            productWeightSum = 0;//出库总重量
            for (InventoryProducerParam producerParam : producerList) {
                Integer producerNumber = producerParam.getProducerNumber();
                productNumberSum += producerParam.getProducerNumber();
                productSum++;
                Long producerId = producerParam.getProducerId();
                ProductInfo productInfo = productInfoService.selectById(producerId);
                if (null == productInfo) {
                    continue;
                }
                productWeightSum = productWeightSum + productInfo.getUnitWeight() * producerParam.getProducerNumber();

                //获取当前产品的库存 - 当前产品此次出库的数量
                Integer inventoryNumber = productInfo.getInventoryNumber();
                inventoryNumber = inventoryNumber - producerNumber;
                productInfo.setInventoryNumber(inventoryNumber);
                productInfoService.updateById(productInfo);//保存

                //产品、入库单中间表数据保存
                OutboundProduct outboundProduct = new OutboundProduct();
                outboundProduct.setOutboundId(outbound.getId());//出库库单ID
                outboundProduct.setProductId(producerId);//产品ID
                //此次出库的产品数量
                outboundProduct.setProductNumber(producerParam.getProducerNumber());
                if (producerParam.getProducerNumber() > 0) {
                    outboundProducts.add(outboundProduct);
                }
            }
            if (outboundProducts.size() > 0) {
                //批量保存中间表数据
                outboundProductService.insertBatch(outboundProducts);
                outboundError.setCode(200);
                outboundError.setErrorMsg("创建成功");
                response.object.createTime = outbound.getCreateTime();
                response.object.outboundOrder = outbound.getOutboundOrder();
                response.object.skuNumber = skuSet.size();
                response.object.id = outbound.getId();
                return response;
            }
            //插入数据失败
            outboundError.setCode(201);
            outboundError.setErrorMsg("地址信息错误");
            return response;
        }

        StringBuilder strUrl = new StringBuilder();
        strUrl.append("https://stg-secure.shippingapis.com/ShippingAPI.dll?API=eVS&XML=<eVSRequest USERID=\"872FMUSS6909\">"); //不变
        strUrl.append("<Option></Option>");
        strUrl.append("<Revision></Revision>");
        strUrl.append("<ImageParameters>");
        strUrl.append("<ImageParameter>4x6LABELP</ImageParameter>"); //标签大小
        strUrl.append("<XCoordinate>0</XCoordinate>");   //X坐标
        strUrl.append("<YCoordinate>900</YCoordinate>"); //Y坐标
        strUrl.append("</ImageParameters>");
        strUrl.append("<FromName>" + portContact.getConName() + "</FromName>"); //发件人姓名
        strUrl.append("<FromFirm>" + (StringUtils.isEmpty(portContact.getConCompany()) ? "" : portContact.getConCompany()) + "</FromFirm>");   //发件人公司
        strUrl.append("<FromAddress1>" + (StringUtils.isEmpty(portContact.getConAddressOne()) ? "" : portContact.getConAddressOne()) + "</FromAddress1>");  //发件人地址1
        strUrl.append("<FromAddress2>" + portContact.getConAddressTwo() + "</FromAddress2>"); //发件人地址2
        strUrl.append("<FromCity>" + portContact.getConEcity() + "</FromCity>"); //发件人城市
        strUrl.append("<FromState>" + portContact.getConEprovince() + "</FromState>"); //发件人国家
        strUrl.append("<FromZip5>" + portContact.getConCode() + "</FromZip5>"); //邮政编码
        strUrl.append("<FromZip4>" + portContact.getConCodet() + "</FromZip4>");  //不变
        strUrl.append("<FromPhone>" + portContact.getConPhone() + "</FromPhone>"); //发件人电话
        strUrl.append("<AllowNonCleansedOriginAddr>True</AllowNonCleansedOriginAddr>"); //不变
        strUrl.append("<ToName>" + receive.getName() + "</ToName>"); //收件人姓名
        strUrl.append("<ToFirm>" + (StringUtils.isEmpty(receive.getCompany()) ? "" : receive.getCompany()) + "</ToFirm>"); //收件人公司
        strUrl.append("<ToAddress1>" + (StringUtils.isEmpty(receive.getAddressOne()) ? "" : receive.getAddressOne()) + "</ToAddress1>"); //收件人地址1
        strUrl.append("<ToAddress2>" + receive.getAddressTwo() + "</ToAddress2>"); //收件人地址1
        strUrl.append("<ToCity>" + receive.getCityEname() + "</ToCity>"); //收件人城市
        strUrl.append("<ToState>" + receive.getProvinceEname() + "</ToState>"); //收件人国家
        strUrl.append("<ToZip5>" + receive.getPostalCode() + "</ToZip5>"); //收件人邮政编码
        strUrl.append("<ToZip4>" + receive.getPostalCodet() + "</ToZip4>"); //不变
        strUrl.append("<ToPhone>" + receive.getPhone() + "</ToPhone>"); //收件人电话
        strUrl.append("<AllowNonCleansedDestAddr>True</AllowNonCleansedDestAddr>"); //不变
        strUrl.append("<WeightInOunces>" + outbound.getOutboundWeight() + "</WeightInOunces>"); //包裹重量
        if (outbound.getOutboundWeight() < 1) {
            wayBill.setService("F");
            strUrl.append("<ServiceType>FIRST CLASS</ServiceType>"); //服务类型
        } else {
            wayBill.setService("P");
            strUrl.append("<ServiceType>PRIORITY</ServiceType>"); //服务类型
        }
        /*if ("RECTANGULAR".equals(wayBill.getParcel().getParcelShape())) {
            strUrl.append("<Container>RECTANGULAR</Container>"); //物品类型
            strUrl.append("<Width>" + wayBill.getParcel().getWidth() + "</Width>"); //宽
            strUrl.append("<Length>" + wayBill.getParcel().getLengths() + "</Length>"); //长
            strUrl.append("<Height>" + wayBill.getParcel().getHeight() + "</Height>"); //高
        } else {
            //strUrl.append("<Container>PACKAGE SERVICE</Container>"); //物品类型
        }*/

        //strUrl.append("<Machinable>" + wayBill.getParcel().getIsCoubid() + "</Machinable>"); //是否是长方体（true,false）
        strUrl.append("<CustomerRefNo>FC</CustomerRefNo>"); //用户内部使用编号，可变
        strUrl.append("<ExtraServices><ExtraService>155</ExtraService></ExtraServices>"); //可选
        strUrl.append("<ReceiptOption>None</ReceiptOption>"); //不变
        strUrl.append("<ImageType>PDF</ImageType>"); //不变
        strUrl.append("<PrintCustomerRefNo>False</PrintCustomerRefNo>"); //是否打印客户编号
        strUrl.append("</eVSRequest>");
        System.out.println("URL----------------------->" + strUrl);
        Map<String, String> stringMap;
        String coding = "";
        try {
            String ss = testCreateBill(strUrl.toString());
            stringMap = XmlUtils.xmlToMap(ss);

            coding = stringMap.get("LabelImage");//base64编码
            if (coding == null) {
                //删除数据库数据
                wayBillService.deleteById(wayBill.getId());
                articleService.deleteById(article.getId());
                parcelService.deleteById(parcel.getId());
                wayBillContactService.deleteById(wc.getId());
                outboundMapper.deleteById(outbound.getId());
                //插入数据失败
                outboundError.setCode(201);
                outboundError.setErrorMsg("地址信息错误");
                return response;
            }
            //String zone1 = stringMap.get("Zone");//区域：如 08
            String substring = stringMap.get("BarcodeNumber").substring(8, stringMap.get("BarcodeNumber").length());//追踪号码

            wayBill.setCoding(coding);
            wayBill.setTrackingNumber(substring);
            wayBill.setChannel("fmuss");
            wayBill.setBillWeight(productWeightSum);
            wayBillService.updateById(wayBill);
            outbound.setWarehouseWaybillNumber(substring);
            outbound.setWaybillCode(coding);
            outbound.setChannel("fmuss");
            outboundMapper.updateById(outbound);
            // 记录运单轨迹
            pointScanRecordService.addSysRecord(5, substring, "createOutbound", null, new Date(), "出库单号：" + outbound.getOutboundOrder());
            productNumberSum = 0;//出库总数
            productWeightSum = 0;//出库总重量
            for (InventoryProducerParam producerParam : producerList) {
                Integer producerNumber = producerParam.getProducerNumber();
                productNumberSum += producerParam.getProducerNumber();
                productSum++;
                Long producerId = producerParam.getProducerId();
                ProductInfo productInfo = productInfoService.selectById(producerId);
                if (null == productInfo) {
                    continue;
                }
                productWeightSum = productWeightSum + productInfo.getUnitWeight() * producerParam.getProducerNumber();

                //获取当前产品的库存 + 当前产品此次入库的数量
                Integer inventoryNumber = productInfo.getInventoryNumber();
                inventoryNumber = inventoryNumber - producerNumber;
                productInfo.setInventoryNumber(inventoryNumber);
                productInfoService.updateById(productInfo);//保存

                //产品、入库单中间表数据保存
                OutboundProduct outboundProduct = new OutboundProduct();
                outboundProduct.setOutboundId(outbound.getId());//出库库单ID
                outboundProduct.setProductId(producerId);//产品ID
                //此次入库的产品数量
                outboundProduct.setProductNumber(producerParam.getProducerNumber());
                if (producerParam.getProducerNumber() > 0) {
                    outboundProducts.add(outboundProduct);
                }
            }

            if (outboundProducts.size() > 0) {
                //批量保存中间表数据
                outboundProductService.insertBatch(outboundProducts);
                //成功
                response.object.createTime = outbound.getCreateTime();
                response.object.outboundOrder = outbound.getOutboundOrder();
                response.object.skuNumber = skuSet.size();
                response.object.id = outbound.getId();
                outboundError.setCode(200);
                outboundError.setErrorMsg("成功");
                return response;
            }
            //插入数据失败
            outboundError.setCode(201);
            outboundError.setErrorMsg("地址信息错误");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //插入数据失败
        outboundError.setCode(201);
        outboundError.setErrorMsg("地址信息错误");
        return response;
    }

    //出库单详情(一件代发)
    public OutboundDetailsVo getOutboundDetails(long id) {
        return outboundDetailsMapper.getOutboundDetails(id);
    }

    //出库单详情(非一件代发)
    public NotReplaceSendOutboundDetailsVo getNotReplaceSendOutboundDetails(Long id) {
        return outboundDetailsMapper.getNotReplaceSendOutboundDetails(id);
    }

    //修改出库单的详情信息(一件代发)
    public UpdateOutboundDetailsVo getUpdateOutboundDetails(long id) {
        return outboundDetailsMapper.UpdateOutboundDetails(id);
    }

    //修改出库单的详情信息(非一件代发)
    public UpdateReplaceSendOutboundDetailsVo UpdateReplaceSendOutboundDetails(long id) {
        return outboundDetailsMapper.UpdateReplaceSendOutboundDetails(id);
    }


    public String batchExportWayPDF(List outboundIds) {
        List<Outbound> outboundList = outboundMapper.selectBatchIds(outboundIds);
        User user = ShiroUtil.getLoginUser();
        String userName = user.getUsername();
        String pdfUserPath = PathUtils.resDir + userName;
        String pdfPath = pdfUserPath + "/" + UUID.randomUUID().toString() + ".pdf";

        List<String> pdfPaths = new ArrayList<String>();
        String tmpDirPath = pdfUserPath + "/outboundPDF/";
        File tmpDir = new File(tmpDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        //生成每一张临时的中间PDF
        for (int i = 0; i < outboundList.size(); i++) {
            Outbound outbound = outboundList.get(i);
            String base64Str = outbound.getOutboundOrderCode();
            String newPdfPath = tmpDirPath + UUID.randomUUID().toString() + ".pdf";
            PDFUtils.base64StringToPDF(base64Str, newPdfPath);
            pdfPaths.add(newPdfPath);
        }

        //生成最终合并的pdf
        PDFUtils.mergePDF(pdfPaths, pdfPath);

        //删除每一行临时的中间PDF
        for (String tmpPath : pdfPaths) {
            new File(tmpPath).delete();
        }

        return pdfPath;
    }

    public String batchExportOutboundWayBillPDF(List outboundIds) {
        List<Outbound> outboundList = outboundMapper.selectBatchIds(outboundIds);
        User user = ShiroUtil.getLoginUser();
        String userName = user.getUsername();
        String pdfUserPath = PathUtils.resDir + userName;
        String pdfPath = pdfUserPath + "/" + UUID.randomUUID().toString() + ".pdf";

        List<String> pdfPaths = new ArrayList<String>();
        String tmpDirPath = pdfUserPath + "/outboundPDF/";
        File tmpDir = new File(tmpDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        //生成每一张临时的中间PDF
        for (int i = 0; i < outboundList.size(); i++) {
            Outbound outbound = outboundList.get(i);
            String base64Str = outbound.getWaybillCode();
            String newPdfPath = tmpDirPath + UUID.randomUUID().toString() + ".pdf";
            PDFUtils.base64StringToPDF(base64Str, newPdfPath);
            pdfPaths.add(newPdfPath);
        }

        //生成最终合并的pdf
        PDFUtils.mergePDF(pdfPaths, pdfPath);

        //删除每一行临时的中间PDF
        for (String tmpPath : pdfPaths) {
            System.out.println(tmpPath);
            new File(tmpPath).delete();
        }

        return pdfPath;
    }


    public List<Outbound> createOutboundList(Map map) {
        return outboundMapper.createOutboundList(map);
    }

    public Integer createOutboundCount(Map map) {
        return outboundMapper.createOutboundCount(map);
    }

    //当月一号0点时间
    public Date initDateByMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public List<OutboundDetailsVo> getOutboundDetailList(List ids) {
        return outboundDetailsMapper.getOutboundDetailList(ids);
    }

}
