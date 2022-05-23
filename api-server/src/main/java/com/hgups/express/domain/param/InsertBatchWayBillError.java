package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/6/30 0030-18:18
 */
@Data
public class InsertBatchWayBillError {

    private String errorLocation;
    private String errorMessage;
    private String columName;
    private Integer errorIndex;
    private Integer errorCode;

    public String getColumName(int i){
        switch (i){
            case 1:
                this.columName = "姓名";
                break;
            case 2:
                this.columName = "公司";
                break;
            case 3:
                this.columName = "国家";
                break;
            case 4:
                this.columName = "州/省";
                break;
            case 5:
                this.columName = "城市";
                break;
            case 6:
                this.columName = "主要地址";
                break;
            case 7:
                this.columName = "门牌号";
                break;
            case 8:
                this.columName = "邮编一";
                break;
            case 9:
                this.columName = "电话所属国家";
                break;
            case 10:
                this.columName = "手机号";
                break;
            case 11:
                this.columName = "重量";
                break;
            case 12:
                this.columName = "长度";
                break;
            case 13:
                this.columName = "宽度";
                break;
            case 14:
                this.columName = "高度";
                break;
            case 15:
                this.columName = "英文包裹描述";
                break;
            case 16:
                this.columName = "包裹形状";
                break;
            case 17:
                this.columName = "物品类型";
                break;
            case 18:
                this.columName = "其他说明";
                break;
            case 19:
                this.columName = "是否为规则长方体";
                break;
            case 20:
                this.columName = "是否为软包裹";
                break;
            case 21:
                this.columName = "备注一";
                break;
            case 22:
                this.columName = "备注二";
                break;
            case 23:
                this.columName = "中文名称";
                break;
            case 24:
                this.columName = "英文名称";
                break;
            case 25:
                this.columName = "单位价格";
                break;
            case 26:
                this.columName = "单位重量";
                break;
            case 27:
                this.columName = "数量";
                break;
            case 28:
                this.columName = "产地";
                break;
            case 29:
                this.columName = "HS编码";
                break;
            case 30:
                this.columName = "申报要素";
                break;
            default:
                this.columName = "未知列";
                break;
        }
        return this.columName;
    }

}
