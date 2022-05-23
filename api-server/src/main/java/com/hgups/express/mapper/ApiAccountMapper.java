package com.hgups.express.mapper;


import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hgups.express.domain.ApiAccount;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.param.AddApiAccountParam;
import com.hgups.express.domain.param.PageParam;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;
import sun.security.pkcs11.wrapper.Constants;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wandaming
 * @since 2021-07-16
 */
public interface ApiAccountMapper extends BaseMapper<ApiAccount> {

    void deleteApiAccount(@Param("id") Integer id);

    void insertApiAccount(AddApiAccountParam apiAccount);

}
