package com.hgups.express.service.usermgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.PortEntry;
import com.hgups.express.domain.PortLate;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.domain.param.ParamId;
import com.hgups.express.domain.param.PortEntryParam;
import com.hgups.express.mapper.PortLateMapper;
import com.hgups.express.util.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fanc
 * 2020/6/24 0024-16:38
 */
@Service
@Slf4j
public class PortLateService extends ServiceImpl<PortLateMapper, PortLate> {

    @Resource
    private PortLateMapper portLateMapper;

    @Resource
    private RightsManagementService rightsManagementService;

    @Resource
    private UserService userService;

    //后程用户入境口岸
    public List<PortLate> getPortLateList(PageParam param) {
        Long userId = ShiroUtil.getLoginUserId();
        EntityWrapper<PortLate> wrapper = new EntityWrapper<>();
        wrapper.eq("port_state", 1);
        wrapper.eq("user_id", userId);
        Page<PortLate> page = new Page<>(param.getCurrent(), param.getSize());
        Page<PortLate> portLatePage = selectPage(page, wrapper);
        return portLatePage.getRecords();

    }

    //后程用户入境口岸总数
    public Integer getPortLateListCount() {
        Long userId = ShiroUtil.getLoginUserId();
        EntityWrapper<PortLate> wrapper = new EntityWrapper<>();
        wrapper.eq("port_state", 1);
        wrapper.eq("user_id", userId);
        return portLateMapper.selectCount(wrapper);
    }

    //修改后程用户入境口岸状态
    public Integer getPortLateListState(ParamId param) {
        PortLate portLate = portLateMapper.selectById(param.getId());
        if (portLate.getPortState() == 0) {
            return 2;//管理员关闭了该入境口岸
        }
        portLate.setLateState(param.getState());
        boolean b = updateById(portLate);
        if (b) {
            return 1;
        }
        return -1;
    }

    public void addPortForAllUser(PortEntry port) {
        List<Integer> ids = rightsManagementService.getLateUserIds();
        List<PortLate> portLates = new ArrayList<>();

        for (Integer id : ids) {
            PortLate portLate = new PortLate();
            portLate.setPortLateName(port.getTitle());
            portLate.setPortState(1);
            portLate.setLateState(1);
            portLate.setPortId(port.getId());
            portLate.setUserId(new Long(id));
            portLates.add(portLate);
        }

        boolean status = insertBatch(portLates);
        log.info(" addPortForAllUser status: " + status);
    }

    /**
     * 如果用户不是后程用户了，要删除他的后程入境口岸
     *
     * @param userId
     */
    public void deleteLatePorts(long userId) {
        EntityWrapper<PortLate> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id", userId);
        boolean status = delete(wrapper);
        log.info(" deleteLatePorts status: " + status);
    }

    /**
     * 如果角色添加了 后程用户，则进行后程入境口岸的创建
     */
    public void addAllLatePortForNewRole(List<PortEntry> portEntries, Long userId) {
        List<PortLate> portLates = new ArrayList<>();
        for(PortEntry port : portEntries) {
            PortLate portLate = new PortLate();
            portLate.setPortLateName(port.getTitle());
            portLate.setPortState(port.getState());
            portLate.setLateState(1);
            portLate.setPortId(port.getId());
            portLate.setUserId(userId);
            portLates.add(portLate);
        }

        boolean status = insertBatch(portLates);
        log.info(" addAllLatePortForNewRole status: " + status);
    }

    public void adjustLatePorts(boolean delete, boolean add, List<PortEntry> portEntries, Long userId) {

        if(delete) {
            deleteLatePorts(userId);
        }

        if(add) {
            addAllLatePortForNewRole(portEntries, userId);
        }

    }

}
