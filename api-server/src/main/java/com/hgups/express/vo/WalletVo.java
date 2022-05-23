package com.hgups.express.vo;


import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lyx
 * 2021/7/20
 */
@Data
public class WalletVo {

    //平台余额
    private BigDecimal balancePlatform;
    //货代余额
    private BigDecimal balanceFreight;
}
