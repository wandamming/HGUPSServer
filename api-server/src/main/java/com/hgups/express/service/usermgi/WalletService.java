package com.hgups.express.service.usermgi;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.Wallet;
import com.hgups.express.domain.param.UserIdParam;
import com.hgups.express.mapper.WalletMapper;
import com.hgups.express.vo.WalletVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WalletService extends ServiceImpl<WalletMapper,Wallet> {

    @Resource
    private WalletMapper walletMapper;

    public WalletVo getWalletBalance(Long id){
        WalletVo walletBalances = walletMapper.walletBalance(id);
        return walletBalances;

    }
}
