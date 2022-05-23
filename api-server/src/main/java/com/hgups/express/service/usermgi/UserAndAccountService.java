package com.hgups.express.service.usermgi;

import com.hgups.express.domain.UserAndAccount;
import com.hgups.express.mapper.UserAndAccountMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/6/15 0015-19:56
 */
@Service
public class UserAndAccountService{

    @Resource
    private UserAndAccountMapper userAndAccountMapper;

    public List<UserAndAccount> getUserAndAccount(Map map){
        return userAndAccountMapper.getUserAndAccount(map);
    }
}
