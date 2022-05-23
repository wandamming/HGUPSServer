package com.hgups.express.business.ec.pay.fb;

import cn.hutool.http.HttpUtil;
import com.google.gson.Gson;
import com.hgups.express.business.ec.pay.fb.entity.CodeParameter;
import com.hgups.express.business.ec.pay.fb.entity.PayParameter;
import com.hgups.express.business.ec.pay.utils.MD5;
import com.hgups.express.business.ec.pay.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PayService {
    private Gson gson = new Gson();

    public void pay(String code) {

        String url = "https://shq-api.51fubei.com/gateway/agent";
        String appSecret = "eb5f7998b05a5d9083e5c5e577a20db6";

        CodeParameter parameter = new CodeParameter();
        parameter.setAuth_code(code);
        parameter.setTotal_amount(0.01d);
        parameter.setStore_id(1140897);
        long now = System.currentTimeMillis();
        parameter.setMerchant_order_sn(String.valueOf(now));

        PayParameter payParameter = new PayParameter();
        payParameter.setMethod("fbpay.order.pay");
        payParameter.setApp_id("20210703212733470226");
        payParameter.setFormat("json");
        payParameter.setSign_method("md5");
        payParameter.setNonce(String.valueOf(now));
        String busStr = gson.toJson(parameter);
        payParameter.setBiz_content(busStr);

        Map<String, String> paramsMap = new HashMap<>();
//        paramsMap.put("store_id", String.valueOf(1140897));
//        paramsMap.put("auth_code", code);
//        paramsMap.put("total_amount", "0.01");
//        paramsMap.put("merchant_order_sn", String.valueOf(now));

        paramsMap.put("method", "fbpay.order.pay");
        paramsMap.put("app_id", "20210703212733470226");
        paramsMap.put("format", "json");
        paramsMap.put("sign_method", "md5");
        paramsMap.put("nonce", String.valueOf(now));
        paramsMap.put("biz_content", busStr);

        StringBuilder buf = new StringBuilder((paramsMap.size() + 1) * 10);
        SignUtils.buildPayParams(buf, paramsMap, false);
        String preStr = buf.toString();
        String sign = MD5.sign(preStr, appSecret, "utf-8");
        payParameter.setSign(sign);

        String dsd = HttpUtil.post(url, gson.toJson(payParameter));
        log.info("bingo dsd: " + dsd);
    }


}
