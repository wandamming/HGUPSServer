package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.ZoneDto;
import com.hgups.express.mapper.ZoneMapper;
import com.hgups.express.service.usermgi.PortLateDhlService;
import com.hgups.express.service.usermgi.PortLateService;
import com.hgups.express.service.usermgi.RightsManagementService;
import com.hgups.express.util.ShiroUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fanc
 * 2020/7/13 0013-15:55
 */
@Service
public class ZoneService extends ServiceImpl<ZoneMapper,Zone> {

    @Resource
    private ZoneMapper zoneMapper;
    @Resource
    private PortEntryService portEntryService;
    @Resource
    private DhlPortService dhlPortService;
    @Resource
    private RightsManagementService rightsManagementService;
    @Resource
    private PortLateService portLateService;
    @Resource
    private PortLateDhlService portLateDhlService;


    //根据收件人邮编查询DHL对应口岸与zone
    public ZoneDto calculateDHLZone(String receiveCode,Long userId){
        String subCode = receiveCode.substring(0, 3);
        int rCode = Integer.parseInt(subCode);
        List<ZoneDto> zoneDtoList = new ArrayList<>();
        //查询可用入境口岸
        EntityWrapper<DhlPort> wrapper = new EntityWrapper<>();
        wrapper.eq("state",1);
        wrapper.eq("fake_delete",0);
        int processRole = rightsManagementService.isProcessRole(userId);
        //如果是后程用户则进一步塞选入境口岸
        if (processRole==1){
            EntityWrapper<PortLateDhl> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("port_dhl_state",1);
            wrapper1.eq("late_dhl_state",1);
            List<PortLateDhl> portLateDhlList = portLateDhlService.selectList(wrapper1);
            List<Long> portIds = new ArrayList<>();
            for (PortLateDhl portLatedhl : portLateDhlList) {
                portIds.add(portLatedhl.getPortId());
            }
            if (portIds.size()<=0){
                return null;
            }
            wrapper.in("id",portIds);
        }
        //----后称用户塞选结束---

        List<Integer> portIds = new ArrayList<>();
        List<DhlPort> portList = dhlPortService.selectList(wrapper);
        for (DhlPort dhlPort:portList){
            portIds.add(dhlPort.getHgupsPortId());
        }
        //查询开放入境口岸的zone
        EntityWrapper<Zone> wrapper1 = new EntityWrapper<>();
        wrapper1.in("port_entry_id", portIds);
        List<Zone> zones = zoneMapper.selectList(wrapper1);
        for (Zone zone : zones){
            ZoneDto zoneDto = new ZoneDto();
            String zipCode = zone.getZipCode();
            if(zipCode.indexOf("---") >= 0){
                String[] zipCodeStr = zipCode.split("---");
                int zipMin = Integer.parseInt(zipCodeStr[0]);
                int zipMax = Integer.parseInt(zipCodeStr[1]);
                if (rCode>=zipMin&&rCode<=zipMax){
                    zoneDto.setPortEntryId(zone.getPortEntryId());
                    zoneDto.setZone(zone.getZone());
                    for (DhlPort dhlPort:portList){
                        if (zone.getPortEntryId()==dhlPort.getHgupsPortId()){
                            zoneDto.setPortEntryName(dhlPort.getPortName());
                            zoneDtoList.add(zoneDto);
                            break;
                        }
                    }
                }
            }else {
                if (rCode== Integer.parseInt(zipCode)){
                    zoneDto.setPortEntryId(zone.getPortEntryId());
                    zoneDto.setZone(zone.getZone());
                    for (DhlPort dhlPort:portList){
                        if (zone.getPortEntryId()==dhlPort.getHgupsPortId()){
                            zoneDto.setPortEntryName(dhlPort.getPortName());
                            zoneDtoList.add(zoneDto);
                            break;
                        }
                    }
                }
            }
        }

        //计算最近距离入境口岸的zone
        if (zoneDtoList.size()>0){
            int zoneMin = Integer.parseInt(zoneDtoList.get(0).getZone().substring(0, 1));
            String PortEntryNameMin = zoneDtoList.get(0).getPortEntryName();
            int PortEntryIdMin = zoneDtoList.get(0).getPortEntryId();
            for (int i = 1;i<zoneDtoList.size();i++){
                int zone = Integer.parseInt(zoneDtoList.get(i).getZone().substring(0, 1));
                if (zoneMin>zone){
                    zoneMin=zone;
                    PortEntryNameMin=zoneDtoList.get(i).getPortEntryName();
                    PortEntryIdMin=zoneDtoList.get(i).getPortEntryId();
                }
            }
            ZoneDto zoneDto = new ZoneDto();
            zoneDto.setPortEntryName(PortEntryNameMin);
            zoneDto.setZone(String.valueOf(zoneMin));
            zoneDto.setPortEntryId(PortEntryIdMin);
            return zoneDto;
        }else {
            return null;
        }
    }


    //根据收件人邮编查询HGUPS对应口岸与zone
    public ZoneDto calculateZone(String receiveCode,Long userId){
        String subCode = receiveCode.substring(0, 3);
        int rCode = Integer.parseInt(subCode);
        List<ZoneDto> zoneDtoList = new ArrayList<>();
        //查询开放入境口岸
        int processRole = rightsManagementService.isProcessRole(userId);
        EntityWrapper<PortEntry> wrapper = new EntityWrapper<>();
        wrapper.eq("state",1);


        //如果是后程用户则进一步塞选入境口岸
        if (processRole==1){
            EntityWrapper<PortLate> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("port_state",1);
            wrapper1.eq("late_state",1);
            wrapper1.eq("user_id", userId);
            List<PortLate> portLateList = portLateService.selectList(wrapper1);
            List<Integer> portIds = new ArrayList<>();
            for (PortLate portLate : portLateList) {
                portIds.add(portLate.getPortId());
            }
            if (portIds.size()<=0){
                return null;
            }
            wrapper.eq("type","tail");
            wrapper.in("id",portIds);
        } else {
            wrapper.eq("type","all");
        }
        //----后称用户塞选结束---
        //可用入境口岸
        List<Integer> portIds = new ArrayList<>();
        List<PortEntry> portList = portEntryService.selectList(wrapper);
        for (PortEntry portEntry:portList){
            portIds.add(portEntry.getId());
        }
        //查询开放入境口岸的zone
        EntityWrapper<Zone> wrapper1 = new EntityWrapper<>();
        wrapper1.in("port_entry_id", portIds);
        List<Zone> zones = zoneMapper.selectList(wrapper1);
        for (Zone zone : zones){
            ZoneDto zoneDto = new ZoneDto();
            String zipCode = zone.getZipCode();
            if(zipCode.indexOf("---") >= 0){
                String[] zipCodeStr = zipCode.split("---");
                int zipMin = Integer.parseInt(zipCodeStr[0]);
                int zipMax = Integer.parseInt(zipCodeStr[1]);
                if (rCode>=zipMin&&rCode<=zipMax){
                    zoneDto.setPortEntryId(zone.getPortEntryId());
                    zoneDto.setZone(zone.getZone());
                    for (PortEntry portEntry:portList){
                        if (zone.getPortEntryId()==portEntry.getId()){
                            zoneDto.setPortEntryName(portEntry.getTitle());
                            zoneDtoList.add(zoneDto);
                            break;
                        }
                    }
                }
            }else {
                if (rCode== Integer.parseInt(zipCode)){
                    zoneDto.setPortEntryId(zone.getPortEntryId());
                    zoneDto.setZone(zone.getZone());
                    for (PortEntry portEntry:portList){
                        if (zone.getPortEntryId()==portEntry.getId()){
                            zoneDto.setPortEntryName(portEntry.getTitle());
                            zoneDtoList.add(zoneDto);
                            break;
                        }
                    }
                }
            }
        }

        //计算最近距离入境口岸的zone
        if (zoneDtoList.size()>0){
            int zoneMin = Integer.parseInt(zoneDtoList.get(0).getZone().substring(0, 1));
            String PortEntryNameMin = zoneDtoList.get(0).getPortEntryName();
            int PortEntryIdMin = zoneDtoList.get(0).getPortEntryId();
            for (int i = 1;i<zoneDtoList.size();i++){
                int zone = Integer.parseInt(zoneDtoList.get(i).getZone().substring(0, 1));
                if (zoneMin>zone){
                    zoneMin=zone;
                    PortEntryNameMin=zoneDtoList.get(i).getPortEntryName();
                    PortEntryIdMin=zoneDtoList.get(i).getPortEntryId();
                }
            }
            ZoneDto zoneDto = new ZoneDto();
            zoneDto.setPortEntryName(PortEntryNameMin);
            zoneDto.setZone(String.valueOf(zoneMin));
            zoneDto.setPortEntryId(PortEntryIdMin);
            return zoneDto;
        }else {
            return null;
        }
    }



}
