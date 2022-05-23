package com.hgups.express.service.usermgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.Config;
import com.hgups.express.mapper.ConfigMapper;
import org.springframework.stereotype.Service;

/**
 * @author fanc
 * 2020/6/24 0024-16:38
 */
@Service
public class ConfigService extends ServiceImpl<ConfigMapper,Config> {
    public void wrappereq( String s){

        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("k","s");
    }


}
