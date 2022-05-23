package com.hgups.express.service.usermgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.OperateLog;
import com.hgups.express.domain.param.OperateLogListParam;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.mapper.OperateLogMapper;
import com.hgups.express.vo.OperateLogVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class OperateLogService extends ServiceImpl<OperateLogMapper,OperateLog> {

    @Resource
    OperateLogMapper operateLogMapper;
    public Page<OperateLogVo> listOperateLog(OperateLogListParam pageParam) {
        EntityWrapper<OperateLogVo> entityWrapper = new EntityWrapper<>();
//        entityWrapper.eq("fake_delete", 0);
        entityWrapper.orderBy("operate_time", false);
        Page<OperateLogVo> page = new Page<>();
        page.setCurrent(pageParam.getCurrent());
        page.setSize(pageParam.getSize());
        List<OperateLogVo> vos=operateLogMapper.getOperateLogList(page,pageParam);
        //Page<OperateLog> operateLogPage = selectPage(page, entityWrapper);
        //int total = selectCount(entityWrapper);//总条数
        int total = operateLogMapper.count(pageParam);
        page.setTotal(total);
        page.setRecords(vos);
        //page.setRecords(operateLogMapper.getOperateLog(pageParam));
        return page;
    }
}
