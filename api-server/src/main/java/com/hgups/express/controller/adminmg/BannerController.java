package com.hgups.express.controller.adminmg;


import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.BannerSearchParam;
import com.hgups.express.domain.param.StatusParam;
import com.hgups.express.domain.param.WebBannerParam;
import com.hgups.express.domain.param.weightParam;
import com.hgups.express.domain.Banner;
import com.hgups.express.service.adminmgi.MaterialService;
import com.hgups.express.service.adminmgi.BannerService;
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

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author Seman
 * @since 2020-12/15
 * <p>
 * 官网Banner相关接口
 */
@RestController
@RequestMapping(value = "/webBanner")
@Slf4j
@Api(description = "官网Banner相关接口")
public class BannerController {

    @Autowired
    private BannerService bannerService;
    @ApiOperation("后台banner列表")
    @PostMapping(value = "/apiBannerList")
    public Response apiBannerList(@RequestBody BannerSearchParam paramVo) {
        Response response = new Response();
        List<Banner> bannerList = bannerService.getApiBannerList(paramVo);
        Integer count = bannerService.getCount(paramVo);
        Map result = ResultParamUtil.result(bannerList, count, paramVo.getCurrent() / paramVo.getSize() + 1, paramVo.getSize());
        response.setData(result);
        return response;
    }

    @ApiOperation("后台增加修改banner")
    @PostMapping(value = "/update")
    public Response update(@RequestBody Banner banner) {
        Response response = new Response();
        Banner banners = DomainCopyUtil.map(banner, Banner.class);
        boolean update = bannerService.insertOrUpdate(banners);
        if (update) {
            response.setResponseBySuccessMsg("添加商品成功");
            return response;
        }
        response.setResponseByErrorMsg("添加商品失败");
        return response;
    }

    @ApiOperation("后台批量删除banner")
    @PostMapping(value = "/deleteBannerList")
    public Response deleteBannerList(@RequestBody List<Long> ids) {
        Response response = new Response();
        boolean b = bannerService.deleteBatchIds(ids);
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

    @ApiOperation("后台banner批量可视、不可视")
    @PostMapping(value = "/updateBannerStatus")
    public Response updateBannerStatus(@RequestBody StatusParam param) {
        Response response = new Response();
        /*//数字等于1或者0才可以调用接口
        if (param.getStatus() == 0) {
            webBannerService.updateStatus(param);
        } else if (param.getStatus() == 1) {
            webBannerService.updateStatus(param);
        }*/
        bannerService.updateStatus(param);
        response.setMsg("操作成功");
        return response;
    }

    @ApiOperation("后台banner排序")
    @PostMapping(value = "/rankBanner")
    public Response rankCourseCenter(@RequestBody weightParam weightParam) {
        Response response = new Response();
        Banner webBanner = bannerService.selectById(weightParam.getId());
        //设置排序
        webBanner.setWeight(weightParam.getWeight());
        bannerService.updateById(webBanner);
        response.setMsg("操作成功");
        return response;
    }


}
