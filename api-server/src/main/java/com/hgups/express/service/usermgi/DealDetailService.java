package com.hgups.express.service.usermgi;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.DealDetail;
import com.hgups.express.domain.UserAccount;
import com.hgups.express.mapper.DealDetailMapper;
import com.hgups.express.service.waybillmgi.UserAccountService;
import com.hgups.express.util.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author fanc
 * 2020/6/14 0014-13:38
 */
@Service
@Slf4j
public class DealDetailService extends ServiceImpl<DealDetailMapper,DealDetail> {

    @Autowired
    private UserAccountService userAccountService;

    /**
     * 生成退款记录
     */
    public int refund(double price) {
        //给账户充值
        int code = ResponseCode.FAILED_CODE;
        Long id = ShiroUtil.getLoginUserId();
        UserAccount userAccount = userAccountService.selectById(id);

        if(userAccount != null) {
            DealDetail dealDetail = new DealDetail();
            dealDetail.setBalance(userAccount.getBalance());
            dealDetail.setDealAmount(price);
            dealDetail.setDealType(DealDetail.TYPE_REFUND);
            dealDetail.setState(DealDetail.STATE_OK);
            dealDetail.setUserId(id);
            code = ResponseCode.SUCCESS_CODE;
        }
        log.info(" refund price: " + price + ", code: " + code);
        return code;
    }

}
