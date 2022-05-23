package com.hgups.express.domain.param;

import com.hgups.express.domain.Area;
import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/9/26 0026-14:15
 */
@Data
public class FloorAreaParam {

    private Long floorId;
    private String floorName;
    private List<Area> areas;


}
