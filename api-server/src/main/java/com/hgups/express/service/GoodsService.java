package com.hgups.express.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.Goods;
import com.hgups.express.domain.PointScanChild;
import com.hgups.express.mapper.GoodsMapper;
import com.hgups.express.mapper.PointScanChildMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;

@Slf4j
@Service
public class GoodsService extends ServiceImpl<GoodsMapper, Goods> {


    @Transactional(rollbackFor = Exception.class)
    public void updateTotal(int id) throws FileNotFoundException {
        Goods goods = baseMapper.getGoodsForUpdate(id);
        goods.setTotal(goods.getTotal() + 1);
        updateById(goods);
        File file = new File("dd");
        FileReader inputStream = new FileReader(file);
    }


}
