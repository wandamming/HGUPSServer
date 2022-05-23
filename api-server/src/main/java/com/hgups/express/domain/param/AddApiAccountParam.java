package com.hgups.express.domain.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(value = "API账户参数,用于添加api账户")
@Data
public class AddApiAccountParam {

    private String userAccount;
    private String remarks;
    private String uname;
    private String appToken;
    private String appKey;
}
