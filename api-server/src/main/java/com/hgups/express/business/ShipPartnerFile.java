package com.hgups.express.business;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.hgups.express.domain.PortContact;
import com.hgups.express.util.LabelUtils;
import com.hgups.express.util.PathUtils;
import com.hgups.express.vo.WayBillVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class ShipPartnerFile {
    public static final String UPLOAD_FILE_NAME = "hgups2019.consolid";
    public static final String ACTION_CODE = "S0";
    public static final String USERNAME = "hgups2019";
    public static final String PASSWORD = "WG.U4V4nyJ8c2MW";
    public static final String CREDENTIALS = USERNAME + ":" + PASSWORD;
    public static final String AUTHORIZATION = "Basic " + Base64.encodeBase64String(CREDENTIALS.getBytes());
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final long HALF_DAY = 43200000;
    public static final String HEARDER_MID = "902769051";
    public static String mid = "902769051";
    public static String crid = "30692487";
    public static Map<String, Integer> sEventCodeMap = new HashMap<>();

    static {
        //初始化预上线状态对应的出、入库的状态
        sEventCodeMap.put("GX", 1);
        sEventCodeMap.put("80", 1);
        sEventCodeMap.put("81", 1);
        sEventCodeMap.put("82", 1);
        sEventCodeMap.put("83", 1);
        sEventCodeMap.put("84", 2);
        sEventCodeMap.put("85", 2);
        sEventCodeMap.put("86", 2);
        sEventCodeMap.put("87", 2);
        sEventCodeMap.put("89", 2);
    }

    public static boolean isValidState(String state) {
        return sEventCodeMap.containsKey(state);
    }

    public static void main(String[] args) {
//        File uploadFile = new File(PathUtils.resDir + UPLOAD_FILE_NAME);
//        if(uploadFile.exists()) {
//            boolean deleteRs = uploadFile.delete();
//            System.out.println(" deleteRs: " + deleteRs);
//        }
//
//        List<String> lines = new ArrayList<>();
//        lines.add(newHeaderRecord("00000000091",3,1, "20200723"));
//
//        PkgInfo info = new PkgInfo(
//                "92055902769052000100000015", "94603", "3000", "Joan Chen", "10918 Edes Ave", "Oakland", "CA", "94603", "3000", "18", "Kevin Wu", "54 Hightop LN", "Jericho", "NY", "11753", "1719", "54", "02", 0.3f);
//        PkgInfo info1 = new PkgInfo(
//                "92001902769052000100000029", "94603", "3000", "Joan Chen", "10918 Edes Ave", "Oakland", "CA", "94603", "3000", "18", "Kevin Wu", "54 Hightop LN", "Jericho", "NY", "11753", "1719", "54", "02", 0.3f);
//
//        lines.add(newPkgRecord("GX", "20200724", "111111", info, "20200723", "11753"));
//        lines.add(newPkgRecord("GX", "20200725", "121212", info1, "20200723", "11753"));
//        FileUtil.appendLines(lines, uploadFile, "UTF-8");
//
//        upload();
//        String testStr = fullBlank(6);
//        System.out.println(" testsTR:" + testStr + "||");
       /* String str = wrapTailBlank("asdfghj", 4);
        System.out.println(" testsTR:" + str + "||");*/
    }

    public static void batchUpload(final List<WayBillVo> wayBillDetailsList, String eventCode) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int total = wayBillDetailsList.size();
                log.info(" start batchUpload ");
                long timestamp = System.currentTimeMillis();
                String currDataStr = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN);

                File uploadFile = new File(PathUtils.resDir + SP_EVENT_DIR + timestamp + "/" + currDataStr + "_" + UPLOAD_FILE_NAME);
                log.info("currDataStr: " + currDataStr + ", fileName: " + uploadFile.getName());
                if (uploadFile.exists()) {
                    boolean deleteRs = uploadFile.delete();
                    log.info(" batchUpload deleteRs: " + deleteRs);
                    System.out.println(" deleteRs: " + deleteRs);
                }


                //减去12个小时，变成美国时区
                long time = System.currentTimeMillis() - HALF_DAY;
                Date date = new Date(time);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
                String currentDate = dateFormat.format(date);
                String currentTime = timeFormat.format(date);

                //USPS邮政编码 Queens P&DC facility (14-02 20th Ave, Queens, NY, 11356)
                String uspsEntryCode = "11356";
                List<String> lines = new ArrayList<>();
                increaseNo();
                lines.add(newHeaderRecord(fillHeadZero(noIndex, 11), total + 1, sEventCodeMap.get(eventCode), currentDate, HEARDER_MID));

                String pcrid = wayBillDetailsList.get(0).getPortContact().getCrid();
                String pmid = wayBillDetailsList.get(0).getPortContact().getMid();
                if (!StringUtils.isEmpty(pmid)){
                    mid=pmid;
                }
                if (!StringUtils.isEmpty(pcrid)){
                    crid=pcrid;
                }
                for (WayBillVo vo : wayBillDetailsList) {
                    PkgInfo info = PkgInfo.create(vo);
                    log.info(" create SSP info: " + String.valueOf(info));
                    lines.add(newPkgRecord(eventCode, currentDate, currentTime, info, currentDate, uspsEntryCode,crid,mid));
                }

                FileUtil.appendLines(lines, uploadFile, "UTF-8");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println(" batchUpload sleep error: " + String.valueOf(e));
                    log.info(" batchUpload sleep error: " + String.valueOf(e));
                    e.printStackTrace();
                }

                upload(uploadFile);
            }
        });
        thread.start();
    }

    public static void upload(File file) {

        String url = "https://pdx.usps.com/api/manifests";
        System.out.println(" AUTHORIZATION: " + AUTHORIZATION);
        HttpResponse response = HttpUtil.createPost(url)
                .header("Authorization", AUTHORIZATION)
                .header(Header.ACCEPT, CONTENT_TYPE_APPLICATION_JSON)
                .form("filename", file.getName())
//                .form("filename", UPLOAD_FILE_NAME)
                .form("environment", "PROD")
                .form("multipartFile", file)
                .execute();
        int statusCode = response.getStatus();
        String msg = response.body();
        boolean code = false;
        boolean codeP = false;
//        boolean code = file.delete();
//        boolean codeP = file.getParentFile().delete();
        log.info(" upload SSF statusCode: " + statusCode
                + "\n msg: " + msg
                + "， pFile: " + file.getParent()
                + "， codeP: " + codeP +
                "code: " + code);
    }

    /**
     * @param serialNo           11位的序列号
     * @param total              总的记录条数，包含头部信息
     * @param io                 1入库，还是2出库
     * @param dateTenderedToUSPS 邮件送达USPS的日期
     * @return
     */
    public static String newHeaderRecord(String serialNo, int total, int io, String dateTenderedToUSPS,String mid) {
        String spfn = LabelUtils.newNumber("92753" + mid + serialNo);
        System.out.println("parthnerFile===mid=="+mid);
        String record = "EH";
        record += mid;
        record += fillHeadZero(total, 9);
        record += io;
        record += dateTenderedToUSPS;
        record += fullBlank(6);
        record += "050";
        record += wrapTailBlank(spfn, 34);
        record += fullBlank(8);

        return record;
    }

    @Data
    public static class PkgInfo {
        public PkgInfo(String trackNo, String eventZipCode, String eventZipCode4,
                       String senderName, String senderAddress, String senderCity,
                       String senderState, String senderZipCode, String senderZipCode4,
                       String senderDO, String receiverName, String receiverAddress,
                       String receiverCity, String receiverState, String receiverZipCode,
                       String receiverZipCode4, String receiverDO, String domesticZone,
                       double weight, double wareWeight, String serviceType, String carrierRoute,
                       double price,double warePrice,double costPrice,
                       double length, double width, double height,
                       String receiverEmail, String senderEmail, double articleUnitPrice,
                       int articleNumber, String uspsZipCode) {
            this.trackNo = trackNo;
            this.eventZipCode = eventZipCode;
            this.eventZipCode4 = eventZipCode4;
            this.senderName = senderName;
            this.senderAddress = senderAddress;
            this.senderCity = senderCity;
            this.senderState = senderState;
            this.senderZipCode = senderZipCode;
            this.senderZipCode4 = senderZipCode4;
            this.senderDO = senderDO;
            this.receiverName = receiverName;
            this.receiverAddress = receiverAddress;
            this.receiverCity = receiverCity;
            this.receiverState = receiverState;
            this.receiverZipCode = receiverZipCode;
            this.receiverZipCode4 = receiverZipCode4;
            this.receiverDO = receiverDO;
            this.domesticZone = domesticZone;
            this.weight = weight;
            this.wareWeight = wareWeight;
            this.serviceType = serviceType;
            this.carrierRoute = carrierRoute;
            this.price = price;
            this.warePrice = warePrice;
            this.costPrice = costPrice;
            this.length = length;
            this.width = width;
            this.height = height;
            this.receiverEmail = receiverEmail;
            this.senderEmail = senderEmail;
            this.articleUnitPrice = articleUnitPrice;
            this.articleNumber = articleNumber;
            this.uspsZipCode = uspsZipCode;
        }

        String trackNo;
        String eventZipCode;
        String eventZipCode4;

        //取件地址
        String pickupAddress;
        String pickupCity;
        String pickupState;
        String pickupZipCode;
        String pickupZipCode4;
        String pickupDO;

        //发件人信息
        String senderName;
        String senderAddress;
        String senderCity;
        String senderState;
        String senderZipCode;
        String senderZipCode4;
        String senderDO;
        String senderEmail;

        //收件人信息
        String receiverName;
        String receiverAddress;
        String receiverCity;
        String receiverState;
        String receiverZipCode;
        String receiverZipCode4;
        String receiverDO;
        String carrierRoute;
        String receiverEmail;

        //USPS分配中心地址信息
        String uspsZipCode;

        //zone几
        String domesticZone = "2";

        //打单重量
        double weight;

        //核重重量
        double wareWeight;

        //体积
        double length;
        double width;
        double height;

        //服务类型
        String serviceType;

        //打单价格 人民币
        double price;

        //核重价格 人民币
        double warePrice;

        //成本价 美元
        double costPrice;

        //物品的单价
        double articleUnitPrice;

        //物品的数量
        int articleNumber;

        /**
         * 获取客户扣费的价格，包含核重的环节
         * @return
         */
        public double getUserPrice() {
            return warePrice == 0 ? price : warePrice;
        }

        /**
         * 获取包裹的重量，包含核重的环节
         * @return
         */
        public double getRealWeight() {
            return wareWeight == 0 ? weight : wareWeight;
        }

        public boolean isF() {
            return "F".equals(serviceType);
        }

        public String getServiceTypeCode() {
            return isF() ? "001" : "055";
        }

        public static PkgInfo create(WayBillVo vo) {
            PortContact portContact = vo.getPortContact();
            String uspsZipCode = vo.getPortContact() != null ? vo.getPortContact().getUspsCode() : "";
            log.info(", uspsZipCode: " + uspsZipCode + "， vo： " + vo);
            PkgInfo info = new PkgInfo(
                    vo.getWayBill().getTrackingNumber(),
                    portContact.getConCode(),
                    portContact.getConCodet(),
                    portContact.getConAddressOne(),
                    portContact.getConAddressTwo(),
                    portContact.getConEcity(),
                    portContact.getConEprovince(),
                    portContact.getConCode(),
                    portContact.getConCodet(),
                    "18",
                    vo.getWaybillContact().getReceiveName(),
                    vo.getWaybillContact().getReceiveAddressTwo(),
                    vo.getWaybillContact().getReceiveCity(),
                    vo.getWaybillContact().getReceiveProvince(),
                    vo.getWaybillContact().getReceivePostalCode(),
                    vo.getWaybillContact().getReceivePostalCodet(),
                    vo.getWayBill().getDeliveryPoint(),
                    vo.getWayBill().getZone(),
                    vo.getWayBill().getBillWeight(),
                    vo.getWayBill().getWareWeight(),
                    vo.getWayBill().getService(),
                    vo.getWayBill().getCarrierRoute(),
                    vo.getWayBill().getPrice(),
                    vo.getWayBill().getWarePrice(),
                    vo.getWayBill().getUserWaybillPrice(),
                    vo.getParcel().getLengths(),
                    vo.getParcel().getWidth(),
                    vo.getParcel().getHeight(),
                    vo.getWaybillContact().getReceiveEmail(),
                    portContact.getConEmail(),
                    vo.getArticleList().get(0).getPrice(),
                    vo.getArticleList().get(0).getNumber(),
                    uspsZipCode);

            return info;
        }
    }

    /**
     * @param eventCode
     * @param eventDate            发起预上线的日期 （这两个信息用来显示在USPS查询轨迹中）
     * @param eventTime            发起预上线的时间
     * @param info                 包裹信息
     * @param dateTenderedToUSPS   期望送到USPS的时间
     * @param defEntryFacilityZipCode USPS邮局的邮政编码
     * @return
     */
    public static String newPkgRecord(String eventCode, String eventDate, String eventTime,
                                      PkgInfo info, String dateTenderedToUSPS, String defEntryFacilityZipCode
                                      ,String crid,String mid) {

        String record = "ED";
        record += wrapTailBlank(info.trackNo, 34);
        record += eventCode;
        record += info.eventZipCode;
        record += StringUtils.isEmpty(info.eventZipCode4) ? fullBlank(4) : info.eventZipCode4;
        record += eventDate;
        record += eventTime;
        record += mid;
        record += wrapTailBlank(crid, 15);
        record += wrapTailBlank(info.receiverName, 48);
        record += wrapTailBlank(info.receiverAddress, 48);
        record += wrapTailBlank(info.receiverCity, 28);
        record += info.receiverState;
        record += info.receiverZipCode;
        record += StringUtils.isEmpty(info.receiverZipCode4) ? fullBlank(4) : info.receiverZipCode4;
        record += StringUtils.isEmpty(info.receiverDO) ? fullBlank(2) : info.receiverDO;
        record += ACTION_CODE;
        record += dateTenderedToUSPS;
        record += dateTenderedToUSPS;
        record += "A";
        record += StringUtils.isEmpty(info.uspsZipCode) ? defEntryFacilityZipCode : info.uspsZipCode;
        record += fullBlank(2);  //Customer Delivery Preference ???
        record += fullBlank(30);  // Customer Reference Number 1、Customer Type ???
        record += fullBlank(1);  //Customer Type  ???
        record += "07";
        record += fillHeadZero(0, 8); //Indicium Creation Record Date
        record += fullBlank(24);  //Meter 2+ 20 + 2

//        record += wrapTailBlank(info.pickupAddress, 48);
//        record += wrapTailBlank(info.pickupCity, 28);
//        record += info.pickupState;
//        record += info.pickupZipCode;
//        record += info.pickupZipCode4;
//        record += info.pickupDO;
        //提货地点和发货地点一样
        record += wrapTailBlank(info.senderAddress, 48);
        record += wrapTailBlank(info.senderCity, 28);
        record += info.senderState;
        record += info.senderZipCode;
        record += StringUtils.isEmpty(info.senderZipCode4) ? fullBlank(4) : info.senderZipCode4;
        record += info.senderDO;

        record += wrapTailBlank(info.senderName, 48);
        record += wrapTailBlank(info.senderAddress, 48);
        record += wrapTailBlank(info.senderCity, 28);
        record += info.senderState;
        record += info.senderZipCode;
        record += StringUtils.isEmpty(info.senderZipCode4) ? fullBlank(4) : info.senderZipCode4;
        record += info.senderDO;

        record += "US"; //Destination Country Code
        record += fullBlank(4); //Rate Category    ???
        record += info.isF() ? "SP" : "C1"; //Rate Indicator  ???
        record += 3; //Processing Category ???
        record += fillHeadZero(0, 7); // Value of Article ???
        record += fillHeadZero(0, 7); // 邮政费用 ???
        record += fillHeadZero(Long.parseLong(info.domesticZone), 2); //zone几，是两位
        record += fullBlank(34); // Container ID  ???
        record += fullBlank(2); // Container Type ???
        record += 1; // Unit of measurement for mailpiece weigh   1=LBS 2=OZ 3=KILOS
        record += getFormatWeight(info.getRealWeight());    // Weight
        record += "000000";  // Dimensional Weight ??? 体积重量
        record += "01100";  // Length ???
        record += "01100";  // Width ???
        record += "01100";  // Height ???

        return record;
    }

    private static String getFormatWeight(double weight) {
        String src = String.valueOf(weight);
        String[] arr = src.split("\\.");
        String header = fillHeadZero(Integer.parseInt(arr[0]), 5);
        String tail = wrapTailBlank(arr[1], 4);
        tail = tail.replaceAll(" ", "0");
        return header + tail;
    }

    public static String getFormatDouble(double weight, int start, int end) {
        String src = String.valueOf(weight);
        String[] arr = src.split("\\.");
        String header = fillHeadZero(Integer.parseInt(arr[0]), start);
        String tail = wrapTailBlank(arr[1], end);
        tail = tail.replaceAll(" ", "0");
        return header + tail;
    }


    public static class Field {
        public int maxLen;
        public String value;

        public Field(String value, int maxLen) {
            this.maxLen = maxLen;
            this.value = value;
        }

        public String builder() {

            return maxLen == value.length() ? value : wrapTailBlank(value, maxLen - value.length());
        }
    }

    public static String fullBlank(int n) {
        return String.format("%" + (n) + "s", " ");
    }

    public static String fullZero(int n) {
        String result = "";
        for (int i = 1; i <= n; i++) {
            result += "0";
        }
        return result;
    }

    public static String wrapTailBlank(String prefix, int total) {
        if(prefix == null) {
            prefix = "";
        }

        int len = prefix.length();
        if (len >= total) {
            return prefix.substring(0, total);
        }
        String str = String.format("%s%" + (total - prefix.length()) + "s", prefix, " ");
        return str;
    }

    public static String wrapHeadBlank(String value, int total) {
        String str = String.format("%" + total + "s", value);
        return str;
    }

    public static String fillHeadZero(long value, int total) {
        return String.format("%0" + total + "d", value);
    }


    private static long noIndex = 1;

    public static final String NO_FILE = "spEventNoFile.txt";
    public static final String SP_EVENT_DIR = "spEvent/";
    public static String NO_FILE_PATH = "";

    public static void initSpEventFile(String resDir) {
        NO_FILE_PATH = resDir+ SP_EVENT_DIR + NO_FILE;
        String currNo = FileUtil.readString(NO_FILE_PATH, "UTF-8");
        log.info(" initSpEventFile currNo: " + currNo);
        noIndex = Long.parseLong(currNo.trim());
        log.info(" initSpEventFile noIndex: " + noIndex);
    }

    public static void increaseNo() {
        noIndex++;
        FileUtil.writeString(String.valueOf(noIndex), NO_FILE_PATH, "UTF-8");
        log.info(" increaseNo noIndex: " + noIndex);
    }

}
