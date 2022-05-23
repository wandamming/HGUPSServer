package com.hgups.express.domain.param;

import lombok.Data;

@Data
public class ShippingBatchParam extends PageParam{
    public String spEventState;
    public boolean hasSSF;
    private String port;
}
