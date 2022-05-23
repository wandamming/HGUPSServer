package com.hgups.express.domain.param;

import com.hgups.express.vo.PageParameters;
import lombok.Data;

/**
 * @author fanc
 * 2020/6/20 0020-15:51
 */
@Data
public class CityParams extends PageParameters {
    private int provinceId;
}
