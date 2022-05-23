package com.hgups.express.controller.waybillmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.domain.City;
import com.hgups.express.domain.Province;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.CityParam;
import com.hgups.express.domain.param.CityParams;
import com.hgups.express.domain.param.ProvinceParam;
import com.hgups.express.service.waybillmgi.CityService;
import com.hgups.express.service.waybillmgi.PlaceService;
import com.hgups.express.service.waybillmgi.ProvinceService;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.vo.PageParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/6/10 0010-10:42
 */
@RestController
@Api(description = "地点相关API")
@RequestMapping("/place")
public class PlaceController {

    /*@Resource
    private CountriesService countriesService;*/
    @Resource
    private ProvinceService provinceService;
    @Resource
    private CityService cityService;
    @Resource
    private PlaceService placeService;

    @PostMapping("/setProvince")
    @ApiOperation(value = "添加省API")
    public Response setProvince(@RequestBody ProvinceParam param){
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("pro_cname",param.getProCname())
                .or()
                .eq("pro_ename",param.getProEname());
        Province p = provinceService.selectOne(wrapper);
        if(p!=null){
            return new Response(131,"省/州已存在",null);
        }
        Province province = DomainCopyUtil.map(param, Province.class);
        province.setCountriesId(2); // 默认美国
        boolean insert = provinceService.insert(province);
        if(insert){
            return new Response(200,"添加成功",null);
        }
        return new Response(130,"添加失败",null);
    }

    @PostMapping("/updateProvince")
    @ApiOperation(value = "修改省API")
    public Response updateProvince(@RequestBody ProvinceParam param){

        Province province = DomainCopyUtil.map(param, Province.class);
        province.setCountriesId(2); // 默认美国
        boolean update = provinceService.updateById(province);
        if(update){
            return new Response(200,"修改成功",null);
        }
        return new Response(130,"修改失败",null);
    }

    @PostMapping("/getProvince")
    @ApiOperation(value = "查看省/州信息")
    public Response getProvince(@RequestBody PageParameters parameters){

        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("countries_id",2);
        Page<Province> page = new Page<>(parameters.getCurrent(),parameters.getSize());
        Page<Province> pageList = provinceService.selectMapsPage(page,wrapper);

        List<Province> plist = pageList.getRecords();
        Map map = new HashMap();
        int total = provinceService.selectCount(wrapper);//总页数
        map.put("total",total);
        map.put("pages",(total%parameters.getSize())==0?total/parameters.getSize():total/parameters.getSize()+1);//总条数
        map.put("records",plist);
        return new Response(map);
    }


    @PostMapping("/deleteProvince")
    @ApiOperation(value = "删除省API")
    public Response delete(@RequestBody ProvinceParam param){
        boolean flag = placeService.deleteProvince(param.getId());
        if (flag) {
            return new Response(200, "删除成功", null);
        }
        return new Response(130, "删除失败", null);

    }

    @PostMapping("/getCity")
    @ApiOperation(value = "查看省对应城市")
    public Response setCity(@RequestBody CityParams city){
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("province_id",city.getProvinceId());
        Page<City> page = new Page<>(city.getCurrent(),city.getSize());
        Page<City> pageList = cityService.selectMapsPage(page,wrapper);
        List<City> clist= pageList.getRecords();
        Map map = new HashMap();
        int total = cityService.selectCount(wrapper);//总页数
        map.put("total",total);
        map.put("pages",(total%city.getSize())==0?total/city.getSize():total/city.getSize()+1);//总条数
        map.put("records",clist);
        return new Response(map);
    }

    @PostMapping("/updateCity")
    @ApiOperation(value = "修改城市API")
    public Response updateCity(@RequestBody CityParam param){
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("province_id",param.getProvinceId());
        wrapper.andNew().eq("city_cname",param.getCityCname())
                .or()
                .eq("city_ename",param.getCityEname());
        City c = cityService.selectOne(wrapper);
        if(c!=null){
            return new Response(131,"城市已存在",null);
        }
        City city = DomainCopyUtil.map(param, City.class);
        city.setCountriesId(2); // 默认美国
        boolean update = cityService.updateById(city);
        if(update){
            return new Response(200,"修改成功",null);
        }
        return new Response(130,"修改失败",null);
    }


    @PostMapping("/setCity")
    @ApiOperation(value = "添加城市API")
    public Response setCity(@RequestBody CityParam param){

        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("province_id",param.getProvinceId());
        wrapper.andNew().eq("city_cname",param.getCityCname())
               .or()
               .eq("city_ename",param.getCityEname());
        City c = cityService.selectOne(wrapper);
        if(c!=null){
            return new Response(131,"城市已存在",null);
        }

        City city = DomainCopyUtil.map(param, City.class);
        city.setCountriesId(2); // 默认美国
        boolean insert = cityService.insert(city);
        if(insert){
            return new Response(200,"添加成功",null);
        }
        return new Response(130,"添加失败",null);
    }

    @PostMapping("/deleteCity")
    @ApiOperation(value = "删除城市API")
    public Response deleteCity(@RequestBody CityParam param){
        boolean insert = cityService.deleteById(param.getId());
        if(insert){
            return new Response(200,"删除成功",null);
        }
        return new Response(130,"删除失败",null);
    }



    @PostMapping("getPlace")
    @ApiOperation(value = "获取地点API")
    public Response getPlace(){

        List<Province> province = provinceService.selectList(null);
        List<Province> prlist = new ArrayList<>();
        for (Province pro:province){
            Province provin = new Province();
            provin.setProCname(pro.getProCname());
            provin.setProEnglish(pro.getProEnglish());
            provin.setProEname(pro.getProEname());
            provin.setCountriesId(pro.getCountriesId());
            provin.setId(pro.getId());

            EntityWrapper wrapper2 = new EntityWrapper();
            wrapper2.eq("province_id",pro.getId());
            List<City> city = cityService.selectList(wrapper2);
            List<City> cityList = new ArrayList<>();
            for (City c:city){
                City city1 = new City();
                city1.setId(c.getId());
                city1.setCityCname(c.getCityCname());
                city1.setCityEname(c.getCityEname());
                city1.setCountriesId(c.getCountriesId());
                city1.setProvinceId(c.getProvinceId());

                cityList.add(city1);
            }

            provin.setChildren(cityList);
            prlist.add(provin);
        }

        return new Response(prlist);


        /*
        *
        *   国家、省/州、城市，三级联动
        *
        */
        /*Integer id = 2; //默认美国
        Countries countries = countriesService.selectById(id);

        EntityWrapper wrapper1 = new EntityWrapper();
        wrapper1.eq("countries_id",id);
        List<Province> province = provinceService.selectList(wrapper1);
        List<Province> prlist = new ArrayList<>();
        for (Province pro:province){
            Province provin = new Province();
            provin.setProCname(pro.getProCname());
            provin.setProEname(pro.getProEname());
            provin.setCountriesId(pro.getCountriesId());
            provin.setId(pro.getId());

            EntityWrapper wrapper2 = new EntityWrapper();
            wrapper2.eq("province_id",pro.getId());
            List<City> city = cityService.selectList(wrapper2);
            List<City> cityList = new ArrayList<>();
            for (City c:city){
                City city1 = new City();
                city1.setId(c.getId());
                city1.setCityCname(c.getCityCname());
                city1.setCityEname(c.getCityEname());
                city1.setCountriesId(c.getCountriesId());
                city1.setProvinceId(c.getProvinceId());

               cityList.add(city1);
            }

            provin.setChildren(cityList);
            prlist.add(provin);
        }

        countries.setChildren(prlist);
        return new Response(countries);*/
    }
}
