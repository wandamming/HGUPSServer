package com.hgups.express.controller.warehousermg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.Notice;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.NewNoticeVo;
import com.hgups.express.domain.param.NoticeParam;
import com.hgups.express.service.warehousemgi.NoticeService;
import com.hgups.express.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/11/3-20:16
 */

@Api(description = "通知相关API")
@Slf4j
@RestController
@RequestMapping("notice")
public class NoticeController {

    @Resource
    private NoticeService noticeService;


    @ApiOperation(value = "历史通知列表")
    @PostMapping("/test")
    public Response test() throws Exception{
        WebSocketServer.sendInfo("666666","3",1,1L);
        return null;
    }



    @PostMapping("/noticeList")
    @ApiOperation(value = "历史通知列表")
    public Response noticeList(@RequestBody NoticeParam param){
        Response response = new Response();
        Integer noticeType = param.getNoticeType();
        if (noticeType==null){
            response.setStatusCode(ResponseCode.FAILED_CODE);
            response.setMsg("参数错误");
            return response;
        }
        EntityWrapper<Notice> wrapper = new EntityWrapper<>();
        wrapper.eq("notice_type",noticeType);
        wrapper.eq("to_user_id",ShiroUtil.getLoginUserId());
        //总数
        int total = noticeService.selectCount(wrapper);
        Page<Notice> page = new Page<>(param.getCurrent(),param.getSize());
        Page<Notice> page1 = noticeService.selectPage(page, wrapper);

        //改变通知状态为已读
        List<Notice> notices = noticeService.selectList(wrapper);
        for (Notice notice : notices) {
            notice.setLookOver(1);
        }
        if (notices.size()>0){
            noticeService.updateBatchById(notices);
        }

        Map<Object,Object> result = new HashMap<>();
        result.put("total",total);
        result.put("size",param.getSize());
        result.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);
        result.put("current",param.getCurrent());
        result.put("records",page1.getRecords());
        response.setData(result);
        return response;
    }

    @PostMapping("/newNotice")
    @ApiOperation(value = "最新通知")
    public Response newNotice(){
        Response response = new Response();
        List<NewNoticeVo> newNoticeVos = noticeService.newNotice();
        response.setData(newNoticeVos);
        return response;
    }

}
