package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/6/18 0018-19:54
 */
@Data
public class UserAndAccountParam {
    private Integer current;
    private Integer size;
    private String likes;
    private int state;
}
