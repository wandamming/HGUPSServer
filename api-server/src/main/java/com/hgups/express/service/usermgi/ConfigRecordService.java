package com.hgups.express.service.usermgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.ConfigRecord;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.mapper.ConfigRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class ConfigRecordService extends ServiceImpl<ConfigRecordMapper,ConfigRecord> {
    public Page<ConfigRecord> listConfigRecord(PageParam pageParam) {
        EntityWrapper<ConfigRecord> entityWrapper = new EntityWrapper<>();
//        entityWrapper.eq("fake_delete", 0);
        //entityWrapper.orderBy("operate_time", false);
        Page<ConfigRecord> page = new Page<>();
        page.setCurrent(pageParam.getCurrent());
        page.setSize(pageParam.getSize());

        Page<ConfigRecord> configRecordPage = selectPage(page, entityWrapper);
        int total = selectCount(entityWrapper);//总条数

        page.setTotal(total);
        page.setRecords(configRecordPage.getRecords());
        return page;
    }
}
