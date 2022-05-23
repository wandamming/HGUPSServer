package com.hgups.express.domain.param;

import com.hgups.express.domain.PortEntry;
import com.hgups.express.domain.Sender;
import lombok.Data;

/**
 * @author fanc
 * 2020/7/14 0014-15:48
 */
@Data
public class PortParam {


    private PortEntry portEntry;
    private Sender sender;


}
