package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.ConfigRecord;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lyx
 * @since 2021-07-16
 */
public interface ConfigRecordMapper extends BaseMapper<ConfigRecord> {
    List<ConfigRecord> getConfigRecord();



}
