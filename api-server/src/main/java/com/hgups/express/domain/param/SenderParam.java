package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/6/14 0014-10:40
 */
@ApiModel(value = "收件人参数")
@Data
public class SenderParam {

    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "公司")
    private String company;
    @ApiModelProperty(value = "国家")
    private String countries;
    @ApiModelProperty(value = "省份(英)")
    private String provinceEname;
    @ApiModelProperty(value = "省份（中）")
    private String provinceCname;
    @ApiModelProperty(value = "城市(英)")
    private String cityEname;
    @ApiModelProperty(value = "城市(中)")
    private String cityCname;
/*    @ApiModelProperty(value = "县")
    private String prefecture;*/
    @ApiModelProperty(value = "邮政编码前5位")
    private String postalCode;
    @ApiModelProperty(value = "地址一")
    private String addressOne;
    @ApiModelProperty(value = "地址二")
    private String addressTwo;
    @ApiModelProperty(value = "电话")
    private String phone;
    @ApiModelProperty(value = "电话前缀")
    private String phonePrefix;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "是否保存到地址簿")
    private String isSave;
    @ApiModelProperty(value = "邮政编码后4位")
    private String postalCodet;

}
