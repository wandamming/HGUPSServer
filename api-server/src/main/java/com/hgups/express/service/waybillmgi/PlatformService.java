package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.Platform;
import com.hgups.express.domain.param.IdParam;
import com.hgups.express.domain.param.IdsParam;
import com.hgups.express.domain.param.PlatformAdderssParam;
import com.hgups.express.domain.vo.PlatformVo;
import com.hgups.express.mapper.PlatformMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2021/7/27-16:32
 */
@Service
@Transactional
public class PlatformService extends ServiceImpl<PlatformMapper,Platform> {

    @Autowired(required = false)
    private PlatformMapper platformMapper;

    public List<PlatformVo> getPlatform(PlatformAdderssParam param){
        return platformMapper.getPlatform(param);
    }
}
