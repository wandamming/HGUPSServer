package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.mapper.ItemCategoryMapper;
import com.hgups.express.domain.ItemCategory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author fanc
 * 2020/6/10 0010-14:39
 */
@Service
public class ItemCategoryService extends ServiceImpl<ItemCategoryMapper,ItemCategory> {

    @Resource
    private ItemCategoryMapper itemCategoryMapper;

    @Transactional
    public boolean setItemCategory(ItemCategory itemCategory){
        try{
            int flag = itemCategoryMapper.insert(itemCategory);
            if(flag>0){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteItemCategory(Integer id) {
        try {
            int flag = itemCategoryMapper.deleteById(id);
            if(flag>0){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }

}
