package com.hgups.express.controller;

import com.hgups.express.domain.Response;
import com.hgups.express.domain.dto.KeyValue;
import com.hgups.express.domain.param.ConsultionParam;
import com.hgups.express.domain.param.PriceParam;
import com.hgups.express.service.usermgi.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;


@Api(description = "官网")
@RestController
@Slf4j
@RequestMapping("/website")
public class WebsiteController {

    @Value(value = "${website.consulting.receiver.mail}")
    private String receiverMail;

    @Autowired
    private UserService userService;

    public static Map<Integer, String> sPriceMap = new HashMap<>();
    static {
        sPriceMap.put(0, "全程");
        sPriceMap.put(1, "尾程");
        sPriceMap.put(2, "海外仓");
    }

    @ApiOperation(value = "获取报价")
    @PostMapping("/getPrice")
    public Response getPrice(@RequestBody PriceParam param) {
        log.info(" getPrice: " + param);
        String content = "公司名称:" + param.company
                + "\n联系人: " + param.contact
                + "\n联系电话: " + param.telephone
                + "\n邮箱: " + param.email
                + "\n打单类型: " + sPriceMap.get(param.type)
                + "\n咨询: " + param.remark;
        userService.sendSimpleMail(receiverMail, "HGUPS官网-获取报价", content);
        return new Response();
    }

    @ApiOperation(value = "咨询")
    @PostMapping("/consulting")
    public Response consulting(@RequestBody ConsultionParam param ) {
        log.info(" consulting: " + param);
        String content = "公司名称:" + param.company
                + "\n行业: " + param.industry
                + "\n规模: " + param.scale
                + "\n联系人: " + param.name
                + "\n联系电话: " + param.telephone
                + "\n邮箱: " + param.email
                + "\n咨询业务: " + param.content;
        userService.sendSimpleMail(receiverMail, "HGUPS官网-咨询", content);
        return new Response();
    }

    @ApiOperation(value = "获取报价的类型")
    @GetMapping("/getPriceTypeList")
    public Response getPriceTypeList() {
        Response response = new Response();

        List<KeyValue<String, Integer>> types = new ArrayList<>();
        for(Map.Entry<Integer, String> entity: sPriceMap.entrySet()) {
            KeyValue<String, Integer> kv1 = new KeyValue<>(entity.getValue(), entity.getKey());
            types.add(kv1);
        }

        response.setData(types);
        return response;
    }

}
