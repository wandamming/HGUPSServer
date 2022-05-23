package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.BatchRecord;
import com.hgups.express.mapper.BatchRecordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author fanc
 * 2020/7/1 0001-17:11
 */
@Service
public class BatchRecordService extends ServiceImpl<BatchRecordMapper,BatchRecord> {

    @Resource
    private BatchRecordMapper batchRecordMapper;

    //添加批量历史
    public Integer setBatchRecord(BatchRecord batchRecord){
        Integer insert = batchRecordMapper.insert(batchRecord);
        if (insert>0){
            return batchRecord.getBatchId();
        }
        return -1;
    }

    public int getCount(EntityWrapper wrapper){
        return batchRecordMapper.selectCount(wrapper);//总条数
    }


}
