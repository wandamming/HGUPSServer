package com.hgups.express.service.waybillmgi;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.domain.vo.*;
import com.hgups.express.mapper.CityMapper;
import com.hgups.express.mapper.OrderInfoMapper;
import com.hgups.express.mapper.PlatformMapper;
import com.hgups.express.mapper.StoreMapper;
import com.hgups.express.service.warehousemgi.ProductInfoService;
import com.hgups.express.util.MyFileUtil;
import com.hgups.express.util.ShiroUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wandaming
 * 2021/7/19-11:25
 */
@Slf4j
@Service
@Transactional
public class OrderInfoService extends ServiceImpl<OrderInfoMapper,OrderInfo> {
    @Autowired(required = false)
    private OrderInfoMapper orderInfoMapper;

    private List<DeliverCostVo> orderCost;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private ProductInfoService productInfoService;

    @Resource
    private ChannelService channelService;

    @Resource
    private CityService cityService;

    @Resource
    private OrderTypeService orderTypeService;

    @Resource
    private DeliverRouteService deliverRouteService;

    @Resource
    private PlatformMapper platformMapper;


    @Resource
    private DeliverModeService deliverModeService;

    @Resource
    private StoreService storeService;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private CityMapper cityMapper;


    //分页查询系统通知
    public Page<OrderInfoVo> getOrderInfo(OrderParam param) {

        Page<OrderInfoVo> page = new Page<>();
        page.setCurrent(param.getCurrent());
        page.setSize(param.getSize());
        List<OrderInfoVo> vo = orderInfoMapper.getOrderInfo(page, param);
        int total = orderInfoMapper.getOrderInfoCount(param);
        page.setTotal(total);
        page.setRecords(vo);
        return page;
    }


    //获取订单详情
    public OrderDetailsVo getOrderDetails(IdParam param){

        return orderInfoMapper.getOrderDetails(param);
    }

    //批量删除订单

    public boolean deleteBatchOrder(IdsParam id) {
        try {
            orderInfoMapper.deleteBatchOrder(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }




    //导出订单excel
    public ResponseEntity exportOrderExcel(List orderList){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Map<Object, Object> map = new HashMap<>();
        map.put("ids", orderList);
        List<ExportOrderVo> vo = orderInfoMapper.exportOrderExcel(map);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//设置日期格式
        List<String> headRow1 = Lists.newArrayList("序号", "平台订单号", "所属平台", "店铺名称", "电商订单号", "客户名称", "联系电话", "订单状态", "下单时间",
                "发件人","联系电话","发货城市","具体地址","邮政编码","发货渠道","发货路线","sku编号","图片","中文名称","英文名称","数量");

        ExcelWriter writer = ExcelUtil.getWriter();
        writer.writeHeadRow(headRow1);
        for (int i = 0; i < headRow1.size(); i++) {
            writer.autoSizeColumn(i);
            if (i == 0) {
                writer.setColumnWidth(i, 12);
            } else if (i == 1) {
                writer.setColumnWidth(i, 40);
            } else {
                writer.setColumnWidth(i, 30);
            }
        }
        List i = new ArrayList();
        vo.forEach(x -> {
            i.add("");
            List<Object> dataList = Lists.newArrayList();
            dataList.add(i.size());
            //订单信息
            OrderInfo order = x.getOrderInfo();
            Store store = x.getStore();
            Platform platform = x.getPlatform();

            dataList.add(order.getId());
            dataList.add(order.getPlatformOrderNum());
            dataList.add(platform.getName());
            dataList.add(store.getName());
            dataList.add(order.getCommerceOrderNum());
            dataList.add(order.getCustomerName());
            dataList.add(order.getTelephone());

            if ("1".equals(order.getOrderState())) {
                dataList.add("未发货");
            } else if ("2".equals(order.getOrderState())) {
                dataList.add("已发货");
            } else {
                dataList.add("已取消");
            }
            Date createTime = order.getOrderTime();
            dataList.add(df.format(createTime));


            //发件人
            Sender sender = x.getSender();
            dataList.add(sender.getName());
            dataList.add(sender.getPhone());
            dataList.add(sender.getCityCname());
            dataList.add(sender.getAddressTwo());
            dataList.add(sender.getPostalCode());

            //渠道路线
            Channel channel = x.getChannel();
            DeliverRoute deliverRoute = x.getDeliverRoute();
            dataList.add(channel.getChannelName());
            dataList.add(deliverRoute.getRoute());

            //商品
            List<ProductInfo> productInfoList = x.getProductInfoList();
            ProductInfo productInfo = productInfoList.get(0);
            dataList.add(productInfo.getSkuCode());
            dataList.add(productInfo.getImageUrl());
            dataList.add(productInfo.getCName());
            dataList.add(productInfo.getEName());
            dataList.add(productInfo.getInventoryNumber());

            writer.writeRow(dataList);
        });

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        writer.flush(byteOutputStream);
        try {
            return MyFileUtil.downloadFile(byteOutputStream.toByteArray(), "订单列表.xls", httpServletRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


    //费用
    public List<OrderCostVo> getOrderCost(OrderCostParam pageParam) {
        pageParam.setCurrent((pageParam.getCurrent() - 1) * pageParam.getSize());
        return baseMapper.getOrderCost(pageParam);
    }

    public Integer getOrderCostCount(OrderCostParam pageParam) {
        return baseMapper.getOrderCostCount(pageParam);
    }


    public Response insertBatchOrder(MultipartFile excelFile){
        Response response = new Response();
        List<BatchOrderParam> insertEntityList = null;
        if (excelFile == null) {
            response.setStatusCode(600);
            response.setMsg("Excel文件丢失");
            return response;
        }

        List<InsertBatchOrderErrorParam> errorMsgList = new ArrayList<>();
        List<List<Object>> lines = new ArrayList<>();

        try {
            lines = ExcelUtil.getReader(excelFile.getInputStream()).read();
        } catch (Exception e) {
            log.error("parse error:", e);
            response.setStatusCode(601);
            response.setMsg("文件解析异常，请检查excel文件格式是否正确!");
            return response;
        }


        insertEntityList = Lists.newArrayList();

        int nid = 0;
        int f = 0;
        boolean failure = false;
        List<Integer> errorIndex = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            nid++;
            List<Object> line = lines.get(i);
            //InsertBatchWayBillError errorMsg = new InsertBatchWayBillError();
            int index = 0;

            BatchOrderParam batchOrderParam = new BatchOrderParam();

            batchOrderParam.setNid(nid);

            //平台订单号
            String platformOrderNum = "";
            //姓名
            try {
                platformOrderNum = String.valueOf(line.get(index++));
                if ("null".equals(platformOrderNum) || "" == platformOrderNum) {
                    platformOrderNum = "";
                    throw new NullPointerException("NullPointerException");
                }

            } catch (Exception e) {
                failure = true;
                InsertBatchOrderErrorParam error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }

            //所属平台
            String platform = "";
            try {
                platform = String.valueOf(line.get(index++));
                if ("null".equals(platform) || "" == platform) {
                    platformOrderNum = "";
                    throw new NullPointerException("NullPointerException");
                }

            } catch (Exception e) {
                failure = true;
                InsertBatchOrderErrorParam error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }


            //店铺名称

            String storeName = "";
            try {
                storeName = String.valueOf(line.get(index++));
                if ("null".equals(storeName) || "" == storeName) {
                    platformOrderNum = "";
                    throw new NullPointerException("NullPointerException");
                }

            } catch (Exception e) {
                failure = true;
                InsertBatchOrderErrorParam error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }

            //电商订单号
            String commerceOrderNum = "";
            try {
                commerceOrderNum = String.valueOf(line.get(index++));
                if ("null".equals(commerceOrderNum) || "" == commerceOrderNum) {
                    commerceOrderNum = "";
                    throw new NullPointerException("NullPointerException");
                }

            } catch (Exception e) {
                failure = true;
                InsertBatchOrderErrorParam error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }


            //客户名称
            String customerName = "";
            try {
                customerName = String.valueOf(line.get(index++));
                if ("null".equals(customerName) || "" == customerName) {
                    customerName = "";
                    throw new NullPointerException("NullPointerException");
                }

            } catch (Exception e) {
                failure = true;
                InsertBatchOrderErrorParam error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }


            //联系电话

            String telephone = "";
            try {
                telephone = String.valueOf(line.get(index++));
                if ("null".equals(telephone) || "" == telephone) {
                    telephone = "";
                    throw new NullPointerException("NullPointerException");
                }

            } catch (Exception e) {
                failure = true;
                InsertBatchOrderErrorParam error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }

            //点单状态
            String orderStateValue = "";
            try {
                orderStateValue = String.valueOf(line.get(index++));
                if ("null".equals(orderStateValue) || "" == orderStateValue) {
                    orderStateValue = "";
                    throw new NullPointerException("NullPointerException");
                }

            } catch (Exception e) {
                failure = true;
                InsertBatchOrderErrorParam error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }

            //下单时间
            String orderTime = "";
            try {
                orderTime = String.valueOf(line.get(index++));
                if ("null".equals(orderTime) || "" == orderTime) {
                    orderTime = "";
                    throw new NullPointerException("NullPointerException");
                }

            } catch (Exception e) {
                failure = true;
                InsertBatchOrderErrorParam error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }


            String sender = "";
            try {
                sender = String.valueOf(line.get(index++));
                if ("null".equals(sender) || "" == sender) {
                    sender = "";
                    throw new NullPointerException("NullPointerException");
                }

            } catch (Exception e) {
                failure = true;
                InsertBatchOrderErrorParam error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }

            String senderTelephone = "";
            try {
                senderTelephone = String.valueOf(line.get(index++));
                if ("null".equals(senderTelephone) || "" == senderTelephone) {
                    senderTelephone = "";
                    throw new NullPointerException("NullPointerException");
                }

            } catch (Exception e) {
                failure = true;
                InsertBatchOrderErrorParam error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }

            String deliverCity = "";
            try {
                deliverCity = String.valueOf(line.get(index++));
                if ("null".equals(deliverCity) || "" == deliverCity) {
                    deliverCity = "";
                    throw new NullPointerException("NullPointerException");
                }

            } catch (Exception e) {
                failure = true;
                InsertBatchOrderErrorParam error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }


            String specificAddress = "";
            try {
                specificAddress = String.valueOf(line.get(index++));
                if ("null".equals(specificAddress) || "" == specificAddress) {
                    specificAddress = "";
                    throw new NullPointerException("NullPointerException");
                }

            } catch (Exception e) {
                failure = true;
                InsertBatchOrderErrorParam error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }


            String postalCode = "";
            try {
                postalCode = String.valueOf(line.get(index++));
                if ("null".equals(postalCode) || "" == postalCode) {
                    postalCode = "";
                    throw new NullPointerException("NullPointerException");
                }

            } catch (Exception e) {
                failure = true;
                InsertBatchOrderErrorParam error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }


            String channel = "";
            try {
                channel = String.valueOf(line.get(index++));
                if ("null".equals(channel) || "" == channel) {
                    channel = "";
                    throw new NullPointerException("NullPointerException");
                }

            } catch (Exception e) {
                failure = true;
                InsertBatchOrderErrorParam error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }


            String deliverRoute = "";
            try {
                deliverRoute = String.valueOf(line.get(index++));
                if ("null".equals(deliverRoute) || "" == deliverRoute) {
                    deliverRoute = "";
                    throw new NullPointerException("NullPointerException");
                }

            } catch (Exception e) {
                failure = true;
                InsertBatchOrderErrorParam error = tryCatch(e, i, index);
                errorMsgList.add(error);
            }


            batchOrderParam.setPlatformOrderNum(platformOrderNum);
            batchOrderParam.setPlatform(platform);
            batchOrderParam.setStoreName(storeName);
            batchOrderParam.setCommerceOrderNum(commerceOrderNum);
            batchOrderParam.setCustomerName(customerName);
            batchOrderParam.setTelephone(telephone);
            batchOrderParam.setOrderStateValue(orderStateValue);
            batchOrderParam.setOrderTime(orderTime);
            batchOrderParam.setSender(sender);
            batchOrderParam.setSenderTelephone(senderTelephone);
            batchOrderParam.setDeliverCity(deliverCity);
            batchOrderParam.setSpecificAddress(specificAddress);
            batchOrderParam.setPostalCode(postalCode);



            if (failure) {
                f++;
                errorIndex.add(i - 2);
            }
            failure = false;
            insertEntityList.add(batchOrderParam);
        }

        if (errorMsgList.size() > 0) {
            response.setStatusCode(602);
            response.setMsg("订单数据有误，请检查数据是否正确!");
            Map<Object, Object> result = new HashMap<>();
            result.put("insertEntityList", insertEntityList);
            result.put("errorMsgList", errorMsgList);
            result.put("batchSum", lines.size() - 2);
            result.put("success", lines.size() - 2 - f);
            result.put("failure", f);
            result.put("errorIndex", errorIndex);
            response.setData(result);
            return response;
        }
        response.setStatusCode(200);
        response.setMsg("导入成功");
        response.setData(insertEntityList);
        return response;
    }

    public InsertBatchOrderErrorParam tryCatch(Exception error, int i, int index) {
        InsertBatchOrderErrorParam errorMsg = new InsertBatchOrderErrorParam();
        try {
            throw error;
        } catch (ArrayIndexOutOfBoundsException e) {
            errorMsg.setErrorLocation(String.format("第%s行只有%s列", i + 1, index));
        } catch (Exception e) {
            String es = e.toString();
            log.info("批量导入表格错误信息-------->>>" + es);
            boolean NumberFormatException = es.contains("NumberFormatException");
            boolean illegalArgumentException = es.contains("IllegalArgumentException");
            boolean nullPointerException = es.contains("NullPointerException");
            if (illegalArgumentException || NumberFormatException) {
                errorMsg.setErrorMessage("填写格式错误");
            }
            if (nullPointerException) {
                errorMsg.setErrorMessage("必填项不能为空");
            }
            errorMsg.setErrorLocation(String.format("第%s行第%s列解析失败", i + 1, index) + "(" + errorMsg.getColumName(index) + ")：" + errorMsg.getErrorMessage());
            return errorMsg;
        }
        return null;
    }

}
