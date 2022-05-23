package com.hgups.express.business.ec.shopify;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.hgups.express.business.ec.shopify.entity.Order;
import com.hgups.express.domain.Response;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Api(description = "Shopify电商")
@Slf4j
@RestController
@RequestMapping("/Shopify")
@Configuration
public class Shopify {


    @GetMapping("/getCode")
    public Response getShopify(@RequestParam String code, @RequestParam String hmac, @RequestParam String host,
                               @RequestParam String timestamp, @RequestParam String state, @RequestParam String shop) {

        log.info(" code: " + code);
        log.info(" hmac: " + hmac);
        log.info(" host: " + host);
        log.info(" timestamp: " + timestamp);
        log.info(" state: " + state);
        log.info(" shop: " + shop);

        return new Response();
    }

    @GetMapping("/getAccessToken")
    public Response getAccessToken() {
        String url = "https://maimaitongerp.myshopify.com/admin/oauth/access_token?client_id=1abe4f2f3dadb68259c80e14bc10c5f0&client_secret=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&code=b306d1551ad989e4c37b4e6056e9beed" ;

        HttpResponse response = HttpUtil.createPost(url).execute();
        log.info(" getAccessToken content: " + response.body().toString());

        return new Response(response.body().toString());
    }

    @GetMapping("/getOrder")
    public Response getOrder(@RequestParam String token) {
        String url = "https://maimaitongerp.myshopify.com/admin/api/2021-04/orders.json?status=any";

        HttpResponse response = HttpUtil.createGet(url).header("X-Shopify-Access-Token", token).execute();
        log.info(" getOrder content: " + response.body().toString());

        return new Response(response.body().toString());
    }

    @GetMapping("/addOrder")
    public Response addOrder(@RequestParam String token) {
        String url = "https://maimaitongerp.myshopify.com/admin/api/2021-04/orders.json";
        Order order = new Order();
        HttpResponse response = HttpUtil.createPost(url).header("X-Shopify-Access-Token", token).body(JSONUtil.parse(order)).execute();
        log.info(" addOrder content: " + response.body().toString());

        return new Response(response.body().toString());
    }


}
