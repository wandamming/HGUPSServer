package com.hgups.express.domain.param;

import com.hgups.express.domain.PointScan;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: LZJ
 * @Date: 2021/3/3 13:37
 */
@Data
public class SortPointScanParam {
    @NotNull(message = "过点扫描ID不能为空")
    private Long id;
    @NotNull(message = "过点扫描序号不能为空")
    @Min(value = 1, message = "不能将过点排到第一位")
    private Integer rank;
    @NotEmpty(message = "过点排序列表不能为空")
    List<PointScan> pointScanList;
}
