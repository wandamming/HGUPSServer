package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

@Data
public class ShipBatchSpStateParam {
    List<Integer> ids;
    String state;
}
