package com.hgups.express.domain.param;

import lombok.Data;

/**
 * @author fanc
 * 2020/9/22 0022-17:20
 */
@Data
public class PortLateListVo {

    private Long id;
    //入境口岸名称
    private String portLateName;
    //入境口岸状态
    private Integer lateState;

}
