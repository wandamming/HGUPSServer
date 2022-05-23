package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/12/29-20:53
 */
@Data
public class BatchImportProductParam {


    //下标
    private Integer indexId;

    //shi否校验地址
    private boolean checkAddress;

    //产品ID
    private Long producerId;
    //出库数量
    private Integer producerNumber;

    /**
     *  收件人信息
     */
    //姓名
    private String name;
    //公司
    private String company;
    //国家
    private String countries;
    //省份(英)
    private String provinceEname;
    //城市(英)
    private String cityEname;
    //邮政编码一
    private String postalCode;
    //邮政编码二
    private String postalCodet;
    //门牌号
    private String addressOne;
    //主要地址
    private String addressTwo;
    //电话
    private String phone;
    //电话前缀
    private String phonePrefix;
    //邮箱
    private String email;

}
