package com.hgups.express.domain.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author fanc
 * 2020/9/25 0025-17:13
 */
@Data
public class IdLongParam {
    @NotNull(message = "id不能为空")
    private Long id;


}
