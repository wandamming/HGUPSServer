package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author wandaming
 * 2021/7/29-13:34
 */
@Data
public class InsertBatchOrderErrorParam {

    private String errorLocation;
    private String errorMessage;
    private String columName;
    private Integer errorIndex;
    private Integer errorCode;

    public String getColumName(int i){
        switch (i){
            case 1:
                this.columName = "平台订单号";
                break;
            case 2:
                this.columName = "所属平台";
                break;
            case 3:
                this.columName = "店铺名称";
                break;
            case 4:
                this.columName = "电商订单号";
                break;
            case 5:
                this.columName = "客户名称";
                break;
            case 6:
                this.columName = "联系电话";
                break;
            case 7:
                this.columName = "订单状态";
                break;
            case 8:
                this.columName = "下单时间";
                break;
            default:
                this.columName = "未知列";
                break;
        }
        return this.columName;
    }

}
