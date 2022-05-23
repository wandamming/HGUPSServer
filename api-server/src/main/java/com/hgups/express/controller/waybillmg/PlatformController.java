package com.hgups.express.controller.waybillmg;

import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.IdParam;
import com.hgups.express.domain.param.IdsParam;
import com.hgups.express.domain.param.PlatformAdderssParam;
import com.hgups.express.domain.vo.PlatformVo;
import com.hgups.express.service.waybillmgi.PlatformService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wandaming
 * 2021/7/21-15:12
 */

@Api(description = "平台API")
@Slf4j
@RestController
@RequestMapping("/Platform")
public class PlatformController {

    @Resource
    private PlatformService platformService;

    @ApiOperation(value = "所属平台",notes = "此接口用于获取店铺所属平台")
    @PostMapping("/getPlatform")
    public Response getPlatform(@ApiParam(value = "平台id") @RequestBody PlatformAdderssParam param ) {
        Response response = new Response();
        List<PlatformVo> vo = platformService.getPlatform(param);
        response.setData(vo);
        return response;
    }


}