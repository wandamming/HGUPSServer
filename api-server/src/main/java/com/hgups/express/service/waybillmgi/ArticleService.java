package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.mapper.ArticleMapper;
import com.hgups.express.domain.Article;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author fanc
 * 2020/6/9 0009-11:05
 */

@Service
public class ArticleService extends ServiceImpl<ArticleMapper,Article>{

    @Resource
    private ArticleMapper articleMapper;

    public List<String> categoryGroup(){
        return articleMapper.categoryGroup();
    }

    public List<String> itemGroup(){
        return articleMapper.itemGroup();
    }

}
