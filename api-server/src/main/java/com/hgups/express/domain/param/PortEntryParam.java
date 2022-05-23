package com.hgups.express.domain.param;

import com.hgups.express.constant.Constant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/6/20 0020-9:56
 */
@Data
public class PortEntryParam {

    private int id;
    @ApiModelProperty(value = "入境口岸名称")
    private String title;
    @ApiModelProperty(value = "邮政编码")
    private String zipCode;
    @ApiModelProperty(value = "mid")
    private String mid;
    @ApiModelProperty(value = "crid")
    private String crid;
    //入境口岸
    private String type = Constant.USPS_PORT_ALL;

}
