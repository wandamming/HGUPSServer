package com.hgups.express.mapper;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.Banner;
import com.hgups.express.domain.param.BannerSearchParam;
import com.hgups.express.domain.param.StatusParam;
import com.hgups.express.domain.param.WebBannerParam;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lyx
 * @since 2021-07-27
 */
public interface BannerMapper extends BaseMapper<Banner> {
    List<Banner> getApiBannerList(BannerSearchParam paramVo);

    Integer getCount(BannerSearchParam paramVo);

    void updateStatus(StatusParam param);

    List<Banner> getBannerList(WebBannerParam param);

    Integer getBannerCount(WebBannerParam param);
}
