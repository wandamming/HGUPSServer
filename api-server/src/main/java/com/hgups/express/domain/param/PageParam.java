package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/7/6 0006-16:55
 */
@Data
public class PageParam {
    @ApiModelProperty(value = "当前页码数")
    private Integer current;
    @ApiModelProperty(value = "一页里面的数据条数")
    private Integer size;

}
