package com.hgups.express.mapper;

import com.hgups.express.domain.param.NotReplaceSendOutboundDetailsVo;
import com.hgups.express.domain.param.OutboundDetailsVo;
import com.hgups.express.domain.param.UpdateOutboundDetailsVo;
import com.hgups.express.domain.param.UpdateReplaceSendOutboundDetailsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author fanc
 * 2020/9/29 0029-14:14
 */
public interface OutboundDetailsMapper {

    OutboundDetailsVo getOutboundDetails(@Param("id")long id);

    List<OutboundDetailsVo> getOutboundDetailList(@Param("ids")List ids);

    NotReplaceSendOutboundDetailsVo getNotReplaceSendOutboundDetails(@Param("id")long id);


    UpdateOutboundDetailsVo UpdateOutboundDetails(@Param("id")long id);

    UpdateReplaceSendOutboundDetailsVo UpdateReplaceSendOutboundDetails(@Param("id")long id);

}
