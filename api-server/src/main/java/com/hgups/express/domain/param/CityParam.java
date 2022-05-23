package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/6/19 0019-11:35
 */
@Data
public class CityParam {

    @ApiModelProperty(value = "id")
    private int id;
    @ApiModelProperty(value = "城市名(中)")
    private String cityCname;
    @ApiModelProperty(value = "城市名(英)")
    private String cityEname;
    @ApiModelProperty(value = "省份ID")
    private int provinceId;
    /*@ApiModelProperty(value = "国家ID")
    private int countriesId;*/
}
