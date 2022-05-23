package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/11/14-18:27
 */
@Data
public class AddOrUpdateDhlPortContactParam {

    //入境口岸Id
    private Integer id;
    //联系人ID
    private Integer sendId;

}
