package com.hgups.express.domain.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: LZJ
 * @Date: 2021/3/4 20:51
 */
@Data
public class PointScanParamV2 {
    @NotNull(message = "运单id不能为空")
    private Long id;
}
