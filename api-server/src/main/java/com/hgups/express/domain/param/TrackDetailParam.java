package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/8/6 0006-9:42
 */
@Data
public class TrackDetailParam {

    private String arrivalTime;//到达时间
    private String arrivalAddress;//到达地点
    private String receivingState;//包裹状态

}
