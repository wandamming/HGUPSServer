package com.hgups.express.domain.param;

import com.baomidou.mybatisplus.annotations.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/8/19 0019-20:24
 */
@Data
public class SacksIntoShippingBatchVo {


    @TableId("id")
    private int id;
    @ApiModelProperty(value = "包裹数量")
    private int parcelNumber;
    @ApiModelProperty(value = "麻袋状态")
    private String state;
    @ApiModelProperty(value = "麻袋打单重量")
    private double billWeight;
    @ApiModelProperty(value = "麻袋核重重量")
    private double wareWeight;
    @ApiModelProperty(value = "麻袋服务")
    private String service;
    @ApiModelProperty(value = "入境口岸")
    private String entrySite;
    @ApiModelProperty(value = "创建人")
    private String creator;
    @ApiModelProperty(value = "麻袋追踪号码")
    private String sacksNumber;
    @ApiModelProperty(value = "打单总金额")
    private double sumPrice;
    @ApiModelProperty(value = "关闭麻袋输入重量")
    private double closeWeight;
    @ApiModelProperty(value = "核重总金额")
    private double warePrice;
    @ApiModelProperty(value = "渠道")
    private String channel;


}
