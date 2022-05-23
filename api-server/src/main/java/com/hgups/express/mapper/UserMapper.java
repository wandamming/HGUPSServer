package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.User;

import java.util.List;

/**
 * @author fanc
 * 2020/6/11 0011-10:53
 */
public interface UserMapper extends BaseMapper<User> {

    List<User> isUserInfoRepeatPhone(User user);
    List<User> isUserInfoRepeatEmail(User user);

}
