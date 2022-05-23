package com.hgups.express.business.ec.pay.cq;

import com.hgups.express.business.ec.pay.utils.MD5;
import com.hgups.express.business.ec.pay.utils.SignUtils;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(description = "支付")
@Slf4j
@RestController
@RequestMapping("/pay")
@Configuration
public class PayAction {

    private static Map<String, String> cfg = new HashMap<String, String>();

    private static void init() {
        cfg.put("mch_id", "800391000019504");
        cfg.put("attach", "3c4aaaed9d9811eb99db7cd30ad38560");
        cfg.put("device_info", "XC108734");
        cfg.put("orgNo", "13421");
        cfg.put("openid", "oTpYmw-J6QoV0Qn177BtuZHBjb8I");
        cfg.put("key", "dfkeef1q9qlnd63q6xxm2ctdx85h9gtr");
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
        paramsMap.put("body", "wxzf");//默认

        paramsMap.put("nonce_str", timeStamp);//随机数
        paramsMap.put("out_trade_no", "a"+timeStamp);//订单编号
        paramsMap.put("spbill_create_ip", "127.0.0.1");//ip地址
        paramsMap.put("total_fee", "1");//金额，单位为分
        paramsMap.put("fee_type", "CNY");//必填
        paramsMap.put("payChannel", "ALIPAY");//支付宝ALIPAY 微信WXPAY
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
