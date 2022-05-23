package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.Goods;
import com.hgups.express.domain.PointScanChild;
import com.hgups.express.service.GoodsService;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author: LZJ
 * @Date: 2021/3/4 1:10
 */
public interface GoodsMapper extends BaseMapper<Goods> {


    @Select("select * from goods where id=#{id} for update")
    public Goods getGoodsForUpdate(@Param("id") int id);
}
