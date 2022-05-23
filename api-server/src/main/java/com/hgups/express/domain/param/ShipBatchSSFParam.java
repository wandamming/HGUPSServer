package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

@Data
public class ShipBatchSSFParam {
    List<Integer> ids;
    boolean ssf;
}
