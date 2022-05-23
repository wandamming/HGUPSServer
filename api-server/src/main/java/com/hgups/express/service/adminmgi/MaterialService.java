package com.hgups.express.service.adminmgi;


import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Seman
 * @since 2020-11-16
 * <p>
 * 未注释
 */
@Service
public class MaterialService  {
//public class MaterialService extends ServiceImpl<MaterialMapper, Material> {

  /*  public List<Material> materialList(MaterialPageParam pageParam) {
        pageParam.setPageIndex((pageParam.getPageIndex()-1)*pageParam.getPageSize());
        return baseMapper.materialList(pageParam);
    }

    public Integer getCount(MaterialPageParam pageParam) {
        return baseMapper.getCount(pageParam);
    }

    //删除按钮图和广告长图接口
    public void deleteByType() {
        baseMapper.deleteByType();
    }

    public AdParam getAD() {
        AdParam adParam = new AdParam();
        Material material = new Material();
        material.setType(Long.valueOf(4));
        //获取按钮图
        Material material1 = baseMapper.selectOne(material);
        //获取广告图
        material.setType(Long.valueOf(5));
        Material material2 = baseMapper.selectOne(material);
        adParam.setButtonUrl(material1.getUrl());
        adParam.setAdUrl(material2.getUrl());
        return adParam;
    }

    public void insertMaterial(Long type, String name, String url) {
        Material material = new Material();
        material.setType(type);
        material.setUrl(url);
        material.setName(name);
        baseMapper.insert(material);
    }*/
}
