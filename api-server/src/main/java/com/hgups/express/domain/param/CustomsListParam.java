package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author fanc
 * 2020/6/20 0020-14:03
 */
@Data
public class CustomsListParam {
    @ApiModelProperty(value = "运单id")
    private List wids = new ArrayList();
    @ApiModelProperty(value = "发件人信息")
    private SenderParam senderParam;
    @ApiModelProperty(value = "MAWB")
    private String mawb;
    @ApiModelProperty(value = "航班号")
    private String flightNo;
    @ApiModelProperty(value = "收件人城市")
    private String receiveCity;
    @ApiModelProperty(value = "收件人城市")
    private Date startTime;


}
