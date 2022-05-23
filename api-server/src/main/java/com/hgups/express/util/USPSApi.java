package com.hgups.express.util;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.hgups.express.exception.UspsApiException;
import com.jpay.util.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class USPSApi {
    private static final String HOST = "https://secure.shippingapis.com/ShippingAPI.dll";
    private static final String VALIDATE_ADDRESS_URL = "";

    public static void main(String[] args) {
        Address address = new Address();
        address.index = 0;
        address.state = "NY";
        address.city = "Jericho";
        address.address2 = "53 Hightop LN11";
        address.address1 = "";
        address.zipCode5 = "11753";

        Address address1 = new Address();
        address1.index = 1;
        address1.state = "WA";
        address1.city = "12";
        address1.address2 = "1516 CRESO RD";
        address1.address1 = "";
        address1.zipCode5 = "9833387";
        address1.zipCode4 = "8995";

        Address address2 = new Address();
        address2.index = 2;
        address2.state = "CA";
        address2.city = "GREENBRAE";
        address2.address2 = "65 LOWER VIA CASITAS";
        address2.address1 = "";
        address2.zipCode5 = "94904";
        address2.zipCode4 = "2278";

        Address address3 = new Address();
        address3.index = 3;
        address3.state = "WI";
        address3.city = "HARTLAND";
        address3.address2 = "1221 FOUR WINDS WAY";
        address3.address1 = "";
        address3.zipCode5 = "53029";
        address3.zipCode4 = "8561";

        Address address4 = new Address();
        address4.index = 4;
        address4.state = "ND";
        address4.city = "MANDAN";
        address4.address2 = "900 16TH ST SE";
        address4.address1 = "";
        address4.zipCode5 = "58554";
        address4.zipCode4 = "4815";

        List<Address> srcs = new ArrayList<>();
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
/*
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);

        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);

        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);

        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);
        srcs.add(address);
        srcs.add(address1);
        srcs.add(address2);
        srcs.add(address3);
        srcs.add(address4);*/
        //批量打印
        batchValidateAddress(srcs);
    }

    public static List<Address> batchValidateAddress(List<Address> srcs) {
        List<Address> totalAddress = new ArrayList<>();
        int total = srcs.size();
        int count = total / 5;
        boolean complete = true;
        if(total % 5 != 0) {
            complete = false;
            count++;
        }

        final CountDownLatch latch = new CountDownLatch(count);
        log.info(" bingo count: " + count);
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            //检查最后一个
            List subs = srcs.subList(i * 5, (i == count -1) && !complete ? total : i * 5 + 5);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    List newAddress = validateAddress(subs);
                    totalAddress.addAll(newAddress);
                    latch.countDown();
                }
            });
            thread.start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error(" countdown lunad");
        }
        long end = System.currentTimeMillis();
        log.info(" batch valid address time: " + (end - start) + ", total address size: " + totalAddress.size());

        return totalAddress;
    }

    public static List<Address> validateAddress(List<Address> srcs) {
        log.info(" sub list: " + srcs);
        String xml =
                "<AddressValidateRequest USERID=\"707HGUPS0501\">" +
                        "<Revision>1</Revision>";

        for (int i = 0; i < srcs.size(); i++) {
            Address src = srcs.get(i);
            xml += "<Address ID=\"" + src.index + "\"><Address1>" + src.address1 + "</Address1>" +
                    "<Address2>" + src.address2 + "</Address2>" +
                    "<City>" + src.city + "</City>" +
                    "<State>" + src.state + "</State>" +
                    "<Zip5>" + src.zipCode5 + "</Zip5>" +
                    "<Zip4>" + src.zipCode4 + "</Zip4>" +
                    "</Address>";
        }
        xml += "</AddressValidateRequest>";

        HttpResponse response = null;
        try {
            response = HttpUtil.createGet(buildURL("Verify", URLEncoder.encode(xml, "UTF-8"))).setConnectionTimeout(120000).execute();
        } catch (UnsupportedEncodingException e) {
            System.out.println("----usps异常---" + e);
            e.printStackTrace();
            log.warn(" validateAddress response error: " + String.valueOf(e));
        }catch (Exception e){
            System.out.println("----usps异常---" + e);
            log.error("调用usps请求异常---->>>"+e.toString());
            throw new UspsApiException();
        }
        List<Address> totalList = new ArrayList<>();
        if (response != null &&
                response.isOk() &&
                !StringUtils.isEmpty(response.body())) {

            String body = response.body();
            XmlUtils.xmlToMapFromAddress(body, new XmlUtils.DiscoverAddressCallback() {
                @Override
                public void onDiscover(Map<String, String> map) {
                    Address address = new Address();
                    address.isValid = !map.containsKey("Error");
                    if (address.isValid) {
                        address.index = Integer.parseInt(map.get("index"));
                        address.address2 = getValidValue(map, "Address2");
                        address.address1 = map.get("Address1");
                        address.city = map.get("City");
                        address.state = map.get("State");
                        address.zipCode5 = map.get("Zip5");
                        address.zipCode4 = map.get("Zip4");
                        address.deliveryPoint = map.get("DeliveryPoint");
                        address.carrierRoute = map.get("CarrierRoute");
                    }
                    totalList.add(address);
                }
            });
        } else {
            for (Address src : srcs) {
                totalList.add(new Address(src.index));
            }
        }
        log.info(" validateAddress totalList.size: " + totalList.size() + ", totalList: " + totalList);
        return totalList;
    }

    public static Address validateAddress(Address src) throws UspsApiException {
        log.info(" validateAddress src: " + src);
        String xml =
                "<AddressValidateRequest USERID=\"707HGUPS0501\">" +
                        "<Revision>1</Revision>" +
                        "<Address ID=\"0\"><Address1>" + src.address1 + "</Address1>" +
                        "<Address2>" + src.address2 + "</Address2>" +
                        "<City>" + src.city + "</City>" +
                        "<State>" + src.state + "</State>" +
                        "<Zip5>" + src.zipCode5 + "</Zip5>" +
                        "<Zip4>" + src.zipCode4 + "</Zip4>" +
                        "</Address>" +

                        "</AddressValidateRequest>";
        HttpResponse response = null;
        try {
            response = HttpUtil.createGet(buildURL("Verify", URLEncoder.encode(xml, "UTF-8"))).execute();
//            log.info(" validateAddress response: " +response);
            System.out.println("usps请求返回体------》》》" + response);
        } catch (UnsupportedEncodingException e) {
            System.out.println("----usps异常---" + e);
            e.printStackTrace();
            log.warn(" validateAddress response error: " + String.valueOf(e));
        }catch (HttpException e){
            e.printStackTrace();
            log.error("连接超时: " + String.valueOf(e));
        } catch (Exception e){
            e.printStackTrace();
            log.error("USPSAPIError: " + String.valueOf(e));
            throw new UspsApiException();
        }
        Address address = new Address();
        if (response != null &&
                response.isOk() &&
                !StringUtils.isEmpty(response.body())) {

            String body = response.body();
            Map<String, String> map = new HashMap<>();
            XmlUtils.xmlToMap(body, map);
            log.info(" 请求成功: " + body + ", map.size: " + map.size());
            address.isValid = !map.containsKey("Error");
            if (address.isValid) {
                address.address2 = getValidValue(map, "Address2");
                address.address1 = map.get("Address1");
                address.city = map.get("City");
                address.state = map.get("State");
                address.zipCode5 = map.get("Zip5");
                address.zipCode4 = map.get("Zip4");
                address.deliveryPoint = map.get("DeliveryPoint");
                address.carrierRoute = map.get("CarrierRoute");
            }
        }

        log.info(" validateAddress address: " + address);
        return address;
    }

    private static String getValidValue(Map map, String key) {
        return map.containsKey(key) ? String.valueOf(map.get(key)) : "";
    }


    @Data
    public static class Address {
        public int index;
        public String address1 = "";
        public String address2 = "";
        public String city = "";
        public String state = "";
        public String zipCode5 = "";
        public String zipCode4 = "";
        public String deliveryPoint = "";
        public String carrierRoute = "";
        public String dPVConfirmation = "";
        public String dPVCMRA = "";
        public String dPVFootnotes = "";
        public String business = "";
        public String CentralDeliveryPoint = "";
        public String vacant = "";
        public boolean isValid = false;

        public Address(int index) {
            this.index = index;
        }

        public Address() {
        }
    }

    public static String buildURL(String api, String xml) {
        return HOST + "?API=" + api + "&XML=" + xml;
    }

}
