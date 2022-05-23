package com.hgups.express.controller.waybillmg;

import cn.hutool.http.HttpUtil;
import com.hgups.express.domain.Goods;
import com.hgups.express.domain.Response;
import com.hgups.express.service.GoodsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.io.FileNotFoundException;

@Api(description = "测试API")
@Slf4j
@RestController
@RequestMapping("/hello")
public class HelloController {

    @Resource
    private GoodsService goodsService;

    @GetMapping("/usps")
    public Response<String> testUSPS() {
        log.info(" usps 1 ");
        String result = Talk.testCreateBill();
        log.info(" usps 2 : " + result);
        return new Response<>(result);
    }

    @GetMapping("/selectForUpdate")
    public Response<String> selectForUpdate(@RequestParam Integer id) throws FileNotFoundException {
        log.info(" testSelectForUpdate id: " + id);
        goodsService.updateTotal(id);
        return new Response<>("成功");
    }

    @GetMapping("/testSelectForUpdate")
    public Response<String> testSelectForUpdate(@RequestParam Integer number, @RequestParam Integer id) {
        log.info(" testSelectForUpdate number: " + number + ", id: " + id);

        for(int i=1; i<= number;i++) {

            Thread thread = new Thread(() -> HttpUtil.get("http://localhost:8701/hgups/hello/selectForUpdate?id=" + id));
            thread.start();
        }


        return new Response<>("成功");
    }

}
