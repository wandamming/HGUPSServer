package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hgups.express.domain.OperateLog;
import com.hgups.express.domain.param.OperateLogListParam;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.vo.OperateLogVo;
import org.apache.ibatis.annotations.Param;

import javax.print.DocFlavor;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lyx
 * @since 2021-07-16
 */
public interface OperateLogMapper extends BaseMapper<OperateLog> {


    List<OperateLogVo> getOperateLogList(Pagination pagination, OperateLogListParam pageParam);
    int count(OperateLogListParam param);

    List<Map<String ,Object>> getUserId();
}
