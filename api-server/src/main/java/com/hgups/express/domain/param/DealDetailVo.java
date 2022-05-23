package com.hgups.express.domain.param;

import com.hgups.express.domain.DealDetail;
import lombok.Data;

/**
 * @author fanc
 * 2020/12/9-20:24
 */
@Data
public class DealDetailVo extends DealDetail {
    //打单价格
    private double price;
    //打单重量
    private double billWeight;
    //核重重量
    private double wareWeight;
    //核重价格
    private double warePrice;
}