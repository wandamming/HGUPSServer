package com.hgups.express.service.warehousemgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.Notice;
import com.hgups.express.domain.param.NewNoticeVo;
import com.hgups.express.mapper.NoticeMapper;
import com.hgups.express.util.ShiroUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fanc
 * 2020/9/25 0025-16:43
 */
@Service
public class NoticeService extends ServiceImpl<NoticeMapper,Notice> {


    @Resource
    private NoticeMapper noticeMapper;

    public List<NewNoticeVo> newNotice(){
        EntityWrapper<Notice> wrapper = new EntityWrapper<>();
        wrapper.groupBy("notice_type");
        wrapper.orderBy("create_time",false);
        List<Notice> notices = noticeMapper.selectList(wrapper);
        List<NewNoticeVo> newNoticeVos = new ArrayList<>();
        for (Notice notice : notices) {
            NewNoticeVo newNoticeVo = new NewNoticeVo();
            newNoticeVo.setNotice(notice);
            newNoticeVo.setNoticeType(notice.getNoticeType());
            EntityWrapper<Notice> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("to_user_id",ShiroUtil.getLoginUserId());
            wrapper1.eq("notice_type",notice.getNoticeType());
            wrapper1.eq("look_over",2);
            int noRead = noticeMapper.selectCount(wrapper1);
            newNoticeVo.setNoRead(noRead);
            newNoticeVos.add(newNoticeVo);
        }
        return newNoticeVos;
    }
}
