package com.hgups.express.service.usermgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.PortLateDhl;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.domain.param.ParamId;
import com.hgups.express.mapper.PortLateDhlMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2020/11/27-17:44
 */
@Service
public class PortLateDhlService extends ServiceImpl<PortLateDhlMapper,PortLateDhl> {

    @Resource
    private PortLateDhlMapper portLateDhlMapper;

    //后程用户DHL入境口岸
    public List<PortLateDhl> getPortLateDhlList(PageParam param){
        EntityWrapper<PortLateDhl> wrapper = new EntityWrapper<>();
        wrapper.eq("port_dhl_state",1);
        Page<PortLateDhl> page = new Page<>(param.getCurrent(),param.getSize());
        Page<PortLateDhl> portLateDhlPage = selectPage(page, wrapper);
        return portLateDhlPage.getRecords();

    }

    //后程用户DHL入境口岸总数
    public Integer getPortLateDhlListCount(){
        EntityWrapper<PortLateDhl> wrapper = new EntityWrapper<>();
        wrapper.eq("port_dhl_state",1);
        return portLateDhlMapper.selectCount(wrapper);
    }

    //修改后程用户DHL入境口岸状态
    public Integer getPortLateDhlListState(ParamId param){
        PortLateDhl portLateDhl = portLateDhlMapper.selectById(param.getId());
        if (portLateDhl.getPortDhlState()==0){
            return 2;//管理员关闭了该入境口岸
        }
        portLateDhl.setLateDhlState(param.getState());
        boolean b = updateById(portLateDhl);
        if (b){
            return 1;
        }
        return -1;
    }

}
