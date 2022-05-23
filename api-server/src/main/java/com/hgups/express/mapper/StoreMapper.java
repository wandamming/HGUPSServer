package com.hgups.express.mapper;

import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hgups.express.domain.Store;
import com.hgups.express.domain.param.IdParam;
import com.hgups.express.domain.param.InsertStoreParam;
import com.hgups.express.domain.param.StoreParam;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.vo.StoreVo;

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
public interface StoreMapper extends BaseMapper<Store> {

    List<StoreVo> getStoreList(Pagination pagination, StoreParam param);

    Integer getStoreListCount(StoreParam param);

    void removeAuthorization(IdParam param);

    List<StoreVo> exportStoreList(Map map);

    void insertStore(InsertStoreParam param);

    boolean changeState(IdParam param);

    List<Map<String ,Object>> getStoreName();


}
