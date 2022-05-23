package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/9/26 0026-11:23
 */
@Data
public class AuditInventoryParam {

    private Long id;
    private Long buildingNoId;
    //楼层ID
    private Long floorId;
    //区域ID
    private Long areaId;
    //每个sku产品对应的到货情况
    private List<SumParam> sumParamList;

    //为了接口不报错，先预留着
    //已到数量
    private Integer arrive;
    //未到数量
    private Integer noArrive;
    //合格数量
    private Integer qualified;
    //不合格数量
    private Integer noQualified;
    //楼号ID

    @Data
    public static class SumParam {
        private int id;
        //已到数量
        private Integer arrive;
        //未到数量
        private Integer noArrive;
        //合格数量
        private Integer qualified;
        //不合格数量
        private Integer noQualified;
        //楼号ID
    }

}
