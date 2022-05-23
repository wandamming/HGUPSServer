package com.hgups.express.mapper;

import com.hgups.express.domain.UserAndAccount;

import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/6/15 0015-17:41
 */
public interface UserAndAccountMapper {

    List<UserAndAccount> getUserAndAccount(Map map);
}
