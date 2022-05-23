package com.hgups.express.service.adminmgi;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.param.BannerSearchParam;
import com.hgups.express.domain.param.StatusParam;
import com.hgups.express.domain.param.WebBannerParam;
import com.hgups.express.domain.Banner;
import com.hgups.express.mapper.BannerMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Seman
 * @since 2020-12.18
 * <p>
 * 未注释
 */
@Service
public class BannerService extends ServiceImpl<BannerMapper, Banner> {

    public List<Banner> getApiBannerList(BannerSearchParam paramVo) {
        paramVo.setCurrent((paramVo.getCurrent()-1)*paramVo.getSize());
        return baseMapper.getApiBannerList(paramVo);
    }

    public Integer getCount(BannerSearchParam paramVo) {
        return baseMapper.getCount(paramVo);
    }

    public void updateStatus(StatusParam param) {
        baseMapper.updateStatus(param);
    }

    public List<Banner> getBannerList(WebBannerParam param) {
        param.setCurrent((param.getCurrent()-1)*param.getSize());
        return baseMapper.getBannerList(param);
    }
    public Integer getBannerCount(WebBannerParam param) {
        return baseMapper.getBannerCount(param);
    }
}
