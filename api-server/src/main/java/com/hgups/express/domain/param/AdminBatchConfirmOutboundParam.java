package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/12/9-13:29
 */
@Data
public class AdminBatchConfirmOutboundParam {

    List<AdminOutboundParam> adminOutboundParams;

}
