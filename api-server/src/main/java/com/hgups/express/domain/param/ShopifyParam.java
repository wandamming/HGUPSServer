package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wandaming
 * 2021/7/24-17:10
 */
@Data
public class ShopifyParam {
    @ApiModelProperty(value = "平台后台二级域名")
    String subDomain;

}
