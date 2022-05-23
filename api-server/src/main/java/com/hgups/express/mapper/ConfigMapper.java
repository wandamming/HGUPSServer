package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.Config;

/**
 * @author fanc
 * 2020/6/24 0024-16:38
 */
public interface ConfigMapper extends BaseMapper<Config> {
    int updateConfigByK(String k);
}
