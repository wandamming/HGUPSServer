package com.hgups.express.domain.param;

import com.baomidou.mybatisplus.annotations.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/6/14 0014-11:01
 */
@Data
@ApiModel(value = "物品参数")
public class ArticleParam {

    @ApiModelProperty(value = "物品描述（中文）")
    private String cDescribe;
    @ApiModelProperty(value = "物品描述（英文）")
    private String eDescribe;
    @ApiModelProperty(value = "价格")
    private double price;
    @ApiModelProperty(value = "重量")
    private double weight;
    @ApiModelProperty(value = "数量")
    private int number;
    @ApiModelProperty(value = "产地")
    private String place;

}
