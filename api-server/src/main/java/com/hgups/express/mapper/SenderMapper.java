package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.Sender;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/6/4 0004-17:27
 */
public interface SenderMapper extends BaseMapper<Sender> {

    List<Sender> allSender(Map map);

    void deleteSender(@Param("id") Integer id);
}
