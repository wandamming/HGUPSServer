package com.hgups.express.mapper;

import com.hgups.express.domain.Wallet;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.param.UserIdParam;
import com.hgups.express.vo.WalletVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lyx
 * @since 2021-07-22
 */
public interface WalletMapper extends BaseMapper<Wallet> {
    WalletVo walletBalance(@Param(value = "userId")Long id);
}
