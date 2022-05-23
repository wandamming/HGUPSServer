package com.hgups.express.controller;

import com.hgups.express.domain.Response;
import com.hgups.express.exception.NoAuthException;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping(value = "")
@Slf4j
@Api("没有权限时的跳转接口")
public class NoAuthController {

    @GetMapping(value = "/noauth")
    public Response noauth() {
        throw new NoAuthException();
    }
}
