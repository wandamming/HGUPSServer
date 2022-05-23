package com.hgups.express.domain.param;

import lombok.Data;
/**
 * @author lyx
 * 2021/7/21
 */
@Data
public class EntryParam extends UserIdParam {
    //是否隐藏
    private int isHidden;
}
