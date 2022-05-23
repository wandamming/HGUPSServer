package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.SynoticeType;
import com.hgups.express.domain.SystemNotice;
import com.hgups.express.mapper.SystemNoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author fanc
 * 2021/7/19-11:25
 */
@Service
@Transactional
public class SystemNoticeService extends ServiceImpl<SystemNoticeMapper,SystemNotice> {
    @Autowired(required = false)
    private SystemNoticeMapper systemNoticeMapper;

    //分页查询系统通知
    public Page<SystemNotice> allSystemNotice(SynoticeType param) {
        Page<SystemNotice> page = new Page<>();
        page.setCurrent(page.getCurrent());
        page.setSize(page.getSize());
        List<SystemNotice> vos = systemNoticeMapper.getSystemNotice(page, param);
        int total = systemNoticeMapper.getSynoticeTypeCount(param);
        page.setTotal(total);
        page.setRecords(vos);
        return page;
    }

    //删除系统通知信息
    public boolean deleteSystemNotice(Integer id) {
        try {
            systemNoticeMapper.deleteApiAccount(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
