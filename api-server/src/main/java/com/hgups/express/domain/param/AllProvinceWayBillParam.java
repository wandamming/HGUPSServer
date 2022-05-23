package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/9/21 0021-16:47
 */
@Data
public class AllProvinceWayBillParam implements Comparable<AllProvinceWayBillParam>{

    private String name;
    private Integer value;

    @Override
    public int compareTo(AllProvinceWayBillParam o) {
        //倒序
        return o.value-this.value;
    }
}
