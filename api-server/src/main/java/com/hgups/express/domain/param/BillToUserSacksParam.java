package com.hgups.express.domain.param;

import com.hgups.express.vo.PageParameters;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/7/4 0004-16:33
 */
@Data
public class BillToUserSacksParam extends PageParameters {


    @ApiModelProperty(value = "麻袋ID")
    private int sacksId;
    @ApiModelProperty(value = "运单ID集合")
    private List<Integer> WayBillListId;

}
