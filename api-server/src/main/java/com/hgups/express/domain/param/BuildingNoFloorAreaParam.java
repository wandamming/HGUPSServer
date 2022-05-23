package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/9/26 0026-14:14
 */
@Data
public class BuildingNoFloorAreaParam {

    private Long buildingNoId;
    private String buildingNoName;
    private List<FloorAreaParam> buildingNos;

}
