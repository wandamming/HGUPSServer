package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.WaybillContact;
import com.hgups.express.domain.dto.CityWaybillSum;
import com.hgups.express.domain.param.AllProvinceWayBillParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author fanc
 * 2020/7/2 0002-11:03
 */
public interface WayBillContactMapper extends BaseMapper<WaybillContact> {

    List<AllProvinceWayBillParam> getWayBillProvince();
    List<AllProvinceWayBillParam> provinceGetCityWayBill(@Param("proName")String proName);
    List<AllProvinceWayBillParam> cityWayBill(@Param("cityName")String cityName);
    List<CityWaybillSum> getCityWayBill();
    int countCityWayBill();
}
