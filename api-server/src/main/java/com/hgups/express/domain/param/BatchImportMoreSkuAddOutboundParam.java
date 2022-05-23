package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/12/29-20:35
 */
@Data
public class BatchImportMoreSkuAddOutboundParam {

    List<ImportMoreSkuWarehouseInfoParam> batchImportProductParamList;

}
