package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.Article;

import java.util.List;


/**
 * @author fanc
 * 2020/6/9 0009-11:03
 */
public interface ArticleMapper  extends BaseMapper<Article> {

    List<String> categoryGroup();
    List<String> itemGroup();
}
