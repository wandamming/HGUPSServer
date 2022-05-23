package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.ApiAccount;
import com.hgups.express.domain.param.AddApiAccountParam;
import com.hgups.express.domain.param.PageParam;
import com.hgups.express.mapper.ApiAccountMapper;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author fanc
 * 2021/7/16-10:33
 */

@Service
@Transactional
public class ApiAccountService extends ServiceImpl<ApiAccountMapper,ApiAccount> {

    @Autowired(required = false)
    private ApiAccountMapper apiAccountMapper;


    //name获取API账户信息
    public List<ApiAccount> selectByMap(String uname) {
        Map<String, Object> map = new HashMap<>();
        //增加查询条件,字段需要与数据库表中的字段一致
        map.put("uname", uname);
        return apiAccountMapper.selectByMap(map);

}

    public Page<ApiAccount> getPageList(PageParam pageParam) {
        EntityWrapper<ApiAccount> entityWrapper = new EntityWrapper<>();
        Page<ApiAccount> page = new Page<>();
        page.setCurrent(pageParam.getCurrent());
        page.setSize(pageParam.getSize());
        Page<ApiAccount> apiAccountPage = selectPage(page, entityWrapper);
        page.setTotal(apiAccountPage.getTotal());
        page.setRecords(apiAccountPage.getRecords());
        return page;
    }

    //删除API账户信息
    public boolean deleteApiAccount(Integer id) {
        try {
            apiAccountMapper.deleteApiAccount(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    //添加API账户信息根据用户账号
    public boolean insertApiAccount(AddApiAccountParam apiAccount) {

        apiAccount.setAppToken(RandomStringUtils.randomAlphanumeric(32));
        apiAccount.setAppKey(RandomStringUtils.randomAlphanumeric(32));
        try {
            apiAccountMapper.insertApiAccount(apiAccount);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
