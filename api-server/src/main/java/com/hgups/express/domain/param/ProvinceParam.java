package com.hgups.express.domain.param;

import com.baomidou.mybatisplus.annotations.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanc
 * 2020/6/19 0019-11:26
 */
@Data
public class ProvinceParam {

    @TableId("id")
    private int id;
    @ApiModelProperty(value = "省名(中)")
    private String proCname;
    @ApiModelProperty(value = "省名(英简写)")
    private String proEname;
    @ApiModelProperty(value = "省名(英文)")
    private String proEnglish;
    /*@ApiModelProperty(value = "国家ID")
    private int countriesId;*/
}
