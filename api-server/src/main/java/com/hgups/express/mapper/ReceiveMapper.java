package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.Receive;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/6/5 0005-15:46
 */
public interface ReceiveMapper extends BaseMapper<Receive> {

    List<Receive> allReceive(Map map);

    void deleteReceive(@Param("id") Integer id);

    //查询是否有相同联系人
    Receive getReceive(Receive receive);
}
