package com.hgups.express.mapper;

import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hgups.express.domain.ProductInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.param.IdPageParam;
import com.hgups.express.domain.param.OrderParam;
import com.hgups.express.domain.vo.ProductInfoVo;

import java.util.List;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wdm
 * @since 2021-07-27
 */
public interface ProductInfoMapper extends BaseMapper<ProductInfo> {

    //订单中商品详情
    List<ProductInfoVo> getOrderProduct(Pagination pagination, IdPageParam param);

    Integer getOrderProductCount(IdPageParam param);
}
