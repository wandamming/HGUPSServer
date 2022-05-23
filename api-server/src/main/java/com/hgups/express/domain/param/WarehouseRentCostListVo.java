package com.hgups.express.domain.param;

import com.hgups.express.domain.WarehouseRentCost;
import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/12/10-17:06
 */
@Data
public class WarehouseRentCostListVo {

    //仓租项目表ID
    private Long id;
    //仓租项目名
    private String projectName;

    private List<WarehouseRentCost> warehouseRentCosts;
}
