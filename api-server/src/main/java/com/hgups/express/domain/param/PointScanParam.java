package com.hgups.express.domain.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author fanc
 * 2020/11/5-14:57
 */
@Data
public class PointScanParam {
    @NotBlank(message = "单号不能为空")
    private String trackingNumber;
}
