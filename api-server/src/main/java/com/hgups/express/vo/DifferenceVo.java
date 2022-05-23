package com.hgups.express.vo;

import lombok.Data;

/**
 * @Author: LZJ
 * @Date: 2021/3/4 0:55
 */
@Data
public class DifferenceVo {
    // 0成功，1运单不存在 -1余额不足 2补扣，3退费
    private int code;
    private double amount;
}
