package com.hgups.express.service.usermgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.Banner;
import com.hgups.express.domain.EntryButton;
import com.hgups.express.domain.WayBill;
import com.hgups.express.domain.param.*;
import com.hgups.express.mapper.EntryButtonMapper;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.vo.EntryVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lyx
 * 2021/7/26 0017-11:07
 */
@Service
public class EntryButtonService extends ServiceImpl<EntryButtonMapper,EntryButton>{
    @Resource
    public EntryButtonMapper entrybuttonMapper;

    public List<EntryVo> getEntry(Long loginUserId){
        List<EntryVo> entryVoLists = entrybuttonMapper.getEntry(loginUserId);
        return  entryVoLists;
    }

    public List<EntryVo> getAllEntry(NameParam param){

        List<EntryVo> allEntryLists = entrybuttonMapper.getAllEntry(param);
        return allEntryLists;

    }
    public boolean insertEntry(Long loginUserId,ButtonParam param){
        return entrybuttonMapper.insertEntry(loginUserId,param);
    }
    public boolean deleteEntry(Long loginUserId,ButtonParam param){
        return entrybuttonMapper.deleteEntry(loginUserId,param);
    }

    public List<EntryVo> getRecord(Long loginUserId){
        List<EntryVo> records = entrybuttonMapper.getRecord(loginUserId);
        return  records;
    }

    public boolean deleteAllRecords(Long loginUserId){
        return entrybuttonMapper.deleteAllRecords(loginUserId);
    }

    public boolean updateRecords(UserButtonParam param){
        return entrybuttonMapper.updateRecords(param);
    }

    public boolean sortEntry(SortParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();//参数用user_id 和 button_id
        Integer w1 =entrybuttonMapper.getW(loginUserId,param.getIds().get(0));
        Integer w2 = entrybuttonMapper.getW(loginUserId,param.getIds().get(1));
        boolean a;
        if(w1>w2) {
            a = entrybuttonMapper.updateSort(loginUserId,w2, w1);
        }
        else{
            a= entrybuttonMapper.updateSort1(loginUserId,w1,w2);
        }
        boolean b = entrybuttonMapper.updateChange(w2, param.getIds().get(0),loginUserId);
        return a&&b;
    }
}
