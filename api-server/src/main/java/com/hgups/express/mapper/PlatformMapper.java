package com.hgups.express.mapper;

import com.hgups.express.domain.param.IdParam;
import com.hgups.express.domain.Platform;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.param.IdsParam;
import com.hgups.express.domain.param.PlatformAdderssParam;
import com.hgups.express.domain.vo.PlatformVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wdm
 * @since 2021-07-26
 */
public interface PlatformMapper extends BaseMapper<Platform> {

    List<PlatformVo> getPlatform(PlatformAdderssParam param);

    List<Map<String ,Object>> getPlatformName();

}
