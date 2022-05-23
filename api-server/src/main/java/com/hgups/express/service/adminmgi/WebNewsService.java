package com.hgups.express.service.adminmgi;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.WebNews;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.domain.param.StatusParam;
import com.hgups.express.domain.param.WebNewsApiPageParam;
import com.hgups.express.mapper.WebNewsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WebNewsService extends ServiceImpl<WebNewsMapper, WebNews> {

    public List<WebNews> getApiNewsList(WebNewsApiPageParam paramVo) {
        paramVo.setCurrent((paramVo.getCurrent()-1)*paramVo.getSize());
        return baseMapper.getApiNewsList(paramVo);
    }

    public Integer getAPiCount(WebNewsApiPageParam paramVo) {
        return baseMapper.getAPiCount(paramVo);
    }

    public void updateVisible(StatusParam param) {
        baseMapper.updateVisible(param);
    }

    public List<WebNews> getWebNewsList(PageParam param) {
        param.setCurrent((param.getCurrent()-1)*param.getSize());
        return baseMapper.getWebNewsList(param);
    }

    public Integer getCount(PageParam param) {
        return baseMapper.getCount(param);
    }
}
