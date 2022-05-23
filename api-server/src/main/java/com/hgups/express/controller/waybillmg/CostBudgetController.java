package com.hgups.express.controller.waybillmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.domain.Config;
import com.hgups.express.domain.CostBudget;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.CostBudgetParam;
import com.hgups.express.domain.param.CostBudgetRangeParam;
import com.hgups.express.domain.param.ParamId;
import com.hgups.express.service.usermgi.ConfigService;
import com.hgups.express.service.waybillmgi.CostBudgetService;
import com.hgups.express.util.DomainCopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/6/20 0020-18:58
 */
@Api(description = "价格表API")
@Slf4j
@RestController
@RequestMapping("/costBudget")
public class CostBudgetController {

    @Resource
    private CostBudgetService costBudgetService;
    @Resource
    private ConfigService configService;


    @ApiOperation("添加价格")
    @PostMapping("/setCostBudget")
    public Response setCostBudget(@RequestBody CostBudget costBudget){
        Response response = new Response();
        boolean insert = costBudgetService.insert(costBudget);
        if(insert){
            return new Response(200,"添加成功",null);
        }
        return new Response(130,"添加失败",null);
    }

    @ApiOperation(value = "删除价格")
    @PostMapping("/deleteCostBudget")
    public Response deleteCostBudget(@RequestBody ParamId param){
        boolean delete = costBudgetService.deleteById(param.getId());
        if(delete){
            return new Response(200,"删除成功",null);
        }
        return new Response(130,"删除失败",null);
    }

    @ApiOperation(value = "修改价格")
    @PostMapping("/updateCostBudget")
    public Response updateCostBudget(@RequestBody CostBudget costBudget){
        boolean insert = costBudgetService.updateById(costBudget);
        if(insert){
            return new Response(200,"修改成功",null);
        }
        return new Response(130,"修改失败",null);
    }


    //支持区间搜索
    @ApiOperation(value = "获取价格")
    @PostMapping("/getCostBudget")
    public Response getCostBudget(@RequestBody CostBudgetRangeParam param){
        Response response = new Response();
        DecimalFormat keepDecimal = new DecimalFormat("#.00");
        float weightMin = param.getWeightMin();
        float weightMax = param.getWeightMax();
        //DecimalFormat keepDecimal = new DecimalFormat("#.##########");
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.orderBy("weight",true);
        if (weightMin>0){
            wrapper.ge("weight",weightMin);
        }
        if (weightMax>weightMin&&weightMax>0){
            wrapper.le("weight",weightMax);
        }
        Page<CostBudget> page = new Page<>(param.getCurrent(),param.getSize());
        Page<CostBudget> pageList = costBudgetService.selectPage(page,wrapper);
        List<CostBudget> cList = pageList.getRecords();

        List<Config> configs = configService.selectList(null);
        float chainPrice = Float.parseFloat(configs.get(0).getV());//报关单价
        float aviationPrice = Float.parseFloat(configs.get(1).getV());//航空单价
        float exchangeRate = Float.parseFloat(configs.get(2).getV());//汇率


        List<CostBudgetParam> costParams = new ArrayList<>();
        for (CostBudget cost:cList){
            CostBudgetParam cParam = DomainCopyUtil.map(cost, CostBudgetParam.class);
            float weight = cParam.getWeight();
            float chainPriceSum = weight*chainPrice/1000;

            float aviationPriceSum = weight*aviationPrice/1000;
            float sum = Float.parseFloat(keepDecimal.format(chainPriceSum+aviationPriceSum+((cParam.getAmericaSendPrice()+cParam.getAmericaPrice())*exchangeRate)));
            cParam.setChainPrice(chainPriceSum);
            cParam.setAviationPrice(aviationPriceSum);
            cParam.setSumPrice(sum);
            costParams.add(cParam);
        }
        Map<Object,Object> map = new HashMap<Object, Object>();
        int total = costBudgetService.selectCount(wrapper);//总条数
        map.put("current",param.getCurrent());
        map.put("total",total);
        map.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);//总页数
        map.put("records",costParams);
        response.setStatusCode(200);
        response.setData(map);
        return response;
    }


   /* @ApiOperation("价格Excel导入API")
    @PostMapping(value = "/scoreFormula")
    public Response scoreFormulaImport(MultipartFile excelFile) {
        if (excelFile == null) {
            return new Response(600,"Excel文件丢失",null);
        }
        StringBuilder errorMsg = new StringBuilder();
        try {
            List<List<Object>> lines = ExcelUtil.getReader(excelFile.getInputStream()).read();
            List<CostBudget> insertEntityList = Lists.newArrayList();
            //DecimalFormat keepDecimal = new DecimalFormat("0.00");
            DecimalFormat keepDecimal = new DecimalFormat("#.##########");
            for (int i = 0; i < lines.size(); i++) {
                List<Object> line = lines.get(i);
                int index = 0;
                try {
                    Long weight = Long.parseLong(line.get(index++).toString());
                    Float chainPrice = Float.parseFloat(keepDecimal.format(line.get(index++)));
                    Float aviationPrice = Float.parseFloat(keepDecimal.format(line.get(index++)));
                    Float americaSendPrice = Float.parseFloat(keepDecimal.format(line.get(index++)));
                    Float americaPrice = Float.parseFloat(keepDecimal.format(line.get(index++)));
                    Float sumPrice = Float.parseFloat(keepDecimal.format(line.get(index)));

                    CostBudget excelData = new CostBudget();
                    excelData.setWeight(weight);
                    excelData.setChainPrice(chainPrice);
                    excelData.setAviationPrice(aviationPrice);
                    excelData.setAmericaSendPrice(americaSendPrice);
                    excelData.setAmericaPrice(americaPrice);
                    excelData.setSumPrice(sumPrice);
                    insertEntityList.add(excelData);

                } catch (ArrayIndexOutOfBoundsException e) {
                    errorMsg.append(String.format("第%s行只有%s列", i + 1, index)).append("\n");
                } catch (Exception e) {
                    errorMsg.append(String.format("第%s行第%s列解析失败", i + 1, index)).append("\n");
                }
            }

            if (CollectionUtil.isNotEmpty(insertEntityList)) {
                costBudgetService.insertBatch(insertEntityList);
            }
        } catch (Exception e) {
            log.error("parse error:", e);
            return new Response(601,"文件解析异常，请检查excel文件格式是否正确!",null);
        }

        if (StringUtils.isNotBlank(errorMsg.toString())) {
            return new Response(602,"文件解析异常，请检查excel文件格式是否正确!具体错误信息:" + errorMsg.toString(),null);
        }
        return new Response(200,"导入成功",null);
    }*/


}
