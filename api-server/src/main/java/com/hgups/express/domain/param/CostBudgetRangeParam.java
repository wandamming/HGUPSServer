package com.hgups.express.domain.param;

import com.hgups.express.vo.PageParameters;
import lombok.Data;

/**
 * @author fanc
 * 2020/7/11 0011-18:39
 */
@Data
public class CostBudgetRangeParam extends PageParameters {

    private float weightMin;
    private float weightMax;

}
