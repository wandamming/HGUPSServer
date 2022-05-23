package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hgups.express.domain.SynoticeType;
import com.hgups.express.domain.SystemNotice;
import com.hgups.express.domain.WayBill;
import com.hgups.express.domain.param.ProblemWayBillParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wandaming
 * @since 2021-07-19
 */
public interface SystemNoticeMapper extends BaseMapper<SystemNotice> {

    //删除系统消息
    void deleteApiAccount(@Param("id") Integer id);

    List<SystemNotice> getSystemNotice(Pagination pagination,SynoticeType param);

    Integer getSynoticeTypeCount(SynoticeType param);

}
