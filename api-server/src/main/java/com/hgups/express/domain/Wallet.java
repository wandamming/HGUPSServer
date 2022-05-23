package com.hgups.express.domain;

import com.baomidou.mybatisplus.enums.IdType;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author lyx
 * @since 2021-07-22
 */
@TableName("wallet")
public class Wallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("balance_platform")
    private BigDecimal balancePlatform;
    @TableField("balance_freight")
    private BigDecimal balanceFreight;
    @TableField("user_id")
    private Integer userId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getBalancePlatform() {
        return balancePlatform;
    }

    public void setBalancePlatform(BigDecimal balancePlatform) {
        this.balancePlatform = balancePlatform;
    }

    public BigDecimal getBalanceFreight() {
        return balanceFreight;
    }

    public void setBalanceFreight(BigDecimal balanceFreight) {
        this.balanceFreight = balanceFreight;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Wallet{" +
        ", id=" + id +
        ", balancePlatform=" + balancePlatform +
        ", balanceFreight=" + balanceFreight +
        ", userId=" + userId +
        "}";
    }
}
