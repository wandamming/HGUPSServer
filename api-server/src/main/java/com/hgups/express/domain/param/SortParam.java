package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author lyx
 * 2021/7/27
 */
@Data
public class SortParam {
    //前端传来的id
    private List<Long> ids;

}
