package com.hgups.express.controller.waybillmg;

import com.hgups.express.util.XmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Talk {
    public static void testXmlToBean() {
        Map<String, String> ds = XmlUtils.xmlToMap("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<AddressValidateResponse>\n" +
                "    <Address ID=\"0\">\n" +
                "        <Address2>54 HIGHTOP LN # LN111</Address2>\n" +
                "        <City>JERICHO</City>\n" +
                "        <State>NY</State>\n" +
                "        <Zip5>11753</Zip5>\n" +
                "        <Zip4>1719</Zip4>\n" +
                "        <DeliveryPoint>54</DeliveryPoint>\n" +
                "        <CarrierRoute>C012</CarrierRoute>\n" +
                "        <Footnotes>L</Footnotes>\n" +
                "        <DPVConfirmation>S</DPVConfirmation>\n" +
                "        <DPVCMRA>N</DPVCMRA>\n" +
                "        <DPVFootnotes>AACC</DPVFootnotes>\n" +
                "        <Business>N</Business>\n" +
                "        <CentralDeliveryPoint>N</CentralDeliveryPoint>\n" +
                "        <Vacant>N</Vacant>\n" +
                "    </Address>\n" +
                "</AddressValidateResponse>");

        System.out.println("dsd.size: " + ds.size());
        for(String key : ds.keySet()) {
            System.out.println(" key: " + key + ", value: " + ds.get(key));
        }
    }


    public static String testCreateBill() {
        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        String strUrl = "https://stg-secure.shippingapis.com/ShippingAPI.dll?API=eVS&XML=<eVSRequest USERID=\"707HGUPS0501\"><Option></Option><Revision></Revision><ImageParameters><ImageParameter>4x6LABELP</ImageParameter><XCoordinate>0</XCoordinate><YCoordinate>900</YCoordinate></ImageParameters><FromName>US POSTAL HEADQUARTERS</FromName><FromFirm></FromFirm><FromAddress1>RM 1P010</FromAddress1><FromAddress2>475 LENFANT PLZ SW</FromAddress2><FromCity>Washington</FromCity><FromState>DC</FromState><FromZip5>20260</FromZip5><FromZip4></FromZip4><FromPhone></FromPhone><AllowNonCleansedOriginAddr>FALSE</AllowNonCleansedOriginAddr><ToName>Customer</ToName><ToFirm></ToFirm><ToAddress1></ToAddress1><ToAddress2>325 N Maple Dr</ToAddress2><ToCity>Beverly Hills</ToCity><ToState>CA</ToState><ToZip5>90210</ToZip5><ToZip4></ToZip4><ToPhone></ToPhone><AllowNonCleansedDestAddr>False</AllowNonCleansedDestAddr><WeightInOunces>12.0000</WeightInOunces><ServiceType>FIRST CLASS</ServiceType><Container>PACKAGE SERVICE</Container><Width></Width><Length></Length><Height></Height><Machinable></Machinable><CustomerRefNo>FC</CustomerRefNo><ExtraServices><ExtraService>155</ExtraService></ExtraServices><ReceiptOption>None</ReceiptOption><ImageType>PDF</ImageType><PrintCustomerRefNo>True</PrintCustomerRefNo></eVSRequest>";
        //String strUrl = "https://www.baidu.com";
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
            httpGet.setHeader("Content-Type", "text/xml");
            httpGet.setHeader("charset", "utf-8");
            client = HttpClients.createDefault();
            response = client.execute(httpGet);
            String res = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            log.info("返回数据\n{}", res);
            System.out.println("返回数据\n{}"+ res);
            //            System.out.println(responseMap);
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

    //测试是否有效
    public static void main(String[] args) throws IOException {
        /*System.out.println("===========-==-===");
        testCreateBill();
        testXmlToBean();*/
//        testXmlToBean();
        Map<String, String> nodes = new HashMap<>();

        XmlUtils.xmlToMap("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<AddressValidateResponse>\n" +
                "    <Address ID=\"0\">\n" +
                "        <Address2>54 HIGHTOP LN # LN111</Address2>\n" +
                "        <City>JERICHO</City>\n" +
                "        <State>NY</State>\n" +
                "        <Zip5>11753</Zip5>\n" +
                "        <Zip4>1719</Zip4>\n" +
                "        <DeliveryPoint>54</DeliveryPoint>\n" +
                "        <CarrierRoute>C012</CarrierRoute>\n" +
                "        <Footnotes>L</Footnotes>\n" +
                "        <DPVConfirmation>S</DPVConfirmation>\n" +
                "        <DPVCMRA>N</DPVCMRA>\n" +
                "        <DPVFootnotes>AACC</DPVFootnotes>\n" +
                "        <Business>N</Business>\n" +
                "        <CentralDeliveryPoint>N</CentralDeliveryPoint>\n" +
                "        <Vacant>N</Vacant>\n" +
                "    </Address>\n" +
                "</AddressValidateResponse>", nodes);
        for(String key : nodes.keySet()) {
            System.out.println(" key: " + key + ", value: " + nodes.get(key));
        }
    }
}