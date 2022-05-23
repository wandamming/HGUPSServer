package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/11/14-18:11
 */
@Data
public class AddOrUpdateDhlPortParam {

    //入境口岸ＩＤ
    private long id;
    @ApiModelProperty(value = "入境口岸名称")
    private String portName;
    @ApiModelProperty(value = "入境口岸邮政编码")
    private String zipCode;
    //DHL--POCKUP
    private String dhlPickup;
    //DHL--ACCOUNT
    private String dhlAccount;
    //HGUPS入境口岸ID
    private Integer hgupsPortId;
    //配送中心代码
    private String distributionCenterCode;



}
