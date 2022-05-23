package com.hgups.express.business.dhl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.base.Strings;
import com.hgups.express.business.dhl.closeout.CloseoutParameter;
import com.hgups.express.business.dhl.closeout.CloseoutResponse;
import com.hgups.express.business.dhl.label.LabelParameter;
import com.hgups.express.business.dhl.label.LabelResponse;
import com.hgups.express.business.dhl.track.EventParameter;
import com.hgups.express.business.dhl.track.EventResponse;
import com.hgups.express.domain.*;
import com.hgups.express.service.waybillmgi.DhlPortService;
import com.hgups.express.service.waybillmgi.PortEntryService;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.util.XmlUtils;
import com.hgups.express.vo.WayBillVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 该类进行对DHL的4个API接口进行调用，其中错误响应的Java实体类已被忽略，后期根据业务需求来进行补充添加
 */
@Slf4j
@Service
public class DHLService  implements InitializingBean {

    @Resource
    private WayBillService wayBillService;
    @Resource
    private DhlPortService dhlPortService;
    @Resource
    private PortEntryService portEntryService;


    private String mToken = "";
    private long mExpireIn = 86400;
    private static final String USERNAME = "tracking.etao";
    private static final String PASSWORD = "jnF1n2Wt66S5amYG";
    private static final int CLIENT_ID = 55735;
    private static String PICK_UP = "5352172";
    private static String DISTRIBUTION_CENTER = "USSFO1";
    public static final String MAKE = "HGUPS";



    private static final int ERROR_CODE = 400;
    private static final int SUCCESS_CODE = 200;

    public static void main(String[] args) {
        DHLService service = new DHLService();
        //service.getToken();

        //一、创建运单
        //退货地址
        LabelParameter.ShipmentsBean.PackagesBean.ReturnAddressBean returnAddressBean  = new LabelParameter.ShipmentsBean.PackagesBean.ReturnAddressBean();
        returnAddressBean.setAddress1("Address 1");
        returnAddressBean.setCity("New York");
        returnAddressBean.setState("NY");
        returnAddressBean.setCompanyName("OneZeroBeat");
        returnAddressBean.setCompanyName("OneZeroBeat");
        returnAddressBean.setPostalCode("12345");
        returnAddressBean.setCountry("US");


        //包裹详情
        LabelParameter.ShipmentsBean.PackagesBean.PackageDetailsBean packageDetailsBean = new LabelParameter.ShipmentsBean.PackagesBean.PackageDetailsBean();
        packageDetailsBean.setBillingRef1("test bill ref1");
        packageDetailsBean.setBillingRef2("test bill ref2");
        packageDetailsBean.setCurrency("USD");
        boolean isF = true;
        packageDetailsBean.setMailFOrP(isF);
        packageDetailsBean.setPackageId("12345678"); //重点在意这个字段，用于内部排序
        packageDetailsBean.setWeight(2);
        packageDetailsBean.setWeightUom("LB");

        //收货人信息
        LabelParameter.ShipmentsBean.PackagesBean.ConsigneeAddressBean consigneeAddressBean = new LabelParameter.ShipmentsBean.PackagesBean.ConsigneeAddressBean();
        consigneeAddressBean.address1 = "Flushing Queen";
        consigneeAddressBean.setCompanyName("OneZeroBeat");
        consigneeAddressBean.setCity("New York");
        consigneeAddressBean.setState("NY");
        consigneeAddressBean.setPostalCode("12345");

        //包裹1
        LabelParameter.ShipmentsBean.PackagesBean packagesBean1 = new LabelParameter.ShipmentsBean.PackagesBean();
        packagesBean1.setReturnAddress(returnAddressBean);
        packagesBean1.setConsigneeAddress(consigneeAddressBean);
        packagesBean1.setPackageDetails(packageDetailsBean);

        List<LabelParameter.ShipmentsBean.PackagesBean> packagesBeanList = new ArrayList<>();
        packagesBeanList.add(packagesBean1);

        //运输1
        LabelParameter.ShipmentsBean shipmentsBean1 = new LabelParameter.ShipmentsBean();
        shipmentsBean1.setPackages(packagesBeanList);
        shipmentsBean1.setPickup(PICK_UP);
        shipmentsBean1.setDistributionCenter(DISTRIBUTION_CENTER);

        //面单创建对象
        LabelParameter parameter = new LabelParameter();
        List<LabelParameter.ShipmentsBean> shipments = new ArrayList<>();
        shipments.add(shipmentsBean1);

        parameter.setShipments(shipments);

        try {
            Thread.sleep(10*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //开始创建运单
        //service.createLabel(parameter);


        //二、关闭批次
        CloseoutParameter closeoutParameter = new CloseoutParameter();

        List<CloseoutParameter.CloseoutRequestsBean> closeoutRequestsBeans = new ArrayList<>();
        CloseoutParameter.CloseoutRequestsBean closeoutRequestsBean1 = new CloseoutParameter.CloseoutRequestsBean();

        List<CloseoutParameter.CloseoutRequestsBean.PackagesBean> packages = new ArrayList<>();
        CloseoutParameter.CloseoutRequestsBean.PackagesBean cpackagesBean1 = new CloseoutParameter.CloseoutRequestsBean.PackagesBean();
        cpackagesBean1.setPackageId("123456789");
        CloseoutParameter.CloseoutRequestsBean.PackagesBean cpackagesBean2 = new CloseoutParameter.CloseoutRequestsBean.PackagesBean();
        cpackagesBean2.setPackageId("12345678");
        packages.add(cpackagesBean1);
        packages.add(cpackagesBean2);
        closeoutRequestsBean1.setPackages(packages);

        closeoutRequestsBeans.add(closeoutRequestsBean1);
        closeoutParameter.setCloseoutRequests(closeoutRequestsBeans);

//        CloseoutResponse closeoutResponse = service.closeout(closeoutParameter);
        //三、预上线
        EventParameter.MailItem.Event.Delivery delivery =  new EventParameter.MailItem.Event.Delivery();
        EventParameter.MailItem.Event event = new EventParameter.MailItem.Event();
        event.setEventClass("PICKUP");
        event.setEventId("CE108110");
        event.setEventDescription("Item is pickup");
        event.setLocation("location");
        event.setState("YN");
        event.setDate("11/11/2020");
        event.setTime("12:01:30");
        event.setCountry("US");
        event.setDelivery(delivery);

        List<EventParameter.MailItem.Event> events = new ArrayList<>();
        events.add(event);

        EventParameter.MailItem mailItem = new EventParameter.MailItem();
        mailItem.setTrackingNumber("420300419361269903505760403387");
        mailItem.setPickup(PICK_UP);
        mailItem.setEvents(events);

        EventParameter eventParameter = new EventParameter();
        eventParameter.mailitems = new ArrayList<>();
        eventParameter.mailitems.add(mailItem);
        EventResponse eventResponse = service.postEvent(eventParameter);
        System.out.println(eventResponse);
    }

    public void start() {
        getToken();
    }

    private static boolean isSuccessfully(int code) {
        return SUCCESS_CODE == code;
    }

    /**
     * 进行初始Token的获取和后期Token的刷新
     */
    private void getToken() {
        log.info(" getToken ... ");
        String url = "https://api.dhlglobalmail.com/v2/auth/access_token?username="
                + USERNAME + "&password=" + PASSWORD;

        HttpResponse response = HttpUtil.createGet(url).execute();
        int code = ERROR_CODE;
        String tokenStr = response.body();
        log.info(" tokenStr: " + tokenStr);
        Map<String, String> map = new HashMap<>();
        XmlUtils.xmlToMap(tokenStr, map);
        code = Integer.parseInt(map.get("Code"));//取消订单返回的状态
        String accessToken = map.get("AccessToken");//取消订单返回的状态
        long expiresIn = Long.parseLong(map.get("ExpiresIn"));//取消订单返回的状态

        if(isSuccessfully(code)) {
            mToken = accessToken;
            mExpireIn = expiresIn;

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    getToken();
                }
            }, mExpireIn * 1000);

        } else {
            log.error("  getToken error ...");
        }

        log.info(" code : " + code);
        log.info(" accessToken : " + accessToken);
        log.info(" expiresIn : " + expiresIn);
    }

    /**
     * 创建运单
     */
    public LabelResponse createLabel(LabelParameter labelParameter) {
        log.info(" createLabel mToken: " + mToken);
        String url = "https://api.dhlglobalmail.com/v2/label/multi/image.json?access_token=" + mToken + "&client_id=" + CLIENT_ID;

        log.info(" label: " + labelParameter);
        HttpResponse response = HttpUtil.createPost(url).body(JSON.toJSONString(labelParameter)).execute();
        log.info(" label: " + labelParameter + ", response: " + response);
        if(response == null) {
            log.error(" createLabel error ");
            return null;
        }

        String labelStr = response.body();
        log.info(" labelStr: " + labelStr);
        LabelResponse labelResponse = JSON.parseObject(labelStr, LabelResponse.class);

        if(SUCCESS_CODE == labelResponse.getMeta().getCode()) {
            log.info(" labelResponse: " + labelResponse);
            return labelResponse;
        } else {
            return null;
        }

    }

    /**
     * 关闭批次
     */
    public CloseoutResponse closeout(CloseoutParameter parameter,String pickUp) {
        String url = "https://api.dhlglobalmail.com/v2/locations/"+pickUp+"/closeout/multi.json?access_token=" + mToken + "&client_id=" + CLIENT_ID;

        log.info(" closeout parameter: " + parameter);
        HttpResponse response = HttpUtil.createPost(url).body(JSON.toJSONString(parameter)).execute();
        if(response == null) {
            log.error(" closeout error ");
            return null;
        }

        String closeoutStr = response.body();
        log.info(" closeoutStr: " + closeoutStr);
        CloseoutResponse closeoutResponse = JSON.parseObject(closeoutStr, CloseoutResponse.class);

        if(SUCCESS_CODE == closeoutResponse.getMeta().getCode()) {
            log.info(" closeoutResponse: " + closeoutResponse);
            return closeoutResponse;
        } else {
            return null;
        }

    }

    /**
     * 进行预上线
     */
    public EventResponse postEvent(EventParameter parameter) {
        String url = "https://api.dhlglobalmail.com/v2/mailitems/281/event.json?access_token=" + mToken + "&client_id=" + CLIENT_ID;

        log.info(" postEvent parameter: " + parameter);
        HttpResponse response = HttpUtil.createPost(url).body(JSON.toJSONString(parameter)).execute();
        if(response == null) {
            log.error(" postEvent error ");
            return null;
        }

        String eventStr = response.body();
        log.info(" eventStr: " + eventStr);
        EventResponse eventResponse = JSON.parseObject(eventStr, EventResponse.class);

        if(SUCCESS_CODE == eventResponse.getMeta().getCode()) {
            log.info(" eventResponse: " + eventResponse);
            return eventResponse;
        } else {
            return null;
        }

    }





    //使用方法




    //DHL创建运单
    //一、创建运单(使用中)
    //退货地址
    public LabelResponse createLabelFull(WayBillVo wayBillVo,int portId) {

        WayBill wayBill = wayBillVo.getWayBill();
        Receive receive = wayBillVo.getReceive();
        EntityWrapper<DhlPort> wrapper = new EntityWrapper<>();
        wrapper.eq("hgups_port_id",portId);
        DhlPort dhlPort = dhlPortService.selectOne(wrapper);

        LabelParameter.ShipmentsBean.PackagesBean.ReturnAddressBean returnAddressBean  = new LabelParameter.ShipmentsBean.PackagesBean.ReturnAddressBean();
        returnAddressBean.setAddress1(dhlPort.getContactAddressTwo());
        returnAddressBean.setCity(dhlPort.getContactCity());
        returnAddressBean.setState(dhlPort.getContactProvince());
        returnAddressBean.setName(dhlPort.getContactName());
        if (!StringUtils.isEmpty(dhlPort.getContactCompany())){
            returnAddressBean.setCompanyName(dhlPort.getContactCompany());
        }
        returnAddressBean.setPostalCode(dhlPort.getContactCode());
        returnAddressBean.setCountry("US");


        //包裹详情
        LabelParameter.ShipmentsBean.PackagesBean.PackageDetailsBean packageDetailsBean = new LabelParameter.ShipmentsBean.PackagesBean.PackageDetailsBean();
        packageDetailsBean.setBillingRef1("test bill ref1");
        packageDetailsBean.setBillingRef2("test bill ref2");
        packageDetailsBean.setCurrency("USD");
        boolean isP = true;
        double billWeight = wayBillVo.getParcel().getBillWeight();//打单重量
        if (billWeight<1) {
            isP = false;
        }
        packageDetailsBean.setMailFOrP(isP);
        packageDetailsBean.setPackageId(wayBill.getDhlPackageId()); //重点在意这个字段，用于内部排序 //HGUPS + 生成单号
        packageDetailsBean.setWeight(billWeight);
        packageDetailsBean.setWeightUom("LB");

        packageDetailsBean.setLength(1);
        packageDetailsBean.setWidth(1);
        packageDetailsBean.setHeight(1);

        //收货人信息
        LabelParameter.ShipmentsBean.PackagesBean.ConsigneeAddressBean consigneeAddressBean = new LabelParameter.ShipmentsBean.PackagesBean.ConsigneeAddressBean();
        consigneeAddressBean.setCountry(receive.getCountries());
        consigneeAddressBean.setEmail(receive.getEmail());
        consigneeAddressBean.setPhone(receive.getPhone());
        consigneeAddressBean.address1 = receive.getAddressTwo();
        if (!StringUtils.isEmpty(receive.getCompany())){
            consigneeAddressBean.setCompanyName(receive.getCompany());
        }
        consigneeAddressBean.setCity(receive.getCityEname());
        consigneeAddressBean.setName(receive.getName());
        consigneeAddressBean.setState(receive.getProvinceEname());
        consigneeAddressBean.setPostalCode(receive.getPostalCode());

        //包裹1
        LabelParameter.ShipmentsBean.PackagesBean packagesBean1 = new LabelParameter.ShipmentsBean.PackagesBean();
        packagesBean1.setReturnAddress(returnAddressBean);
        packagesBean1.setConsigneeAddress(consigneeAddressBean);
        packagesBean1.setPackageDetails(packageDetailsBean);

        List<LabelParameter.ShipmentsBean.PackagesBean> packagesBeanList = new ArrayList<>();
        packagesBeanList.add(packagesBean1);

        //运输1
        if (dhlPort!=null){
            PICK_UP=dhlPort.getDhlPickup();
            DISTRIBUTION_CENTER=dhlPort.getDistributionCenterCode();
            log.info("DHL-PICK_UP:"+PICK_UP);
            log.info("DHL-DISTRIBUTION_CENTER:"+DISTRIBUTION_CENTER);
        }
        LabelParameter.ShipmentsBean shipmentsBean1 = new LabelParameter.ShipmentsBean();
        shipmentsBean1.setPackages(packagesBeanList);
        shipmentsBean1.setPickup(PICK_UP);
        shipmentsBean1.setDistributionCenter(DISTRIBUTION_CENTER);

        //面单创建对象
        LabelParameter parameter = new LabelParameter();
        List<LabelParameter.ShipmentsBean> shipments = new ArrayList<>();
        shipments.add(shipmentsBean1);

        parameter.setShipments(shipments);

        /*try {
            Thread.sleep(10*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //开始创建运单
        log.info(" bingo param: " + parameter);
        return createLabel(parameter);
    }


    //DHL关闭批次(使用中)
    public CloseoutResponse closeoutFull(ShippingBatch batch) {
        CloseoutParameter closeoutParameter = new CloseoutParameter();

        List<CloseoutParameter.CloseoutRequestsBean> closeoutRequestsBeans = new ArrayList<>();
        CloseoutParameter.CloseoutRequestsBean closeoutRequestsBean1 = new CloseoutParameter.CloseoutRequestsBean();

        List<CloseoutParameter.CloseoutRequestsBean.PackagesBean> packages = new ArrayList<>();

        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.eq("shipping_batch_id",batch.getId());
        List<WayBill> wayBills = wayBillService.selectList(wrapper);
        for (WayBill wayBill : wayBills) {
            CloseoutParameter.CloseoutRequestsBean.PackagesBean cpackagesBean = new CloseoutParameter.CloseoutRequestsBean.PackagesBean();
            cpackagesBean.setPackageId(wayBill.getDhlPackageId());//HGUPS + 生成单号
            packages.add(cpackagesBean);
        }
        closeoutRequestsBean1.setPackages(packages);
        closeoutRequestsBeans.add(closeoutRequestsBean1);
        closeoutParameter.setCloseoutRequests(closeoutRequestsBeans);

        EntityWrapper<PortEntry> wrapper1 = new EntityWrapper<>();
        wrapper1.eq("title",batch.getEntrySite());
        PortEntry portEntry = portEntryService.selectOne(wrapper1);
        EntityWrapper<DhlPort> wrapper2 = new EntityWrapper<>();
        wrapper2.eq("hgups_port_id",portEntry.getId());
        DhlPort dhlPort = dhlPortService.selectOne(wrapper2);
        return closeout(closeoutParameter,dhlPort.getDhlPickup());
    }

    /**
     * 进行预上线(使用中)
     */
    public EventResponse postEventFull(String trackingNumber,String description ,String location,String province) {
        EventParameter.MailItem.Event.Delivery delivery =  new EventParameter.MailItem.Event.Delivery();
        EventParameter.MailItem.Event event = new EventParameter.MailItem.Event();
        event.setEventClass("PICKUP");
        event.setEventId("CE108110");
        event.setEventDescription(description);//描述
        event.setLocation(location);//位置
        event.setState(province);//省
        SimpleDateFormat dateFormat = new SimpleDateFormat(" yyyy-MM-dd ");
        String currentDate =   dateFormat.format( new Date() );
        event.setDate(currentDate);//当前时间日期
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        event.setTime(time.format(formatter));//当前时间点
        event.setCountry("US");
        event.setDelivery(delivery);

        List<EventParameter.MailItem.Event> events = new ArrayList<>();
        events.add(event);

        EventParameter.MailItem mailItem = new EventParameter.MailItem();
        mailItem.setTrackingNumber(trackingNumber);
        mailItem.setPickup(PICK_UP);
        mailItem.setEvents(events);

        EventParameter eventParameter = new EventParameter();
        eventParameter.mailitems = new ArrayList<>();
        eventParameter.mailitems.add(mailItem);

        return postEvent(eventParameter);

    }

    //生成随机订单号方法
    public static String getOrderNo() {
        long nanoTime = System.nanoTime();
        String date = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
        int randomInt = ThreadLocalRandom.current().nextInt(100);
        long l = nanoTime % 999;
        String s = Strings.padEnd(String.valueOf(l), 3, '0');
        String randomIntStr = Strings.padEnd(String.valueOf(randomInt), 3, '0');
        return date + s + randomIntStr;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }
}
