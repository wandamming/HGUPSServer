package com.hgups.express.service.waybillmgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.business.ShipPartnerFile;
import com.hgups.express.business.ShipServiceFile;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.constant.WayBillState;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.exception.MyException;
import com.hgups.express.mapper.WayBillMapper;
import com.hgups.express.service.usermgi.DealDetailService;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.PDFUtils;
import com.hgups.express.util.PathUtils;
import com.hgups.express.util.ShiroUtil;
import com.hgups.express.vo.DataModuleVo;
import com.hgups.express.vo.DifferenceVo;
import com.hgups.express.vo.WayBillVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author fanc
 * 2020/6/9 0009-10:30
 */

@Service
@Slf4j
public class WayBillService extends ServiceImpl<WayBillMapper, WayBill> /*implements ServletContextAware */ {

    @Resource
    private WayBillMapper wayBillMapper;

    @Resource
    private WayBillService wayBillService;

    @Resource
    private WayBillVoService wayBillVoService;

    @Resource
    private UserAccountService userAccountService;
    @Resource
    private DealDetailService dealDetailService;
    @Resource
    private PointScanRecordService pointScanRecordService;


    /*public List<WayBillVo> getWayBillDetailsByBatchId(Integer batchId) {
        return wayBillMapper.getWayBillDetailsByBatchId(batchId);
    }*/

    public WayBill getWayBill(String tracking) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("tracking_number", tracking);
        WayBill wayBill = selectOne(wrapper);
        return wayBill;
    }


    //批量修改运单状态
    @Transactional
    public void updateWayBillState(List<Integer> wayBillIds, int state) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.in("id", wayBillIds);
        List<WayBill> list = wayBillMapper.selectList(wrapper);
        for (WayBill wayBill : list) {
            wayBill.setState(state);
            wayBillMapper.updateById(wayBill);
        }
    }

    //获取运单总金额(麻袋、批次)
    public float getSumPrice(Map map) {
        return wayBillMapper.getSumPrice(map);
    }

    //获取运单总金额(麻袋、批次)
    public float getShippingSumPrice(Map map) {
        return wayBillMapper.getShippingSumPrice(map);
    }

    //单条运单详情
    public WayBillVo getWayBillDetails(Integer wid) {
        WayBill wayBill = wayBillMapper.selectById(wid);
        String newTrackingNumber = wayBill.getNewTrackingNumber();
        if (StringUtils.isEmpty(newTrackingNumber)) {
            return wayBillMapper.getWayBillDetails(wid);
        } else {
            EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
            wrapper.eq("tracking_number", newTrackingNumber);
            WayBill wayBill1 = selectOne(wrapper);
            WayBillVo wayBillDetails = wayBillMapper.getWayBillDetails(wayBill1.getId());
            wayBillDetails.setWayBill(wayBill);
            return wayBillDetails;
        }
    }

    //多条运单详情
    public List<WayBillVo> getWayBillDetailsList(Map map) {
        List<WayBillVo> wayBillVo = wayBillMapper.getWayBillDetailsList(map);
        return wayBillVo;
    }

    //用户端运单列表
    public List<WayBill> getUserWayBillList(Map map) {
        return wayBillMapper.getUserWayBillList(map);
    }

    public int getUserWayBillListCount(Map map) {
        return wayBillMapper.getUserWayBillListCount(map);
    }

    //检查是否有该运单号
    public List<ParamId> checkWayBill(String wayBillNumber) {
        String[] str = wayBillNumber.split(" |,|，|\\n|\\t");
        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.in("tracking_number", str)
                .setSqlSelect("id");
        List<WayBill> wayBills = wayBillMapper.selectList(wrapper);
        List<ParamId> paramIds = DomainCopyUtil.mapList(wayBills, ParamId.class);
        return paramIds;
    }

    //修改运单,并返回修改后的运单
    @Transactional
    public WayBill updateWayBill(WayBill wayBill) {
        Integer integer = wayBillMapper.updateById(wayBill);
        if (integer > 0) {
            return wayBill;
        }
        return null;
    }

    //航运运单历史列表（多条件查询）
    public List<ShippingWayBillListParam> allWayBill(ShippingWayBillParam param) {
        return wayBillMapper.allWayBill(param);
    }

    //航运运单历史列表（总条数）
    public Integer countWayBill(ShippingWayBillParam param) {
        Integer count = wayBillMapper.countWayBill(param);
        return count;
    }


   /* @Async
    public Future<String> excuteValueTask(int i) throws InterruptedException {
        Thread.sleep(1000);
        Future<String> future = new AsyncResult<String>("success is " + i);
        System.out.println("异步执行任务第[" + i + "] 个");
        return future;
    }


>>>>>>> 5666945... 问题包裹修改，注释多线程测试
    @Async
    public ListenableFuture<String> sayHello1(int i) {
        String res = "异步执行任务 ---1--->>"+i;
        LoggerFactory.getLogger(WayBillService.class).info(res);
        return new AsyncResult<>(res);
    }
    @Async
    public ListenableFuture<String> sayHello2(int i) {
        String res = "异步执行任务 ---2--->>"+i;
        LoggerFactory.getLogger(WayBillService.class).info(res);
        return new AsyncResult<>(res);
    }

    @Async
    public void executeAysncTask1(Integer i){
        log.info("CustomMultiThreadingService ==> executeAysncTask1 method: 执行异步任务{} ", i);
    }
    @Async
    public void executeAsyncTask2(Integer i){
        log.info("CustomMultiThreadingService ==> executeAsyncTask2 method: 执行异步任务{} ", i);
    }*/

    @Transactional
    public boolean uploadShipPartnerState(ShipPartnerStateParam param) throws MyException {

        List<ParamId> paramIds = checkWayBill(param.getWayBillNumber());
        List<Integer> ids = new ArrayList();
        for (int i = 0; i < paramIds.size(); i++) {
            ids.add(paramIds.get(i).getId());
        }
        System.out.println("批次遇上线状态1========" + param.getState());
        return !ids.isEmpty() && uploadShipPartnerState(ids, param.getState());
    }

    @Transactional
    public void uploadShipPartnerStateByShipBatch(ShippingBatch batch, String state) throws MyException {
        EntityWrapper<WayBill> wrapper = new EntityWrapper();
        wrapper.eq("shipping_batch_id", batch.getId());
        List<WayBill> wayBillList = selectList(wrapper);
        if (wayBillList == null || wayBillList.size() == 0) {
            log.warn(" uploadShipPartnerStateByShipBatch bill list is null ");
            return;
        }

        List<Integer> ids = new ArrayList<>();
        for (WayBill bill : wayBillList) {
            if (bill != null) {
                ids.add(bill.getId());
            }
        }
        uploadShipPartnerState(ids, state);
    }

    @Transactional
    public boolean uploadShipPartnerState(List<Integer> wayBillIds, String state) throws MyException {
        Long userId = ShiroUtil.getLoginUserId();

        Map<Object, Object> map = new HashMap<>();
        map.put("wids", wayBillIds);


        //把新运单ID换成旧运单ID，否则会查不到
        List<String> newTrackingNumber = wayBillService.getOldTrackingNumber(map);
        Map map1 = new HashMap<>();
        map1.put("trackingNumbers", newTrackingNumber);
        if (newTrackingNumber != null && newTrackingNumber.size() != 0) {
            List<Integer> oldId = wayBillService.getIdByTrackingNumber(map1);
            for (Integer integer : oldId) {
                wayBillIds.add(integer);
            }
        }

        List<WayBillVo> wayBillDetailsList = getWayBillDetailsList(map);
        log.info(" uploadShipPartnerState userId: " + userId + ", uploadShipPartnerState state: " + state
                + "， ids： " + String.valueOf(wayBillIds)
                + "， wayBillDetailsList： " + String.valueOf(wayBillDetailsList));
        boolean res = wayBillDetailsList != null && wayBillDetailsList.size() > 0;
        if (res) {
            //更新运单 状态
            updateWayBillState(wayBillIds, WayBillState.SEND);
            //批量更新运单 预上线状态
            updateSpEventState(wayBillIds, state);
            //生成 预先上线文件并上传
//            ShipServiceFile.batchUpload(wayBillDetailsList); //测试取出 SSF 文件的代码
            System.out.println("批次遇上线状态2===============" + state);
            /*for (WayBillVo list : wayBillDetailsList) {
                Integer newWayBillId = list.getWayBill().getNewWayBillId();
                if (newWayBillId != null && newWayBillId!=-1){
                    list.setWayBill(wayBillService.selectById(newWayBillId));
                }
            }*/
            for (int i = 0; i < wayBillDetailsList.size(); i++) {
                WayBill wayBill = wayBillDetailsList.get(i).getWayBill();
                if (wayBill.getNewWayBillId() != -1) {
                    Integer newWayBillId = wayBill.getNewWayBillId();
                    WayBill wayBill1 = wayBillService.selectById(newWayBillId);
                    wayBillDetailsList.get(i).setWayBill(wayBill1);
                }
            }
            ShipPartnerFile.batchUpload(wayBillDetailsList, state);
        }
        return res;
    }

    @Transactional
    public int updateSpEventState(List<Integer> wayBillIds, String state) throws MyException {
        baseMapper.updateSpEventState(wayBillIds, state);
        List<WayBill> wayBills = selectBatchIds(wayBillIds);
        for (WayBill wayBill : wayBills) {
            pointScanRecordService.addSysRecord(1, wayBill.getTrackingNumber(), null, "upline", new Date(), "预上线状态：" + state);
        }

        return ResponseCode.SUCCESS_CODE;
    }

    // 批量更新 ===== SSF
    @Transactional
    public boolean updateSSF(ShipServiceParam param) {

        List<ParamId> paramIds = checkWayBill(param.getWayBillNumber());
        List<Integer> ids = new ArrayList();

        for (int i = 0; i < paramIds.size(); i++) {
            ids.add(paramIds.get(i).getId());
        }

        if (ids.size() == 0) {
            return false;
        }

        Map<Object, Object> map = new HashMap<>();
        map.put("wids", ids);

        //如果是新单则把旧运单ID加入map
        List<String> newTrackingNumber = wayBillService.getOldTrackingNumber(map);
        Map map1 = new HashMap<>();
        map1.put("trackingNumbers", newTrackingNumber);
        if (newTrackingNumber != null && newTrackingNumber.size() != 0) {
            List<Integer> oldId = wayBillService.getIdByTrackingNumber(map1);
            for (Integer integer : oldId) {
                ids.add(integer);
            }
        }
        return !ids.isEmpty() && updateSSFByIds(ids, true);
    }

    @Transactional
    public void updateSSFByShipBatch(ShippingBatch batch, boolean ssf) {
        EntityWrapper<WayBill> wrapper = new EntityWrapper();
        wrapper.eq("shipping_batch_id", batch.getId());
        List<WayBill> wayBillList = selectList(wrapper);

        log.info(" updateSSFByIds wayBillList: " + wayBillList);
        if (wayBillList == null || wayBillList.size() == 0) {
            log.warn(" updateSSFByIds bill list is null ");
            return;
        }

        List<Integer> ids = new ArrayList<>();
        for (WayBill bill : wayBillList) {
            if (bill != null) {
                ids.add(bill.getId());
            }
        }
        updateSSFByIds(ids, ssf);
    }

    @Transactional
    public boolean updateSSFByIds(List<Integer> wayBillIds, boolean ssf) {
        Map<Object, Object> map = new HashMap<>();
        map.put("wids", wayBillIds);
        List<WayBillVo> wayBillDetailsList = getWayBillDetailsList(map);

        /*for (WayBillVo list : wayBillDetailsList) {
            Integer newWayBillId = list.getWayBill().getNewWayBillId();
            if (newWayBillId != null && newWayBillId!=-1){
                list.setWayBill(wayBillService.selectById(newWayBillId));
            }
        }*/
        for (int i = 0; i < wayBillDetailsList.size(); i++) {
            WayBill wayBill = wayBillDetailsList.get(i).getWayBill();
            if (wayBill.getNewWayBillId() != -1) {
                Integer newWayBillId = wayBill.getNewWayBillId();
                WayBill wayBill1 = wayBillService.selectById(newWayBillId);
                wayBillDetailsList.get(i).setWayBill(wayBill1);
            }
        }
        boolean res = wayBillDetailsList != null && wayBillDetailsList.size() > 0;
        if (res) {
            //批量更新运单 SSF属性
            updateSSF(wayBillIds, ssf);
            //生成 SSF文件并上传
            ShipServiceFile.batchUpload(wayBillDetailsList);
        }
        return res;
    }

    @Transactional
    public int updateSSF(List<Integer> ids, boolean ssf) {
        baseMapper.updateSSF(ids, ssf);
        return ResponseCode.SUCCESS_CODE;
    }

    public String batchExportWayPDF(List wayBillList) {
        Map map = new HashMap();
        map.put("wIds", wayBillList);
        List<String> wayBillCoding = wayBillMapper.getWayBillCoding(map);
        User user = ShiroUtil.getLoginUser();
        String userName = user.getUsername();
        String pdfUserPath = PathUtils.resDir + userName;
        String pdfPath = pdfUserPath + "/batchExportWayPDF.pdf";

        log.info(" batchExportWayPDF wayBillList: " + String.valueOf(wayBillList)
                + "\n  pdfPath: " + pdfPath);

        List<String> pdfPaths = new ArrayList<String>();
        String tmpDirPath = pdfUserPath + "/batchExportWayPDF/";
        File tmpDir = new File(tmpDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        //生成每一张临时的中间PDF
        int i = 0;
        for (String base64Str : wayBillCoding) {
            i++;
            String newPdfPath = tmpDirPath + i + ".pdf";
            PDFUtils.base64StringToPDF(base64Str, newPdfPath);
            pdfPaths.add(newPdfPath);
        }

        //生成最终合并的pdf
        PDFUtils.mergePDF(pdfPaths, pdfPath);

        //删除每一行临时的中间PDF
        for (String tmpPath : pdfPaths) {
            new File(tmpPath).delete();
        }

        return pdfPath;
    }

    public List<WayBill> getProblemWayBill(ProblemWayBillParam param) {
        return wayBillMapper.getProblemWayBill(param);
    }

    public Integer getProblemWayBillCount(ProblemWayBillParam param) {
        return wayBillMapper.getProblemWayBillCount(param);
    }


    public List<WayBillAndUserParam> getChangeSingle(ChangeSingleParam param) {
        return wayBillMapper.getChangeSingle(param);
    }

    public Integer getChangeSingleCount(ChangeSingleParam param) {
        return wayBillMapper.getChangeSingleCount(param);
    }


    //更换面单
    public WayBillVo getChangeSingleInfo(Integer wid) {
        return wayBillMapper.getChangeSingleInfo(wid);
    }


    public boolean updateRefund(WayBill wayBill) {
        //打单用户
        long loginUserId = wayBill.getUserId();
        //核重价格
        double price = 0;
        double warePrice = wayBill.getWarePrice();
        double billPrice = wayBill.getPrice();
        if (warePrice == 0) {
            price = billPrice;
        } else {
            price = warePrice;
        }
        //修改余额（退费）
        EntityWrapper<UserAccount> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id", loginUserId);
        UserAccount userAccount = userAccountService.selectOne(wrapper);
        double balance = userAccount.getBalance();
        userAccount.setBalance(balance + price);
        boolean isUserAccount = userAccountService.updateById(userAccount);

        //添加交易记录
        DealDetail dealDetail = new DealDetail();
        dealDetail.setBalance(userAccount.getBalance());
        dealDetail.setDealAmount(price);
        dealDetail.setDealType(3);//1:扣费,2：充值,3：退款
        dealDetail.setState(1);//交易状态
        dealDetail.setUserId(loginUserId);
        dealDetail.setWayBillId(wayBill.getId());
        boolean insert = dealDetailService.insert(dealDetail);
        log.info("交易记录退款-->", price);
        if (isUserAccount && insert) {
            return true;
        }
        return false;
    }


    //根据运单ID修改重量
    public Integer updateWeightById(double weight, Integer id) {
        WayBill wayBill = wayBillMapper.selectById(id);

        if ((wayBill.getBillWeight() >= 1 && weight < 1) || (wayBill.getBillWeight() < 1 && weight >= 1)) {
            return -2;//服务类型不匹配
        }

        double wBillWeight = wayBillVoService.getOnePrice(weight, wayBill.getZone(), wayBill.getChannel(), wayBill.getUserId());
        DecimalFormat format1 = new DecimalFormat("#.00");
        double price = Double.parseDouble(format1.format(wBillWeight));
        double billPrice = wayBill.getPrice();

        DifferenceVo difference = userAccountService.difference(price, billPrice, wayBill);
        if (difference.getCode() == 0) { //多退少补成功
            if (wayBill.getWareWeight() == 0) {
                wayBill.setBillWeight(weight);
                wayBill.setPrice(price);
                wayBillMapper.updateById(wayBill);
            } else {
                wayBill.setWareWeight(weight);
                wayBill.setWarePrice(price);
                wayBillMapper.updateById(wayBill);
            }
            return 0;//成功
        } else if (difference.getCode() == -1) {
            return -1;//余额不足
        } else {
            return 1;//运单未找到
        }
    }

    //清关报关运单号及ID
    public List<ShippingWayBillNumberVo> getShippingWayBillNumber(int shippingBatchId) {
        return wayBillMapper.getShippingWayBillNumber(shippingBatchId);
    }

    public Double getCateGoryWarePrice(List<Integer> wIds) {
        return wayBillMapper.getCateGoryWarePrice(wIds);
    }

    public Double getCateGoryWareWeight(List<Integer> wIds) {
        return wayBillMapper.getCateGoryWareWeight(wIds);
    }

    public Double getWareWeightByBatchId(String trackNumber) {
        return wayBillMapper.getWareWeightByBatchId(trackNumber);
    }

    public List<String> getOldTrackingNumber(Map map) {
        return wayBillMapper.getOldTrackingNumber(map);
    }

    public List<Integer> getIdByTrackingNumber(Map map) {
        return wayBillMapper.getIdByTrackingNumber(map);
    }

    //获取航运批次跟换过面单的运单列表
    public List<WayBill> selectTrackingList() {
        return wayBillMapper.selectTrackingList();
    }

    //根据运单ID获取运单Coding
    public String getCodingById(int id) {
        return wayBillMapper.getCodingById(id);
    }

    //获取未加入用户批次运单信息列表
    public List<UserBatchWayBill> getNotIntoBatchWayBillList(Long loginUserId) {
        return wayBillMapper.getNotIntoBatchWayBillList(loginUserId);
    }

    //获取未加入用户批次运单信息总数
    public Integer getNotIntoBatchWayBillListCount(Long loginUserId) {
        return wayBillMapper.getNotIntoBatchWayBillListCount(loginUserId);
    }

    //获取未加入航运批次运单信息
    public List<WayBill> getShippingBatchWayBillList() {
        return wayBillMapper.getShippingBatchWayBillList();
    }

    //获取未加入航运批次运单信息总数
    public Integer getShippingBatchWayBillListCount() {
        return wayBillMapper.getShippingBatchWayBillListCount();
    }

    //获取未加入麻袋的运单
    public List<WayBill> getNotJoinWayBill() {
        return wayBillMapper.getNotJoinWayBill();
    }

    public Integer getNotJoinWayBillCount() {
        return wayBillMapper.getNotJoinWayBillCount();
    }

    /**
     * 查找父面单
     *
     * @param orderTrackingNumber
     * @return
     * @throws MyException
     */
    public String getNewTrackingNumber(String orderTrackingNumber, Integer pointType) throws MyException {
        if (pointType == 1) {
            WayBill wayBill = selectOne(new EntityWrapper<WayBill>().eq("tracking_number", orderTrackingNumber));
            if (wayBill == null) {
                throw new MyException("找不到该单号");
            } else {
                WayBill oldWayBill = selectOne(new EntityWrapper<WayBill>().eq("new_way_bill_id", wayBill.getId()));
                if (oldWayBill != null) {
                    return oldWayBill.getTrackingNumber();
                }
            }
        }
        return orderTrackingNumber;
    }
    public DataModuleVo getDataModule(){
        Long loginUserId = ShiroUtil.getLoginUserId();
        DataModuleVo moduleVoList = new DataModuleVo();
        moduleVoList.setAllWaybills(wayBillMapper.getAllWayBills(loginUserId));//根据方法返回
        moduleVoList.setPendingWaybills(wayBillMapper.getPendingWaybills(loginUserId));
        moduleVoList.setShippedWaybills(wayBillMapper.getShippedWaybills(loginUserId));
        moduleVoList.setSignedWaybills(wayBillMapper.getSignedWaybills(loginUserId));
        moduleVoList.setProblemWaybills(wayBillMapper.getProblemWaybills(loginUserId));
        BigDecimal forecastPrice = wayBillMapper.getForecastPrice(loginUserId);
        BigDecimal warePrice = wayBillMapper.getWarePrice(loginUserId);
        moduleVoList.setForecastPrice(forecastPrice);
        moduleVoList.setWarePrice(warePrice);
        return moduleVoList;
    }

}


