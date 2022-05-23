package com.hgups.express.controller.adminmg;


import com.hgups.express.domain.Response;
import com.hgups.express.domain.WebNews;
import com.hgups.express.domain.param.StatusParam;
import com.hgups.express.domain.param.WebNewsApiPageParam;
import com.hgups.express.domain.param.WebNewsPageParam;
import com.hgups.express.domain.param.weightParam;
import com.hgups.express.service.adminmgi.WebNewsService;

import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.ResultParamUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Seman
 * @since 2020-12/21
 * <p>
 * 官网新闻资讯相关接口
 */
@RestController
@RequestMapping(value = "/webNews")
@Slf4j
@Api(description = "官网新闻资讯相关接口")
public class WebNewsController {

    @Autowired
    private WebNewsService webNewsService;
    /*@Resource
    private MaterialService materialService;*/

    @ApiOperation("后台新闻资讯列表")
    @PostMapping(value = "/apiNewsList")
    public Response apiNewsList(@RequestBody WebNewsApiPageParam paramVo) {
        Response response = new Response();
        List<WebNews> bannerList = webNewsService.getApiNewsList(paramVo);
        Integer count = webNewsService.getAPiCount(paramVo);
        Map result = ResultParamUtil.result(bannerList, count, paramVo.getCurrent() / paramVo.getSize() + 1, paramVo.getSize());
        response.setData(result);
        return response;
    }

    @ApiOperation("后台增加修改课程中心")
    @PostMapping(value = "/update")
    public Response update(@RequestBody WebNews webNewsParam) {
        Response response = new Response();

//        //将图片路径保存在素材库
//        Long type = 1L;
//        materialService.insertMaterial(type,webNewsParam.getTitle(),webNewsParam.getPicture());

        WebNews webNews = DomainCopyUtil.map(webNewsParam, WebNews.class);
        boolean update = webNewsService.insertOrUpdate(webNews);
        if (update) {
            response.setData(true);
            response.setStatusCode(200);
            return response;
        }
        response.setData(false);
        response.setStatusCode(199);
        response.setMsg("操作失败，请联系管理员");
        return response;
    }

    @ApiOperation("后台批量删除WebNews")
    @PostMapping(value = "/deleteWebNews")
    public Response deleteWebNews(@RequestBody List<Long> ids) {
        Response response = new Response();
        //不能删除已经已经启用的（官网会没有内容）
        boolean b = webNewsService.deleteBatchIds(ids);
        if (b) {
            response.setData(true);
            response.setStatusCode(200);
            return response;
        }
        response.setData(false);
        response.setStatusCode(199);
        response.setMsg("操作失败，请联系管理员");
        return response;
    }

    @ApiOperation("后台News批量可视、不可视")
    @PostMapping(value = "/updateNewsVisible")
    public Response updateNewsVisible(@RequestBody StatusParam param) {
        Response response = new Response();
        /*//数字等于1或者0才可以调用接口
        if (param.getStatus() == 0) {
            webNewsService.updateVisible(param);
        } else if (param.getStatus() == 1) {
            webNewsService.updateVisible(param);
        }*/
        response.setMsg("操作成功");
        return response;
    }

    @ApiOperation("后台News排序")
    @PostMapping(value = "/rankNews")
    public Response rankNews(@RequestBody weightParam rankParam) {
        Response response = new Response();
        WebNews webNews = webNewsService.selectById(rankParam.getId());
        //设置排序
        webNews.setRank(rankParam.getWeight());
        webNewsService.updateById(webNews);
        response.setMsg("操作成功");
        return response;
    }

    @ApiOperation("官网News列表")
    @PostMapping(value = "/newsList")
    public Response newsList(@RequestBody WebNewsPageParam param) {
        Response response = new Response();
        List<WebNews> bannerList = webNewsService.getWebNewsList(param);
        Integer count = webNewsService.getCount(param);
        Map result = ResultParamUtil.result(bannerList, count, param.getCurrent() / param.getSize() + 1, param.getSize());
        response.setData(result);
        return response;
    }
}
