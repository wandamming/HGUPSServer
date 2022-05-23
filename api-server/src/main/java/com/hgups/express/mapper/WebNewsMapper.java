package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.WebNews;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.domain.param.StatusParam;
import com.hgups.express.domain.param.WebNewsApiPageParam;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Seman
 * @since 2020-12-21
 */
public interface WebNewsMapper extends BaseMapper<WebNews> {

    List<WebNews> getApiNewsList(WebNewsApiPageParam paramVo);

    Integer getAPiCount(WebNewsApiPageParam paramVo);

    void updateVisible(StatusParam param);

    List<WebNews> getWebNewsList(PageParam param);

    Integer getCount(PageParam param);
}
