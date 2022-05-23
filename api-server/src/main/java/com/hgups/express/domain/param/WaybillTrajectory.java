package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/8/5 0005-16:33
 */
@Data
public class WaybillTrajectory {

    private List<ChangeTrackingNumberParam> trackingNumbers;

}
