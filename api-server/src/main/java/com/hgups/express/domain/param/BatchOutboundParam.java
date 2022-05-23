package com.hgups.express.domain.param;

import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

@Data
public class BatchOutboundParam {
    private List<Integer> ids;
    private int outboundType;

}
