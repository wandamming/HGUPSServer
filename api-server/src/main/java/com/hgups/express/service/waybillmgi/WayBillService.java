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


    //????????????????????????
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

    //?????????????????????(???????????????)
    public float getSumPrice(Map map) {
        return wayBillMapper.getSumPrice(map);
    }

    //?????????????????????(???????????????)
    public float getShippingSumPrice(Map map) {
        return wayBillMapper.getShippingSumPrice(map);
    }

    //??????????????????
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

    //??????????????????
    public List<WayBillVo> getWayBillDetailsList(Map map) {
        List<WayBillVo> wayBillVo = wayBillMapper.getWayBillDetailsList(map);
        return wayBillVo;
    }

    //?????????????????????
    public List<WayBill> getUserWayBillList(Map map) {
        return wayBillMapper.getUserWayBillList(map);
    }

    public int getUserWayBillListCount(Map map) {
        return wayBillMapper.getUserWayBillListCount(map);
    }

    //???????????????????????????
    public List<ParamId> checkWayBill(String wayBillNumber) {
        String[] str = wayBillNumber.split(" |,|???|\\n|\\t");
        EntityWrapper<WayBill> wrapper = new EntityWrapper<>();
        wrapper.in("tracking_number", str)
                .setSqlSelect("id");
        List<WayBill> wayBills = wayBillMapper.selectList(wrapper);
        List<ParamId> paramIds = DomainCopyUtil.mapList(wayBills, ParamId.class);
        return paramIds;
    }

    //????????????,???????????????????????????
    @Transactional
    public WayBill updateWayBill(WayBill wayBill) {
        Integer integer = wayBillMapper.updateById(wayBill);
        if (integer > 0) {
            return wayBill;
        }
        return null;
    }

    //?????????????????????????????????????????????
    public List<ShippingWayBillListParam> allWayBill(ShippingWayBillParam param) {
        return wayBillMapper.allWayBill(param);
    }

    //???????????????????????????????????????
    public Integer countWayBill(ShippingWayBillParam param) {
        Integer count = wayBillMapper.countWayBill(param);
        return count;
    }


   /* @Async
    public Future<String> excuteValueTask(int i) throws InterruptedException {
        Thread.sleep(1000);
        Future<String> future = new AsyncResult<String>("success is " + i);
        System.out.println("?????????????????????[" + i + "] ???");
        return future;
    }


>>>>>>> 5666945... ??????????????????????????????????????????
    @Async
    public ListenableFuture<String> sayHello1(int i) {
        String res = "?????????????????? ---1--->>"+i;
        LoggerFactory.getLogger(WayBillService.class).info(res);
        return new AsyncResult<>(res);
    }
    @Async
    public ListenableFuture<String> sayHello2(int i) {
        String res = "?????????????????? ---2--->>"+i;
        LoggerFactory.getLogger(WayBillService.class).info(res);
        return new AsyncResult<>(res);
    }

    @Async
    public void executeAysncTask1(Integer i){
        log.info("CustomMultiThreadingService ==> executeAysncTask1 method: ??????????????????{} ", i);
    }
    @Async
    public void executeAsyncTask2(Integer i){
        log.info("CustomMultiThreadingService ==> executeAsyncTask2 method: ??????????????????{} ", i);
    }*/

    @Transactional
    public boolean uploadShipPartnerState(ShipPartnerStateParam param) throws MyException {

        List<ParamId> paramIds = checkWayBill(param.getWayBillNumber());
        List<Integer> ids = new ArrayList();
        for (int i = 0; i < paramIds.size(); i++) {
            ids.add(paramIds.get(i).getId());
        }
        System.out.println("?????????????????????1========" + param.getState());
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


        //????????????ID???????????????ID?????????????????????
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
                + "??? ids??? " + String.valueOf(wayBillIds)
                + "??? wayBillDetailsList??? " + String.valueOf(wayBillDetailsList));
        boolean res = wayBillDetailsList != null && wayBillDetailsList.size() > 0;
        if (res) {
            //???????????? ??????
            updateWayBillState(wayBillIds, WayBillState.SEND);
            //?????????????????? ???????????????
            updateSpEventState(wayBillIds, state);
            //?????? ???????????????????????????
//            ShipServiceFile.batchUpload(wayBillDetailsList); //???????????? SSF ???????????????
            System.out.println("?????????????????????2===============" + state);
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
            pointScanRecordService.addSysRecord(1, wayBill.getTrackingNumber(), null, "upline", new Date(), "??????????????????" + state);
        }

        return ResponseCode.SUCCESS_CODE;
    }

    // ???????????? ===== SSF
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

        //??????????????????????????????ID??????map
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
            //?????????????????? SSF??????
            updateSSF(wayBillIds, ssf);
            //?????? SSF???????????????
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

        //??????????????????????????????PDF
        int i = 0;
        for (String base64Str : wayBillCoding) {
            i++;
            String newPdfPath = tmpDirPath + i + ".pdf";
            PDFUtils.base64StringToPDF(base64Str, newPdfPath);
            pdfPaths.add(newPdfPath);
        }

        //?????????????????????pdf
        PDFUtils.mergePDF(pdfPaths, pdfPath);

        //??????????????????????????????PDF
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


    //????????????
    public WayBillVo getChangeSingleInfo(Integer wid) {
        return wayBillMapper.getChangeSingleInfo(wid);
    }


    public boolean updateRefund(WayBill wayBill) {
        //????????????
        long loginUserId = wayBill.getUserId();
        //????????????
        double price = 0;
        double warePrice = wayBill.getWarePrice();
        double billPrice = wayBill.getPrice();
        if (warePrice == 0) {
            price = billPrice;
        } else {
            price = warePrice;
        }
        //????????????????????????
        EntityWrapper<UserAccount> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id", loginUserId);
        UserAccount userAccount = userAccountService.selectOne(wrapper);
        double balance = userAccount.getBalance();
        userAccount.setBalance(balance + price);
        boolean isUserAccount = userAccountService.updateById(userAccount);

        //??????????????????
        DealDetail dealDetail = new DealDetail();
        dealDetail.setBalance(userAccount.getBalance());
        dealDetail.setDealAmount(price);
        dealDetail.setDealType(3);//1:??????,2?????????,3?????????
        dealDetail.setState(1);//????????????
        dealDetail.setUserId(loginUserId);
        dealDetail.setWayBillId(wayBill.getId());
        boolean insert = dealDetailService.insert(dealDetail);
        log.info("??????????????????-->", price);
        if (isUserAccount && insert) {
            return true;
        }
        return false;
    }


    //????????????ID????????????
    public Integer updateWeightById(double weight, Integer id) {
        WayBill wayBill = wayBillMapper.selectById(id);

        if ((wayBill.getBillWeight() >= 1 && weight < 1) || (wayBill.getBillWeight() < 1 && weight >= 1)) {
            return -2;//?????????????????????
        }

        double wBillWeight = wayBillVoService.getOnePrice(weight, wayBill.getZone(), wayBill.getChannel(), wayBill.getUserId());
        DecimalFormat format1 = new DecimalFormat("#.00");
        double price = Double.parseDouble(format1.format(wBillWeight));
        double billPrice = wayBill.getPrice();

        DifferenceVo difference = userAccountService.difference(price, billPrice, wayBill);
        if (difference.getCode() == 0) { //??????????????????
            if (wayBill.getWareWeight() == 0) {
                wayBill.setBillWeight(weight);
                wayBill.setPrice(price);
                wayBillMapper.updateById(wayBill);
            } else {
                wayBill.setWareWeight(weight);
                wayBill.setWarePrice(price);
                wayBillMapper.updateById(wayBill);
            }
            return 0;//??????
        } else if (difference.getCode() == -1) {
            return -1;//????????????
        } else {
            return 1;//???????????????
        }
    }

    //????????????????????????ID
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

    //????????????????????????????????????????????????
    public List<WayBill> selectTrackingList() {
        return wayBillMapper.selectTrackingList();
    }

    //????????????ID????????????Coding
    public String getCodingById(int id) {
        return wayBillMapper.getCodingById(id);
    }

    //?????????????????????????????????????????????
    public List<UserBatchWayBill> getNotIntoBatchWayBillList(Long loginUserId) {
        return wayBillMapper.getNotIntoBatchWayBillList(loginUserId);
    }

    //?????????????????????????????????????????????
    public Integer getNotIntoBatchWayBillListCount(Long loginUserId) {
        return wayBillMapper.getNotIntoBatchWayBillListCount(loginUserId);
    }

    //???????????????????????????????????????
    public List<WayBill> getShippingBatchWayBillList() {
        return wayBillMapper.getShippingBatchWayBillList();
    }

    //?????????????????????????????????????????????
    public Integer getShippingBatchWayBillListCount() {
        return wayBillMapper.getShippingBatchWayBillListCount();
    }

    //??????????????????????????????
    public List<WayBill> getNotJoinWayBill() {
        return wayBillMapper.getNotJoinWayBill();
    }

    public Integer getNotJoinWayBillCount() {
        return wayBillMapper.getNotJoinWayBillCount();
    }

    /**
     * ???????????????
     *
     * @param orderTrackingNumber
     * @return
     * @throws MyException
     */
    public String getNewTrackingNumber(String orderTrackingNumber, Integer pointType) throws MyException {
        if (pointType == 1) {
            WayBill wayBill = selectOne(new EntityWrapper<WayBill>().eq("tracking_number", orderTrackingNumber));
            if (wayBill == null) {
                throw new MyException("??????????????????");
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
        moduleVoList.setAllWaybills(wayBillMapper.getAllWayBills(loginUserId));//??????????????????
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


