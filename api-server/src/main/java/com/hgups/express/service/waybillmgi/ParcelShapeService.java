package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.mapper.ParcelShapeMapper;
import com.hgups.express.domain.ParcelShape;
import com.hgups.express.util.ShiroUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2020/6/10 0010-14:00
 */
@Service
public class ParcelShapeService extends ServiceImpl<ParcelShapeMapper,ParcelShape>{

    @Resource
    private ParcelShapeMapper parcelShapeMapper;

    @Transactional
    public boolean setParcelShape(ParcelShape parcelShape){
        try {
            int flag = parcelShapeMapper.insert(parcelShape);
            if (flag>0){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }

    public boolean deleteParcelShape(Integer id) {
        try {
            int flag = parcelShapeMapper.deleteById(id);
            if(flag>0){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }


    public List<ParcelShape> getParcelShape(){
        return parcelShapeMapper.selectList(null);
    }

}
