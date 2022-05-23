package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.DealDetail;
import com.hgups.express.domain.UserAccount;
import com.hgups.express.domain.WayBill;
import com.hgups.express.exception.MyException;
import com.hgups.express.mapper.UserAccountMapper;
import com.hgups.express.service.usermgi.DealDetailService;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.vo.DifferenceVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author fanc
 * 2020/6/13 0013-18:13
 */
@Service
@Transactional
@Slf4j
public class UserAccountService extends ServiceImpl<UserAccountMapper, UserAccount> {

    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private DealDetailService dealDetailService;


    /**
     * 充值的金额
     *
     * @param price 正数、负数
     */
    public int charging(double price) {
        int code = ResponseCode.FAILED_CODE;
        Long id = ShiroUtil.getLoginUserId();
        UserAccount userAccount = selectById(id);

        if (userAccount != null) {
            userAccount.setBalance(userAccount.getBalance() + price);
            updateById(userAccount);
            code = ResponseCode.SUCCESS_CODE;
        }
        log.info(" charging price: " + price + ", code: " + code);
        return code;
    }

    //运单装入麻袋多退少补
    public DifferenceVo difference(double warePrice, double billPrice, WayBill wayBill) {
        ShiroUtil.getLoginUserId();
        DifferenceVo differenceVo = new DifferenceVo();
        System.out.println("多退少补-->>>>" + warePrice + "----" + billPrice);
        if (warePrice != billPrice) {
            if (null != wayBill) {
                log.info("当前用户Id--->>" + wayBill.getUserId());

                EntityWrapper<UserAccount> wrapper = new EntityWrapper<>();
                wrapper.eq("user_id", wayBill.getUserId());

                //用于防止同一件运单被分拣的时候被扫描多次
                if (warePrice == wayBill.getWarePrice()) {
                    System.out.println("核重比较-->>>>" + warePrice + "----" + wayBill.getWarePrice());
                    log.info("重量相同不需要多退少补---->>>");
                    differenceVo.setCode(0);
                    return differenceVo;
                }

                if (0 == wayBill.getWareWeight()) {
                    if (warePrice > billPrice) {
                        UserAccount userAccount = selectOne(wrapper);
                        log.info("当前用户账户userAccount--->>" + userAccount);
                        double fillBuckle = warePrice - billPrice;
                        if (userAccount.getBalance() - fillBuckle < 0) {
                            differenceVo.setCode(-1); // 1余额不足
                            return differenceVo;
                        }
                        userAccount.setBalance(userAccount.getBalance() - fillBuckle);
                        userAccountMapper.updateById(userAccount);
                        DealDetail dealDetail = new DealDetail();
                        dealDetail.setBalance(userAccount.getBalance());
                        dealDetail.setDealAmount(fillBuckle);
                        dealDetail.setDealType(4);//1:扣费,2：充值,3：退款 4:补扣
                        dealDetail.setState(1);//交易状态
                        dealDetail.setUserId(wayBill.getUserId());
                        dealDetail.setWayBillId(wayBill.getId());
                        dealDetailService.insert(dealDetail);
                        //  补扣记录
                        differenceVo.setCode(2);
                        differenceVo.setAmount(fillBuckle);
                        log.info("交易记录补扣-->", fillBuckle);
                    } else {
                        UserAccount userAccount = selectOne(wrapper);
                        log.info("当前用户账户userAccount--->>" + userAccount);
                        double refund = billPrice - warePrice;
                        userAccount.setBalance(userAccount.getBalance() + refund);
                        userAccountMapper.updateById(userAccount);
                        DealDetail dealDetail = new DealDetail();
                        dealDetail.setBalance(userAccount.getBalance());
                        dealDetail.setDealAmount(refund);
                        dealDetail.setDealType(3);//1:扣费,2：充值,3：退款 4:补扣
                        dealDetail.setState(1);//交易状态
                        dealDetail.setUserId(wayBill.getUserId());
                        dealDetail.setWayBillId(wayBill.getId());
                        dealDetailService.insert(dealDetail);
                        // 退费记录
                        differenceVo.setCode(3);
                        differenceVo.setAmount(refund);
                        log.info("交易记录多退-->", refund);
                    }
                } else {
                    if (warePrice > wayBill.getWarePrice()) {
                        UserAccount userAccount = selectOne(wrapper);
                        log.info("当前用户账户userAccount--->>" + userAccount);
                        double fillBuckle = warePrice - wayBill.getWarePrice();
                        if (userAccount.getBalance() - fillBuckle < 0) {
                            differenceVo.setCode(-1);
                            return differenceVo; //余额不足
                        }
                        userAccount.setBalance(userAccount.getBalance() - fillBuckle);
                        userAccountMapper.updateById(userAccount);
                        DealDetail dealDetail = new DealDetail();
                        dealDetail.setBalance(userAccount.getBalance());
                        dealDetail.setDealAmount(fillBuckle);
                        dealDetail.setDealType(4);//1:扣费,2：充值,3：退款 4:补扣
                        dealDetail.setState(1);//交易状态
                        dealDetail.setUserId(wayBill.getUserId());
                        dealDetail.setWayBillId(wayBill.getId());
                        dealDetailService.insert(dealDetail);
                        // 补扣记录
                        differenceVo.setCode(2);
                        differenceVo.setAmount(fillBuckle);
                        log.info("交易记录补扣-->", fillBuckle);
                    } else {
                        UserAccount userAccount = selectOne(wrapper);
                        log.info("当前用户账户userAccount--->>" + userAccount);
                        double refund = wayBill.getWarePrice() - warePrice;
                        userAccount.setBalance(userAccount.getBalance() + refund);
                        userAccountMapper.updateById(userAccount);
                        DealDetail dealDetail = new DealDetail();
                        dealDetail.setBalance(userAccount.getBalance());
                        dealDetail.setDealAmount(refund);
                        dealDetail.setDealType(3);//1:扣费,2：充值,3：退款 4:补扣
                        dealDetail.setState(1);//交易状态
                        dealDetail.setUserId(wayBill.getUserId());
                        dealDetail.setWayBillId(wayBill.getId());
                        dealDetailService.insert(dealDetail);
                        // 退费记录
                        differenceVo.setCode(3);
                        differenceVo.setAmount(refund);
                        log.info("交易记录多退-->", refund);
                    }
                }
            } else {
                differenceVo.setCode(1);
                return differenceVo;//运单不存在
            }
        }
        return differenceVo;//成功
    }
}
