package com.hgups.express.domain.param;

import com.hgups.express.domain.User;
import com.hgups.express.domain.WayBill;
import lombok.Data;

/**
 * @author fanc
 * 2020/7/31 0031-17:06
 */
@Data
public class ChangeSingleVo {

    private WayBill wayBill;
    private User user;
}
