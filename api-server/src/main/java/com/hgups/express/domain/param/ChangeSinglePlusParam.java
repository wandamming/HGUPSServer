package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/8/1 0001-18:42
 */
@Data
public class ChangeSinglePlusParam {

    private double wareWeight;
    private Integer id;
    private String flag;//重量：1 入境口岸：2

}
