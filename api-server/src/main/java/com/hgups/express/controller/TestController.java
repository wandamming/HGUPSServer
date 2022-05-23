package com.hgups.express.controller;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.hgups.express.business.ec.pay.fb.PayService;
import com.hgups.express.business.ec.pay.utils.MD5;
import com.hgups.express.business.ec.pay.utils.SignUtils;
import com.hgups.express.business.ec.shopify.entity.Order;
import com.hgups.express.domain.Response;
import com.hgups.express.util.XmlUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Api(description = "临时测试接口")
@Slf4j
@RestController
@RequestMapping("/test")
@Configuration
public class TestController {

    @Autowired
    private PayService fbPayService;

    @GetMapping("/test")
    public Response test(@RequestParam String token, @RequestParam int count) {
        log.info(" test token: " + token + ", count: " + count);

        for(int i = 1; i <= count; i++) {

            Thread thread = new Thread(() -> {
                String body = "{\"sender\":{\"id\":1228,\"name\":\"Joan Chen\",\"company\":\"FMUSS INC\",\"countries\":\"美国\",\"provinceEname\":\"CA\",\"provinceCname\":\"CALIFORNIA 加利福尼亚\",\"cityEname\":\"OAKLAND\",\"cityCname\":\"OAKLAND\",\"postalCode\":\"94603\",\"addressTwo\":\"10918 Edes Ave\",\"phone\":\"8177770079\",\"phonePrefix\":\"1\",\"email\":\"1djfdf@qq.com\",\"userId\":29,\"isSave\":\"1\",\"postalCodet\":\"\",\"senderCarrierRoute\":\"C011\",\"senderDeliveryPoint\":\"18\"},\"receive\":{\"id\":1748,\"name\":\"Kevin Wu\",\"company\":\"232\",\"countries\":\"美国\",\"provinceEname\":\"NY\",\"provinceCname\":\"NEW YORK 纽约\",\"cityEname\":\"JERICHO\",\"cityCname\":\"JERICHO\",\"prefecture\":\"null\",\"postalCode\":\"11753\",\"addressTwo\":\"54 Hightop LN\",\"phone\":\"8103777777\",\"phonePrefix\":\"1\",\"email\":\"1djfdf@qq.com\",\"userId\":29,\"isSave\":\"1\",\"postalCodet\":\"\",\"receiveCarrierRoute\":\"C012\",\"receiveDeliveryPoint\":\"54\"},\"parcel\":{\"billWeight\":\"0.01\",\"lengths\":\"1\",\"width\":\"1\",\"height\":\"1\",\"parcelShape\":\"RECTANGULAR\",\"itmeCategory\":\"电池\",\"aritcleDescribe\":\"232\",\"commentOne\":\"1212\",\"commentTwo\":\"21\",\"isCoubid\":false,\"isSoft\":false},\"articleList\":[{\"cdescribe\":\"121\",\"edescribe\":\"21\",\"price\":\"2\",\"weight\":\"12\",\"number\":\"2\",\"place\":\"12\"}],\"wayBill\":{\"exportCity\":\"上海\",\"channel\":\"HGUPS\"},\"checkAddress\":true}";
                HttpResponse dsd = HttpUtil
                        .createPost("https://www.hgups.com.cn/hgups/wayBillVo/createWayBillVo")
//                        .createPost("https://www.onezerobeat.com/hgups/wayBillVo/createWayBillVo")
                        .header("access-token", token)
                        .body(body)
                        .execute();
                log.info(" msg: " + String.valueOf(dsd));
            });

            thread.start();
        }


        return new Response();
    }



    private static Map<String, String> cfg = new HashMap<String, String>();

    private static void init() {
        cfg.put("mch_id", "800391000019504");
        cfg.put("attach", "3c4aaaed9d9811eb99db7cd30ad38560");
        cfg.put("device_info", "XC108734");
        cfg.put("orgNo", "13421");
        cfg.put("openid", "oTpYmw-J6QoV0Qn177BtuZHBjb8I");
        cfg.put("key", "dfkeef1q9qlnd63q6xxm2ctdx85h9gtr");
    }

    @GetMapping("/alipay")
    private static void alipay() throws Exception {
        init();
        String url = "https://mp.changepay.cn/wxpay/unifiedorder";
        String key = cfg.get("key");
        Map<String, String> paramsMap = new HashMap<String, String>();
        String timeStamp = new Date().getTime() + "";

        paramsMap.put("attach", cfg.get("attach"));//商户号
        paramsMap.put("mch_id", cfg.get("mch_id"));//终端号 平台分配
        paramsMap.put("device_info", cfg.get("device_info"));//机构号 平台分配
        paramsMap.put("orgNo", cfg.get("orgNo"));//机构号 平台分配
        paramsMap.put("nonce_str", timeStamp);//随机数
        paramsMap.put("notify_url", "http://www.baidu.com/");//订单编号
        paramsMap.put("out_trade_no", "a"+timeStamp);//订单编号
        paramsMap.put("spbill_create_ip", "127.0.0.1");//ip地址
        paramsMap.put("total_fee", "1");//金额，单位为分
        paramsMap.put("fee_type", "CNY");//必填
        paramsMap.put("payChannel", "ALIPAY");//支付宝ALIPAY 微信已关闭
        paramsMap.put("trade_type", "NATIVE");//必填



        StringBuilder buf = new StringBuilder((paramsMap.size() + 1) * 10);
        SignUtils.buildPayParams(buf, paramsMap, false);
        String preStr = buf.toString();
        String sign = MD5.sign(preStr, "&key=" + key, "utf-8");

        paramsMap.put("sign", sign);

        String data=XmlUtils.toXml(paramsMap);
        System.out.println("请求地址：" + url);

        System.out.println("请求数据：\n" +data);

        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        String res = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity entityParams = new StringEntity(data,"utf-8");
            httpPost.setEntity(entityParams);
            httpPost.setHeader("Content-Type", "text/xml");
            httpPost.setHeader("charset", "utf-8");
            client = HttpClients.createDefault();
            response = client.execute(httpPost);

            System.out.println("返回数据\n" + EntityUtils.toString(response.getEntity() , Consts.UTF_8));


        }catch (Exception e) {
            res = "操作失败";
        } finally {
            if (response != null) {
                response.close();
            }
            if (client != null) {
                client.close();
            }
        }

    }
    @GetMapping("/fbPay")
    public void fbPay(@RequestParam String scanCode)  {
        fbPayService.pay(scanCode);
    }


    @GetMapping("/scanPay")
    public void micropay(@RequestParam String scanCode) throws Exception {
        init();
        String url = "https://mp.changepay.cn/wxpay/micropay";
        String key = cfg.get("key");
        Map<String, String> paramsMap = new HashMap<>();
        String timeStamp = new Date().getTime() + "";

        paramsMap.put("appid", "");//无公众号留空,必填
        paramsMap.put("attach", cfg.get("attach"));//商户号
        paramsMap.put("mch_id", cfg.get("mch_id"));//终端号 平台分配
        paramsMap.put("device_info", cfg.get("device_info"));//机构号 平台分配
        paramsMap.put("orgNo", cfg.get("orgNo"));//机构号 平台分配
        paramsMap.put("body", "wxzf");//这是商品的说明，会显示在付款方的账单详情中

        paramsMap.put("nonce_str", timeStamp);//随机数
        paramsMap.put("out_trade_no", "a"+timeStamp);//订单编号
        paramsMap.put("spbill_create_ip", "127.0.0.1");//ip地址
        paramsMap.put("total_fee", "1");//金额，单位为分
        paramsMap.put("fee_type", "CNY");//必填
//        paramsMap.put("payChannel", "ALIPAY");//支付宝ALIPAY 微信WXPAY
        paramsMap.put("payChannel", "WXPAY");//支付宝ALIPAY 微信WXPAY
        paramsMap.put("auth_code", scanCode);//必填



        StringBuilder buf = new StringBuilder((paramsMap.size() + 1) * 10);
        SignUtils.buildPayParams(buf, paramsMap, false);
        String preStr = buf.toString();
        String sign = MD5.sign(preStr, "&key=" + key, "utf-8");

        paramsMap.put("sign", sign);

        String data= XmlUtils.toXml(paramsMap);
        System.out.println("请求地址：" + url);

        System.out.println("请求数据：\n" +data);

        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        String res = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity entityParams = new StringEntity(data,"utf-8");
            httpPost.setEntity(entityParams);
            httpPost.setHeader("Content-Type", "text/xml");
            httpPost.setHeader("charset", "utf-8");
            client = HttpClients.createDefault();
            response = client.execute(httpPost);

            System.out.println("返回数据\n" + EntityUtils.toString(response.getEntity() , Consts.UTF_8));


        }catch (Exception e) {
            res = "操作失败";
        } finally {
            if (response != null) {
                response.close();
            }
            if (client != null) {
                client.close();
            }
        }

    }

}
