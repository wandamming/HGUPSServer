package com.hgups.express.controller.waybillmg;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.poi.excel.ExcelUtil;
import com.google.common.collect.Lists;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.Zone;
import com.hgups.express.domain.param.ZoneDto;
import com.hgups.express.service.waybillmgi.ZoneService;
import com.hgups.express.util.ShiroUtil;
import com.jpay.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanc
 * 2020/7/13 0013-15:57
 */
@RestController
@Slf4j
@RequestMapping("/zone")
@Api(description = "入境口岸Zone")
public class ZoneController {

    @Resource
    private ZoneService zoneService;


    //获取zone最小测试
    @ApiOperation(value = "根据邮编获取对应zone")
    @GetMapping("/getPortEntryZone")
    public Response getPortEntryZone(@RequestParam String code){
        Long loginUserId = ShiroUtil.getLoginUserId();
        ZoneDto zoneDto = zoneService.calculateZone(code,loginUserId);
        return new Response(200,".....",zoneDto);
    }

    @ApiOperation(value = "导入zoneExcel")
    @PostMapping("/importZone")
    public Response importZone(MultipartFile excelFile){
        if (excelFile == null) {
            return new Response(600,"Excel文件丢失",null);
        }
        StringBuilder errorMsg = new StringBuilder();
        try {
            List<List<Object>> lines = ExcelUtil.getReader(excelFile.getInputStream()).read();
            List<Zone> insertEntityList = Lists.newArrayList();
            for (int i = 0; i < lines.size(); i++) {
                List<Object> line = lines.get(i);
                int index = 0;
                try {
                    String zipCode = line.get(index++).toString();
                    String zone = line.get(index++).toString();
                    int portEntryId = Integer.parseInt(line.get(index++).toString());

                    Zone excelData = new Zone();
                    excelData.setZipCode(zipCode);
                    excelData.setZone(zone);
                    excelData.setPortEntryId(portEntryId);

                    insertEntityList.add(excelData);

                } catch (ArrayIndexOutOfBoundsException e) {
                    errorMsg.append(String.format("第%s行只有%s列", i + 1, index)).append("\n");
                } catch (Exception e) {
                    errorMsg.append(String.format("第%s行第%s列解析失败", i + 1, index)).append("\n");
                }
            }

            if (CollectionUtil.isNotEmpty(insertEntityList)) {
                zoneService.insertBatch(insertEntityList);
            }
        } catch (Exception e) {
            log.error("parse error:", e);
            return new Response(601,"文件解析异常，请检查excel文件格式是否正确!",null);
        }

        if (StringUtils.isNotBlank(errorMsg.toString())) {
            return new Response(602,"文件解析异常，请检查excel文件格式是否正确!具体错误信息:" + errorMsg.toString(),null);
        }
        return new Response(200,"导入成功",null);

    }




}
