package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/9/12 0012-10:05
 */
@Data
public class UpdateWeightByIdParam {

    //运单ID
    private Integer id;
    //运单重量
    private Double weight;

}
