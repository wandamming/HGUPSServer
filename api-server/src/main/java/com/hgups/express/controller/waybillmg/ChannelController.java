package com.hgups.express.controller.waybillmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hgups.express.domain.Channel;
import com.hgups.express.domain.Response;
import com.hgups.express.service.waybillmgi.ChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2020/6/4 0004-15:08
 */
@Api(description = "渠道API")
@Slf4j
@RestController
@RequestMapping("/channel")
public class ChannelController {

    @Resource
    private ChannelService channelService;

    @ApiOperation(value = "获取渠道列表")
    @PostMapping("getChannelList")
    public Response getBatchRecord(){
        Response response = new Response();
        EntityWrapper<Channel> wrapper = new EntityWrapper<>();
        wrapper.eq("is_show",0);
        List<Channel> channels = channelService.selectList(wrapper);
        for (Channel channel : channels) {
            String s = channel.getChannelName().toUpperCase();//小写转换大写
            channel.setChannelName(s);
        }
        response.setData(channels);
        return response;
    }

}
