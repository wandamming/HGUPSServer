package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

@Data
public class StatusParam {
    private List<Long> ids;
    private Boolean isShow;
}
