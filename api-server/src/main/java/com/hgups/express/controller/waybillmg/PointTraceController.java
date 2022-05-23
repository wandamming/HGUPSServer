package com.hgups.express.controller.waybillmg;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fanc
 * 2020/6/4 0004-15:08
 */
@Api(description = "过点扫描API")
@Slf4j
@RestController
@RequestMapping("/pointTrace")
public class PointTraceController {

    /*@ApiOperation(value = "过点扫描API")
    @PostMapping("/handlePointTrace")
    public Response handlePointTrace(@RequestBody ItemCategory itemCategory){
        Long loginUserId = ShiroUtil.getLoginUserId();

    }*/


}
