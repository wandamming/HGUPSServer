package com.hgups.express.domain.param;

import com.hgups.express.domain.Sender;
import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/6/29 0029-9:31
 */
@Data
public class BatchWayBill {
    //批量excel表格信息
    List<BatchWayBillParam> batchWayBill;
    //发件人信息
    private Sender sender;
    //渠道
    private String channel;
    //批量名称
    private String batchName;


}
