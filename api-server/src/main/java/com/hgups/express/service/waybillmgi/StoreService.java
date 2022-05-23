package com.hgups.express.service.waybillmgi;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.hgups.express.domain.Store;

import com.hgups.express.domain.param.*;
import com.hgups.express.domain.vo.StoreVo;
import com.hgups.express.mapper.StoreMapper;
import com.hgups.express.util.MyFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wandaming
 * 2021/7/26-15:46
 */

@Slf4j
@Service
@Transactional
public class StoreService extends ServiceImpl<StoreMapper,Store> {
    @Autowired(required = false)
    private StoreMapper storeMapper;

    @Autowired(required = false)
    private Store store;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //分页查询系统通知
    public Page<StoreVo> getStoreList(StoreParam param) {
        Page<StoreVo> page = new Page<>();
        page.setCurrent(page.getCurrent());
        page.setSize(page.getSize());
        List<StoreVo> vos = storeMapper.getStoreList(page, param);
        int total = storeMapper.getStoreListCount(param);
        page.setTotal(total);
        page.setRecords(vos);
        return page;
    }


    public boolean removeAuthorize(IdParam param) {
        try {
            storeMapper.removeAuthorization(param);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


//    public List<StoreVo> exportStoreList(Map map) {
//        List<StoreVo> vo = storeMapper.exportStoreList(map);
//        return vo;
//    }


    public ResponseEntity exportStoreList(@RequestParam List storeList) {
        //    Long loginUserId = ShiroUtil.getLoginUserId();
        Map<Object, Object> map = new HashMap<>();
        map.put("ids", storeList);
        List<StoreVo> getStoreList = storeMapper.exportStoreList(map);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//设置日期格式
        List<String> headRow1 = Lists.newArrayList("序号", "店铺名称", "所属平台", "店长", "联系电话", "状态", "授权时间");

        ExcelWriter writer = ExcelUtil.getWriter();
        writer.writeHeadRow(headRow1);
        for (int i = 0; i < headRow1.size(); i++) {
            writer.autoSizeColumn(i);
            if (i == 0) {
                writer.setColumnWidth(i, 12);
            } else if (i == 1) {
                writer.setColumnWidth(i, 40);
            } else {
                writer.setColumnWidth(i, 30);
            }
        }
        List i = new ArrayList();
        getStoreList.forEach(x -> {
            i.add("");
            List<Object> dataList = Lists.newArrayList();
            dataList.add(i.size());
            //发件人信息
            dataList.add(x.getId());
            dataList.add(x.getName());
            dataList.add(x.getPlatformName());
            dataList.add(x.getDirector());
            dataList.add(x.getTelephone());
            dataList.add(x.getState());
            Date createTime = x.getCreateTime();
            dataList.add(df.format(createTime));
            writer.writeRow(dataList);
        });

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "店铺列表.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }




    public boolean insertStore(InsertStoreParam param) {

        try {
            storeMapper.insertStore(param);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    public boolean changeState(IdParam param){
        return storeMapper.changeState(param);
    }


}
