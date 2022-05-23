package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.UserSacks;

/**
 * @author fanc
 * 2020/7/4 0004-16:01
 */
public interface UserSacksMapper extends BaseMapper<UserSacks> {

    String getSacksNumber(Integer sacksNumber);

}
