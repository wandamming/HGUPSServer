package com.hgups.express.controller.waybillmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.PointScan;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.ScanRoleMiddle;
import com.hgups.express.domain.param.*;
import com.hgups.express.domain.vo.ResultPageVo;
import com.hgups.express.exception.MyException;
import com.hgups.express.service.waybillmgi.PointScanService;
import com.hgups.express.service.waybillmgi.ScanRoleMiddleService;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.ResultParamUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fanc
 * 2020/11/6-13:46
 */
@RestController
@Api(description = "过点扫描配置相关相关API")
@RequestMapping("/pointScan")
public class PointScanController {

    @Resource
    private PointScanService pointScanService;
    @Resource
    private ScanRoleMiddleService scanRoleMiddleService;

    @PostMapping("/addAndModifyPointScan")
    @ApiOperation(value = "增加修改过点扫描")
    public Response addAndModifyPointScan(@RequestBody PointScanConfigParam param) {
        Response response = new Response();
        boolean b = pointScanService.addAndModifyPointScan(param);
        if (b) {
            response.setMsg("成功");
        } else {
            response.setMsg("失败");
        }
        return response;
    }

    @ApiOperation(value = "过点扫描排序")
    @PostMapping("/sortPointScan")
    public Response sortPointScan(@RequestBody @Valid SortPointScanParam param) {
        Response response = new Response();
        boolean b = pointScanService.sortPointScan(param);
        if (b) {
            response.setMsg("成功");
        } else {
            response.setMsg("失败");
        }
        return response;
    }

    @ApiOperation(value = "过点扫描开启/关闭")
    @PostMapping("/openPointScan")
    public Response openPointScan(@RequestBody @Valid IdLongParam param) {
        Response response = new Response();
        try {
            boolean b = pointScanService.openPointScan(param.getId());
            if (b) {
                response.setMsg("成功");
            } else {
                response.setMsg("失败");
            }
        } catch (MyException e) {
            response.setResponseByErrorMsg(e.getMessage());
        }

        return response;
    }

    @PostMapping("/deletePointScan")
    @ApiOperation(value = "删除过点扫描")
    public Response deletePointScan(@RequestBody @Valid IdLongParam param) {
        Response response = new Response();
        Long pointScanId = param.getId();
        try {
            boolean b = pointScanService.deletePointScan(pointScanId);
            if (b) {
                response.setStatusCode(ResponseCode.SUCCESS_CODE);
                response.setMsg("成功");
            } else {
                response.setMsg("失败");
            }
        } catch (MyException e) {
            response.setResponseByErrorMsg(e.getMessage());
        }

        return response;
    }

    @PostMapping("/pointScanList")
    @ApiOperation(value = "过点扫描列表")
    public Response<List<PointScanListVo>> pointScanList(@RequestBody @Valid PointScanListParam param) {
        Response<List<PointScanListVo>> response = new Response<>();
        Integer scanType = param.getScanType();
        EntityWrapper<PointScan> pointScanEntityWrapper = new EntityWrapper<>();
        if (scanType == 1) {
            pointScanEntityWrapper.in("scan_type", Arrays.asList(1, 2));
        } else {
            pointScanEntityWrapper.eq("scan_type", scanType);
        }
        pointScanEntityWrapper.eq("fake_delete", 1).orderBy("rank", true);
        List<PointScan> records = pointScanService.selectList(pointScanEntityWrapper);
        List<PointScanListVo> pointScanListVos = DomainCopyUtil.mapList(records, PointScanListVo.class);
        for (PointScanListVo pointScanVo : pointScanListVos) { // 设置该点的角色id
            EntityWrapper<ScanRoleMiddle> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("point_scan_id", pointScanVo.getId());
            List<ScanRoleMiddle> scanRoleMiddles = scanRoleMiddleService.selectList(wrapper1);
            List<Long> rids = scanRoleMiddles.stream().map(ScanRoleMiddle::getRoleId).collect(Collectors.toList());
            pointScanVo.setRoleIds(rids);
        }
        response.setData(pointScanListVos);
        return response;
    }

//    2021-03-03 历史版本保留 - LZJ
//    @PostMapping("/pointScanList")
//    @ApiOperation(value = "过点扫描列表")
//    public Response pointScanList(@RequestBody PointScanListParam param){
//        Response response = new Response();
//        Integer scanType = param.getScanType();
//        if (scanType==null){
//            response.setStatusCode(ResponseCode.FAILED_CODE);
//            response.setMsg("参数错误");
//            return response;
//        }
//        Page<PointScan> pointScanPage = new Page<>(param.getCurrent(),param.getSize());
//        EntityWrapper<PointScan> wrapper = new EntityWrapper<>();
//        wrapper.eq("scan_type",scanType);
//        wrapper.eq("fake_delete",1);
//        wrapper.orderBy("id",true);
//        Page<PointScan> pointScanPage1 = pointScanService.selectPage(pointScanPage, wrapper);
//        List<PointScan> records = pointScanPage1.getRecords();
//        List<PointScanListVo> pointScanListVos = DomainCopyUtil.mapList(records, PointScanListVo.class);
//        for (PointScanListVo pointScanVo : pointScanListVos) {
//            Long id = pointScanVo.getId();
//            EntityWrapper<ScanRoleMiddle> wrapper1 = new EntityWrapper<>();
//            wrapper1.eq("point_scan_id",id);
//            List<ScanRoleMiddle> scanRoleMiddles = scanRoleMiddleService.selectList(wrapper1);
//            List<Long> rids = new ArrayList<>();
//            for (ScanRoleMiddle scanRoleMiddle : scanRoleMiddles) {
//                rids.add(scanRoleMiddle.getRoleId());
//            }
//            pointScanVo.setRoleIds(rids);
//        }
//        int total = pointScanService.selectCount(wrapper);
//        Map<Object,Object> map = new HashMap<>();
//        map.put("total",total);
//        map.put("current",param.getCurrent());
//        map.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);//总条数
//        map.put("records",pointScanListVos);
//        response.setData(map);
//        response.setStatusCode(200);
//        return response;
//    }


}
