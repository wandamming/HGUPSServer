package com.hgups.express.service.waybillmgi;

import com.hgups.express.domain.CustomsList;
import com.hgups.express.mapper.CustomsListMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/6/20 0020-12:26
 */
@Service
@Transactional
public class CustomsListService {

    @Resource
    private CustomsListMapper customsListMapper;

    public List<CustomsList> getCustomsList(Map map){
        List<CustomsList> customsList = customsListMapper.getCustomsList(map);
        return customsList;
    }


}
