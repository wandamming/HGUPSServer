package com.hgups.express.util;

import com.hgups.express.domain.param.TrackDetailParam;
import com.hgups.express.domain.param.XmlParam;
import com.hgups.express.service.waybillmgi.WayBillService;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.*;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
public class XmlUtils {

    /**
     * XML格式字符串转换为Map
     *
     * @param xml XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */

    @Resource
    private WayBillService wayBillService;

    public static Map<String, String> xmlToMap(String xml) {
        try {
            Map<String, String> data = new HashMap<>();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            log.info(" xmlToMap.size: " + nodeList.getLength());
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            stream.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            log.info(" xmlToMap error: " + String.valueOf(e));
            return null;
        }
    }

    /**
     * XML格式字符串转换为Map
     *
     * @param xml XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static void xmlToMap(String xml, Map<String, String> dest) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            parseNode(nodeList, dest);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.info(" xmlToMap error: " + String.valueOf(e));
        }
    }

    public static void xmlToMapFromAddress(String xml, DiscoverAddressCallback callback) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            parseAddressNode(nodeList, callback);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.info(" xmlToMap error: " + String.valueOf(e));
        }
    }

    private static void parseAddressNode(NodeList nodeList, DiscoverAddressCallback callback) {
        for (int idx = 0; idx < nodeList.getLength(); ++idx) {
            Node node = nodeList.item(idx);
            if (node.getNodeType() == Node.ELEMENT_NODE && "Address".equals(node.getNodeName())) {
                Map<String, String> map = new HashMap<>();
                org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                map.put("index", element.getAttribute("ID"));
                if(node.hasChildNodes()) {
                    parseNode(node.getChildNodes(), map);
                }
                callback.onDiscover(map);
            }
        }
    }


    public static interface DiscoverAddressCallback {
        void onDiscover(Map<String, String> map);
    }

    private static void parseNode(NodeList nodeList, Map<String, String> dest) {
        for (int idx = 0; idx < nodeList.getLength(); ++idx) {
            Node node = nodeList.item(idx);
//            System.out.println("node.name: " + node.getNodeName()
//                    + ", node.value: " + node.getTextContent() + ", type: " + node.getNodeType() + ", hasChildren: " + node.hasChildNodes());
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                dest.put(node.getNodeName(), node.getTextContent());
                if(node.hasChildNodes()) {
                    parseNode(node.getChildNodes(), dest);
                }
            }
        }
    }

    public static String toXml(Map<String, String> params){
        StringBuilder buf = new StringBuilder();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        buf.append("<xml>");
        for(String key : keys){
            buf.append("<").append(key).append(">");
            buf.append("<![CDATA[").append(params.get(key)).append("]]>");
            buf.append("</").append(key).append(">\n");
        }
        buf.append("</xml>");
        return buf.toString();
    }

    //运单轨迹xml转对象
    public static List<XmlParam> parseXML(String xml) throws DocumentException {
        //String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><TrackResponse><TrackInfo ID=\"92055902769052000000000986\"><TrackSummary>Your item was delivered in or at the mailbox at 2:58 pm on July 14, 2020 in JERICHO, NY 11753.</TrackSummary><TrackDetail>Out for Delivery, 07/14/2020, 7:10 am, JERICHO, NY 11753</TrackDetail><TrackDetail>Arrived at Post Office, July 14, 2020, 5:05 am, HICKSVILLE, NY 11801</TrackDetail><TrackDetail>Accepted at USPS Facility, July 14, 2020, 4:05 am, HICKSVILLE, NY 11801</TrackDetail><TrackDetail>Picked Up by Shipping Partner, USPS Awaiting Item, July 8, 2020, 3:24 pm, OAKLAND, CA 94603</TrackDetail><TrackDetail>Shipping LabelParameter Created, USPS Awaiting Item, July 7, 2020, 11:06 pm, OAKLAND, CA 94603</TrackDetail></TrackInfo><TrackInfo ID=\"9200190276905200000389\"><TrackSummary>A shipping label has been prepared for your item at 8:50 am on July 23, 2020 in JERICHO, NY 11753. This does not indicate receipt by the USPS or the actual mailing date.</TrackSummary></TrackInfo></TrackResponse>";
        System.out.println("----------xml-------"+xml);
        Document document = null;
        document = DocumentHelper.parseText(xml);
        Element root = document.getRootElement();
        Iterator elementIterator = root.elementIterator();
        //遍历这个迭代器，，循环出根节点里第一层子节点
        List<XmlParam> xmlParamList = new ArrayList<>();
        while(elementIterator.hasNext()){
            //获取根节点的第一个子节点
            Element next =(Element) elementIterator.next();
            XmlParam xmlParam = new XmlParam();
            Attribute id = next.attribute("ID");
            xmlParam.setTrackingNumber(id.getText());
            //获取这个子节点的属性的值
            //System.out.println(next.attributeValue("id"));
            //重复上面的动作，第一个子节点的下子节点的迭代器
            Iterator elementIterator2 = next.elementIterator();
            List<TrackDetailParam> trackDetails = new ArrayList<>();
            while(elementIterator2.hasNext()){
                //获取来的子节点
                Element elem =(Element) elementIterator2.next();
                //打印出元素节点和文本节点
                if ("TrackSummary".equals(elem.getName())) {
                    xmlParam.setTrackSummary(elem.getText());
                    if (elem.getText().contains("delivered")){
                        xmlParam.setEnglishTitle("Delivered");
                        xmlParam.setWayBillState("已送达");
                    }
                    if (elem.getText().contains("prepared")||elem.getText().contains("picked")||elem.getText().contains("partner")){
                        xmlParam.setEnglishTitle("Pre-Shipment");
                        xmlParam.setWayBillState("派送中");
                    }
                    if (elem.getText().contains("could not locate")){
                        xmlParam.setEnglishTitle("LabelParameter Created, not yet in system");
                        xmlParam.setWayBillState("暂无物流信息");
                    }
                    TrackDetailParam trackDetailParam = new TrackDetailParam();
                    trackDetailParam.setReceivingState(elem.getText());
                    trackDetailParam.setArrivalTime("");
                    trackDetailParam.setArrivalAddress("");
                    trackDetails.add(trackDetailParam);
                }
                if ("TrackDetail".equals(elem.getName())) {
                    TrackDetailParam trackDetailParam = new TrackDetailParam();
                    String[] split = elem.getText().split(",");
                    if (!"已送达".equals(xmlParam.getWayBillState())){
                        xmlParam.setEnglishTitle("Transport");
                        xmlParam.setWayBillState("派送中");
                    }
                    if (split.length==5){
                        trackDetailParam.setReceivingState(split[0]);
                        trackDetailParam.setArrivalTime(split[1]+split[2]);
                        trackDetailParam.setArrivalAddress(split[3]+split[4]);
                    }
                    if (split.length==6){
                        trackDetailParam.setReceivingState(split[0]);
                        trackDetailParam.setArrivalTime(split[1]+split[2]+split[3]);
                        trackDetailParam.setArrivalAddress(split[4]+split[5]);
                    }
                    if (split.length==7){
                        trackDetailParam.setReceivingState(split[0]+split[1]);
                        trackDetailParam.setArrivalTime(split[2]+split[3]+split[4]);
                        trackDetailParam.setArrivalAddress(split[5]+split[6]);
                    }
                    trackDetails.add(trackDetailParam);
                }
                Iterator elementIterator3 = elem.elementIterator();
                while (elementIterator3.hasNext()){
                    Element error =(Element) elementIterator3.next();
                    if ("Description".equals(error.getName())) {
                        xmlParam.setDescription(error.getText());
                        xmlParam.setEnglishTitle("LabelParameter Created, not yet in system");
                        xmlParam.setWayBillState("暂无物流信息");
                    }
                }
                // System.out.println(next2.getName()+"= "+next2.getText());
            }
            xmlParam.setTrackDetails(trackDetails);
            xmlParamList.add(xmlParam);
        }

        return xmlParamList;
    }


   /* public static void parseXML() {
        try {
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><TrackResponse><TrackInfo ID=\"92055902769052000000000986\"><TrackSummary>Your item was delivered in or at the mailbox at 2:58 pm on July 14, 2020 in JERICHO, NY 11753.</TrackSummary><TrackDetail>Out for Delivery, 07/14/2020, 7:10 am, JERICHO, NY 11753</TrackDetail><TrackDetail>Arrived at Post Office, July 14, 2020, 5:05 am, HICKSVILLE, NY 11801</TrackDetail><TrackDetail>Accepted at USPS Facility, July 14, 2020, 4:05 am, HICKSVILLE, NY 11801</TrackDetail><TrackDetail>Picked Up by Shipping Partner, USPS Awaiting Item, July 8, 2020, 3:24 pm, OAKLAND, CA 94603</TrackDetail><TrackDetail>Shipping LabelParameter Created, USPS Awaiting Item, July 7, 2020, 11:06 pm, OAKLAND, CA 94603</TrackDetail></TrackInfo><TrackInfo ID=\"9200190276905200000389\"><TrackSummary>A shipping label has been prepared for your item at 8:50 am on July 23, 2020 in JERICHO, NY 11753. This does not indicate receipt by the USPS or the actual mailing date.</TrackSummary></TrackInfo></TrackResponse>";
            Document document = null;
            document = DocumentHelper.parseText(xml);
            Element root = document.getRootElement();
            Iterator elementIterator = root.elementIterator();
            //遍历这个迭代器，，循环出根节点里第一层子节点
            while (elementIterator.hasNext()) {
                //获取根节点的第一个子节点
                Element next = (Element) elementIterator.next();
                System.out.println("1级标签--key:  "+next.getName()+"----value:   "+next.getText());
                //获取这个子节点的属性的值
                //System.out.println(next.attributeValue("id"));
                //重复上面的动作，第一个子节点的下子节点的迭代器
                Iterator elementIterator2 = next.elementIterator();
                while (elementIterator2.hasNext()) {
                    //获取来的子节点
                    Element elem = (Element) elementIterator2.next();
                    //打印出元素节点和文本节点
                    System.out.println("2级标签---key:  "+elem.getName()+"-----value:   "+elem.getText());
                    Iterator elementIterator3 = elem.elementIterator();
                    while (elementIterator3.hasNext()) {
                        Element error = (Element) elementIterator3.next();
                        System.out.println("3级标签---key  "+error.getName()+"----value:   "+error.getText());
                    }

                }
                System.out.println("========================");
            }
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }*/
}

