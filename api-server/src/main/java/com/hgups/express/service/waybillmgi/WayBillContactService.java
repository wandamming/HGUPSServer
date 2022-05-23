package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.Article;
import com.hgups.express.domain.Province;
import com.hgups.express.domain.WaybillContact;
import com.hgups.express.domain.dto.CityWaybillSum;
import com.hgups.express.domain.param.AllProvinceWayBillParam;
import com.hgups.express.domain.param.CateGoryWayBillParam;
import com.hgups.express.domain.param.ItemsWayBillParam;
import com.hgups.express.domain.param.WayBillKeyValueDataVo;
import com.hgups.express.mapper.WayBillContactMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author fanc
 * 2020/7/2 0002-11:04
 */
@Service
public class WayBillContactService extends ServiceImpl<WayBillContactMapper, WaybillContact> {

    @Resource
    private WayBillContactMapper wayBillContactMapper;
    @Resource
    private ProvinceService provinceService;
    @Resource
    private WayBillService wayBillService;
    @Resource
    private ArticleService articleService;


    //获取州对应运单数量
    public List<AllProvinceWayBillParam> getWaybillContactProvince() {
        List<AllProvinceWayBillParam> wayBillProvince = wayBillContactMapper.getWayBillProvince();

        List<Province> provinces = provinceService.selectList(null);

        for (AllProvinceWayBillParam param : wayBillProvince) {
            String srcName = param.getName();
            //排除那些不按正常"州简写"的方式进行创建的运单
            if(StringUtils.isEmpty(srcName) || 2 != srcName.length()) {
                param.setName("");
                continue;
            }

            param.setName("");
            for (Province province : provinces) {
                String proEname = province.getProEname();//简称
                String proEnglish = province.getProEnglish();//英文全称
                if (proEname.equalsIgnoreCase(srcName)) {
                    param.setName(proEnglish);
                }
            }

        }
        return wayBillProvince;
    }

    //获取传入州的城市对应运单数量
    public List<AllProvinceWayBillParam> provinceGetCityWayBill(String proName) {

        List<Province> provinces = provinceService.selectList(null);
        for (Province province : provinces) {
            String proEname = province.getProEname();//简称
            String proEnglish = province.getProEnglish();//英文全称
            String proChinese = province.getProCname();//中文全称
            if (proEname.equalsIgnoreCase(proName) || proEnglish.equalsIgnoreCase(proName) || proChinese.equalsIgnoreCase(proName)) {
                proName = proEname;
                break;
            }
        }
        return wayBillContactMapper.provinceGetCityWayBill(proName);
    }

    public List<CityWaybillSum> getCityWayBill() {
        return baseMapper.getCityWayBill();
    }

    public int countCityWayBill() {
        return baseMapper.countCityWayBill();
    }

    //获取传入城市获取对应运单数量
    public List<AllProvinceWayBillParam> cityWayBill(String cityName) {
        return wayBillContactMapper.cityWayBill(cityName);
    }


    //获取物品对应运单总价格、总重量
    public List<CateGoryWayBillParam> cateGoryWayBill() {
        List<String> stringList = articleService.categoryGroup();
        List<CateGoryWayBillParam> cateGoryWayBillParams = new ArrayList<>();
        for (String s : stringList) {
            EntityWrapper<Article> wrapper = new EntityWrapper<>();
            wrapper.eq("article_type", s);
            List<Article> articleList = articleService.selectList(wrapper);
            List<Integer> wIds = new ArrayList<>();
            for (Article article : articleList) {
                wIds.add(article.getWaybillId());
            }
            CateGoryWayBillParam cateGoryWayBillParam = new CateGoryWayBillParam();
            Double cateGoryWarePrice = wayBillService.getCateGoryWarePrice(wIds);
            Double cateGoryWareWeight = wayBillService.getCateGoryWareWeight(wIds);
            cateGoryWayBillParam.setCateGory(s);
            cateGoryWayBillParam.setCost(cateGoryWarePrice);
            cateGoryWayBillParam.setWeight(cateGoryWareWeight);
            cateGoryWayBillParams.add(cateGoryWayBillParam);
        }
        return cateGoryWayBillParams;
    }

    //快递类型分析----获取物品对应运单总价格、总重量
    public List<ItemsWayBillParam> itemsWayBill() {
        List<String> stringList = articleService.itemGroup();
        List<ItemsWayBillParam> itemsWayBillParamArrayList = new ArrayList<>();
        for (String s : stringList) {
            if (StringUtils.isEmpty(s)){
                continue;
            }
            EntityWrapper<Article> wrapper = new EntityWrapper<>();
            wrapper.eq("c_describe", s);
            List<Article> articleList = articleService.selectList(wrapper);
            List<Integer> wIds = new ArrayList<>();
            for (Article article : articleList) {
                wIds.add(article.getWaybillId());
            }
            ItemsWayBillParam itemsWayBillParam = new ItemsWayBillParam();
            Double cateGoryWarePrice = wayBillService.getCateGoryWarePrice(wIds);
            Double cateGoryWareWeight = wayBillService.getCateGoryWareWeight(wIds);
            itemsWayBillParam.setName(s);
            itemsWayBillParam.setCost(cateGoryWarePrice);
            itemsWayBillParam.setWeight(cateGoryWareWeight);
            itemsWayBillParamArrayList.add(itemsWayBillParam);
        }
        return itemsWayBillParamArrayList;
    }

    //物品运单类型
    public List<WayBillKeyValueDataVo> wayBillItemData() {
        List<String> stringList = articleService.itemGroup();
        List<WayBillKeyValueDataVo> wayBillKeyValueDataVoArrayList = new ArrayList<>();
        for (String s : stringList) {
            if (StringUtils.isEmpty(s)){
                continue;
            }
            EntityWrapper<Article> wrapper = new EntityWrapper<>();
            wrapper.eq("c_describe", s);
            List<Article> articleList = articleService.selectList(wrapper);
            Set<Integer> wIds = new HashSet<>();
            for (Article article : articleList) {
                wIds.add(article.getWaybillId());
            }
            WayBillKeyValueDataVo wayBillKeyValueDataVo = new WayBillKeyValueDataVo();
            wayBillKeyValueDataVo.setName(s);
            wayBillKeyValueDataVo.setValue(wIds.size());
            wayBillKeyValueDataVoArrayList.add(wayBillKeyValueDataVo);
        }
        return wayBillKeyValueDataVoArrayList;
    }
}
