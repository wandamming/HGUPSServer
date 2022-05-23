package com.hgups.express.service.warehousemgi;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.controller.warehousermg.WebSocketServer;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.exception.MyException;
import com.hgups.express.mapper.InventoryDetailsMapper;
import com.hgups.express.mapper.InventoryMapper;
import com.hgups.express.mapper.UserSacksMapper;
import com.hgups.express.service.waybillmgi.PointScanRecordService;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.util.WareHouseLabelUtils;
import com.hgups.express.vo.NoticeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/9/17 0017-10:01
 */
@Service
@Slf4j
public class InventoryService extends ServiceImpl<InventoryMapper, Inventory> {

    @Resource
    private InventoryMapper inventoryMapper;
    @Resource
    private InventoryProductService inventoryProductService;
    @Resource
    private ProductInfoService productInfoService;
    @Resource
    private UserSacksMapper userSacksMapper;
    @Resource
    private BuildingNoService buildingNoService;
    @Resource
    private FloorService floorService;
    @Resource
    private AreaService areaService;
    @Resource
    private ProductStorageService productStorageService;
    @Resource
    private InventoryDetailsMapper inventoryDetailsMapper;
    @Resource
    private PointScanRecordService pointScanRecordService;

    //入库单审核
    public Integer auditInventory(AuditInventoryParam param) throws MyException {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Inventory inventory = inventoryMapper.selectById(param.getId());
        if (null == inventory) {
            return -1;//入库单不存在或已被审核
        }
        BuildingNo buildingNo = buildingNoService.selectById(param.getBuildingNoId());
        if (null == buildingNo) {
            return -2;//该楼号不存在或已被删除,请重新选择
        }
        Floor floor = floorService.selectById(param.getFloorId());
        if (null == floor) {
            return -3;//该楼层不存在或已被删除,请重新选择
        }
        Area area = areaService.selectById(param.getAreaId());
        if (null == area) {
            return -4;//该区域不存在或已被删除,请重新选择
        }
        String address = buildingNo.getBuildingNoName() + "-" + floor.getFloorName() + "-" + area.getAreaName();


        Long id = inventory.getId();
        EntityWrapper<InventoryProduct> wrapper = new EntityWrapper<>();
        wrapper.eq("inventory_id", id);
        List<InventoryProduct> inventoryProductList = inventoryProductService.selectList(wrapper);

        int arrive = 0;
        int noArrive = 0;
        int qualified = 0;
        int noQualified = 0;

        for (AuditInventoryParam.SumParam sumParam : param.getSumParamList()) {
            InventoryProduct inventoryProduct = inventoryProductService.selectById(sumParam.getId());
            if (null == inventoryProduct) {
                continue;
            }

            ProductInfo productInfo = productInfoService.selectById(inventoryProduct.getProductId());
            if (null == productInfo) {
                continue;
            }

            //累计入库单入库情况
            arrive += sumParam.getArrive();
            noArrive += sumParam.getNoArrive();
            qualified += sumParam.getQualified();
            noQualified += sumParam.getNoQualified();

            //更新入库单的每个SKU产品对应的每个批次的到货情况
            inventoryProduct.setArrive(sumParam.getArrive());
            inventoryProduct.setNoArrive(sumParam.getNoArrive());
            inventoryProduct.setQualified(sumParam.getQualified());
            inventoryProduct.setNoQualified(sumParam.getNoQualified());

            //设置产品库存情况
            productInfo.setInventoryNumber(productInfo.getInventoryNumber() + sumParam.getQualified());

            inventoryProductService.updateById(inventoryProduct);
            productInfoService.updateById(productInfo);
        }

        inventory.setState(WareHouseState.ALREADY_INVENTORY);//已入库
        inventory.setArriveTime(new Date());

        inventory.setArrive(arrive);
        inventory.setNoArrive(noArrive);
        inventory.setQualified(qualified);
        inventory.setNoQualified(noQualified);

        inventory.setInventoryAddress(address);
        inventory.setReceiptCoding(WareHouseLabelUtils.createInventoryPDF(inventory));
        Integer integer = inventoryMapper.updateById(inventory);

        if (integer > 0) {
            //发送物流通知入库
            NoticeVo noticeVo = new NoticeVo();
            noticeVo.setContent("您的订单已入库");
            noticeVo.setCreateTime(new Date());
            noticeVo.setFromUserId(loginUserId);
            noticeVo.setToUserId(inventory.getUserId());
            noticeVo.setNoticeType(ResponseCode.NOTICE_LOGISTICS);
            noticeVo.setTitle("入库通知");
            noticeVo.setOrderTitle("入库单号");
            noticeVo.setTrackNumber(inventory.getReceiptOrder());
            //发送...
            try {
                WebSocketServer.sendInfo(JSONObject.toJSONString(noticeVo), String.valueOf(inventory.getUserId()), ResponseCode.NOTICE_LOGISTICS, loginUserId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            pointScanRecordService.addSysRecord(4, inventory.getWarehouseWaybillNumber(), "Entered", null, new Date(), "");

            return 1;
        }
        return 0;
    }


    //新建待入库单
    @Transactional(rollbackFor = Exception.class)
    public Inventory addInventory(InventoryParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Inventory inventory = new Inventory();
        inventory.setDescribe(param.getDescribe());
        inventory.setCreateTime(new Date());
        inventory.setUserId(loginUserId.intValue());
        String inventoryOrder = userSacksMapper.getSacksNumber(14);
        inventory.setReceiptOrder(inventoryOrder);//入库单号
        inventory.setExpectTime(param.getExpectTime());
        inventory.setState(WareHouseState.WAIT_INVENTORY);//待入库
        int count = inventoryMapper.insert(inventory);
        if (count > 0) {
            Long inventoryId = inventory.getId();
            log.info("===入库单ID====" + inventoryId);
            int productNumberSum = 0;//预约入库总数
            int productSum = 0;//sku总数
            List<InventoryProduct> inventoryProducts = new ArrayList<>();
            for (InventoryProducerParam iParam : param.getProducerList()) {
                //获取入库单增加的产品的ID
                Long producerId = iParam.getProducerId();
                productSum++;
                productNumberSum += iParam.getProducerNumber();


                //产品、入库单中间表数据保存
                InventoryProduct inventoryProduct = new InventoryProduct();
                inventoryProduct.setInventoryId(inventoryId);//入库单ID
                inventoryProduct.setProductId(producerId);//产品ID
                //此次入库的产品数量
                inventoryProduct.setProductNumber(iParam.getProducerNumber());

                //保存产品入库时间、数量（出库时使用）
                ProductStorage productStorage = new ProductStorage();
                productStorage.setProductId(producerId);
                productStorage.setProductNumber(iParam.getProducerNumber());
                productStorageService.insert(productStorage);

                if (iParam.getProducerNumber() > 0) {
                    inventoryProducts.add(inventoryProduct);
                }
            }
            if (inventoryProducts.size() > 0) {
                //批量保存中间表数据
                boolean b = inventoryProductService.insertBatch(inventoryProducts);
                if (b) {
                    //保存入库单产品种类数量与总数
                    inventory.setSkuNumber(productSum);
                    inventory.setReceiptNumber(productNumberSum);
                    inventory.setNoArrive(productNumberSum);
                    //生成入库单清单编码
                    inventory.setReceiptCoding(WareHouseLabelUtils.createInventoryPDF(inventory));
                    updateById(inventory);
                }
            }
            return inventory;
        } else {
            return null;
        }

    }

    //删除待入库单
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteInventory(List<Long> inventoryIds) throws MyException {
        if (null == inventoryIds) {
            return false;
        }
        for (Long inventoryId : inventoryIds) {
            EntityWrapper<InventoryProduct> wrapper = new EntityWrapper<>();
            Inventory inventory = selectById(inventoryId);
            wrapper.eq("inventory_id", inventoryId);
            List<InventoryProduct> inventoryProducts = inventoryProductService.selectList(wrapper);
            //获取当前库单的全部产品
            for (InventoryProduct inventoryProduct : inventoryProducts) {
                Long productId = inventoryProduct.getProductId();
                //获取当前库单的产品
                ProductInfo productInfo = productInfoService.selectById(productId);
                if (null != productInfo) {
                    //当前产品总数减去当前库单数量
                    Integer inventoryNumber = productInfo.getInventoryNumber();
                    inventoryNumber -= inventoryProduct.getProductNumber();
                    productInfo.setInventoryNumber(inventoryNumber);
                    productInfoService.updateById(productInfo);
                    //删除中间表
                    inventoryProductService.deleteById(inventoryProduct.getId());
                }
            }
            //删除入库单
            deleteById(inventoryId);
            pointScanRecordService.addSysRecord(4, inventory.getWarehouseWaybillNumber(), null, "cancel", new Date(), "");
        }
        return true;
    }

    //入库单列表
    public List<Inventory> inventoryList(InventoryListParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Page<Inventory> page = new Page<>(param.getCurrent(), param.getSize());
        EntityWrapper<Inventory> wrapper = new EntityWrapper<>();
        wrapper.orderBy("create_time", false);
        wrapper.eq("state", param.getState());
        wrapper.eq("user_id", loginUserId);
        //查询条件
        if (!StringUtils.isEmpty(param.getCreateBeginTime()) && !StringUtils.isEmpty(param.getCreateEndTime())) {
            wrapper.ge("create_time", param.getCreateBeginTime());
            wrapper.le("create_time", param.getCreateEndTime());
        }

        Page<Inventory> page1 = selectPage(page, wrapper);
        return page1.getRecords();
    }

    //管理入库单列表
    public List<Inventory> adminInventoryList(InventoryListParam param) {
        Long loginUserId = ShiroUtil.getLoginUserId();
        Page<Inventory> page = new Page<>(param.getCurrent(), param.getSize());
        List<String> receiptOrders = param.getReceiptOrders();
        EntityWrapper<Inventory> wrapper = new EntityWrapper<>();
        wrapper.eq("state", param.getState());
        wrapper.orderBy("create_time", false);
        //查询条件
        if (!StringUtils.isEmpty(param.getCreateBeginTime()) && !StringUtils.isEmpty(param.getCreateEndTime())) {
            wrapper.ge("create_time", param.getCreateBeginTime());
        }

        if (null != receiptOrders && receiptOrders.size() > 0 && (!"".equals(receiptOrders.get(0)))) { //判断是否需要根据单号查询
            wrapper.in("receipt_order", receiptOrders);
        }

        if (!StringUtils.isEmpty(param.getReceiveName())) {
            wrapper.eq("receive_name", param.getReceiveName());
        }
        if (!StringUtils.isEmpty(param.getSkuCode())) {
            EntityWrapper<ProductInfo> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("sku_code", param.getSkuCode());
            List<ProductInfo> productInfos = productInfoService.selectList(wrapper1);
            List<InventoryProduct> inventoryProducts = inventoryProductService.selectList(null);
            List<Long> ids = new ArrayList<>();
            for (ProductInfo productInfo : productInfos) {
                Long pid = productInfo.getId();
                for (InventoryProduct inventoryProduct : inventoryProducts) {
                    if (inventoryProduct.getProductId() == pid) {
                        ids.add(inventoryProduct.getInventoryId());
                    }
                }
            }
            if (ids.size() > 0) {
                wrapper.in("id", ids);
            }
        }

        Page<Inventory> page1 = selectPage(page, wrapper);
        return page1.getRecords();
    }


    //管理员入库单数量
    public Integer adminInventoryListCount(InventoryListParam param) {
        List<String> receiptOrders = param.getReceiptOrders();
        EntityWrapper<Inventory> wrapper = new EntityWrapper<>();
        wrapper.orderBy("create_time", false);
        wrapper.eq("state", param.getState());
        //查询条件
        if (!StringUtils.isEmpty(param.getCreateBeginTime()) && !StringUtils.isEmpty(param.getCreateEndTime())) {
            wrapper.ge("create_time", param.getCreateBeginTime());
            wrapper.le("create_time", param.getCreateEndTime());
        }

        if (null != receiptOrders && receiptOrders.size() > 0 && (!"".equals(receiptOrders.get(0)))) { //判断是否需要根据单号查询
            wrapper.in("receipt_order", receiptOrders);
        }

        if (!StringUtils.isEmpty(param.getReceiveName())) {
            wrapper.eq("receive_name", param.getReceiveName());
        }
        if (!StringUtils.isEmpty(param.getSkuCode())) {
            EntityWrapper<ProductInfo> wrapper1 = new EntityWrapper<>();
            wrapper1.eq("sku_code", param.getSkuCode());
            List<ProductInfo> productInfos = productInfoService.selectList(wrapper1);
            List<InventoryProduct> inventoryProducts = inventoryProductService.selectList(null);
            List<Long> ids = new ArrayList<>();
            for (ProductInfo productInfo : productInfos) {
                Long pid = productInfo.getId();
                for (InventoryProduct inventoryProduct : inventoryProducts) {
                    if (inventoryProduct.getProductId() == pid) {
                        ids.add(inventoryProduct.getInventoryId());
                    }
                }
            }
            if (ids.size() > 0) {
                wrapper.in("id", ids);
            }
        }
        return inventoryMapper.selectCount(wrapper);
    }

    //用户入库单数量
    public Integer inventoryListCount(InventoryListParam param) {
        EntityWrapper<Inventory> wrapper = new EntityWrapper<>();
        wrapper.orderBy("create_time", false);
        wrapper.eq("state", param.getState());
        wrapper.eq("user_id", ShiroUtil.getLoginUserId());
        //查询条件
        if (!StringUtils.isEmpty(param.getCreateBeginTime()) && !StringUtils.isEmpty(param.getCreateEndTime())) {
            wrapper.ge("create_time", param.getCreateBeginTime());
            wrapper.le("create_time", param.getCreateEndTime());
        }
        return inventoryMapper.selectCount(wrapper);
    }

    //入库单详情
    public InventoryDetailsVo getInventoryDetails(long id) {
        return inventoryDetailsMapper.getInventoryDetails(id);
    }


    //拒绝入库
    @Transactional(rollbackFor = Exception.class)
    public boolean refuseInventory(RefuseInventoryReasonParam param) throws MyException {
        if (param == null || null == param.getId() || param.getId() == 0) {
            return false;
        }

        EntityWrapper<InventoryProduct> wrapper = new EntityWrapper<>();
        wrapper.eq("inventory_id", param.getId());
        List<InventoryProduct> inventoryProducts = inventoryProductService.selectList(wrapper);
        //获取当前库单的全部产品
        for (InventoryProduct inventoryProduct : inventoryProducts) {
            Long productId = inventoryProduct.getProductId();
            //获取当前库单的产品
            ProductInfo productInfo = productInfoService.selectById(productId);
            if (null != productInfo) {
                //当前产品总数减去当前库单数量
                Integer inventoryNumber = productInfo.getInventoryNumber();
                inventoryNumber -= inventoryProduct.getProductNumber();
                productInfo.setInventoryNumber(inventoryNumber);
                productInfoService.updateById(productInfo);
            }
        }
        Inventory inventory = inventoryMapper.selectById(param.getId());
        inventory.setState(8);
        inventory.setRefuseReason(param.getRefuseReason());
        pointScanRecordService.addSysRecord(4, inventory.getWarehouseWaybillNumber(), "waitEnter", "refuseEnter", new Date(), "拒绝理由：" + param.getRefuseReason());

        return updateById(inventory);
    }

    public List<Inventory> createInventoryList(Map map) {
        return inventoryMapper.createInventoryList(map);
    }

    public Integer createInventoryCount(Map map) {
        return inventoryMapper.createInventoryCount(map);
    }

}
