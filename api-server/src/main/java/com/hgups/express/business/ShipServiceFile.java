package com.hgups.express.business;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.hgups.express.util.LabelUtils;
import com.hgups.express.util.PathUtils;
import com.hgups.express.vo.WayBillVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hgups.express.business.ShipPartnerFile.*;

/**
 * 用来进行收费用的
 */
@Slf4j
public class ShipServiceFile {
    private static final String SSF_FILE_NAME = "hgups2019.manifest";
    public static String mid = "903051998";
    public static String crid = "30692487";

    public static void main(String[] args) {
        log.info("D" + fullZero(2) + "D");
        log.info(getFormatDouble(3, 4, 3));
        log.info(getFormatDouble(3.2355 * 7.1, 4, 3));
        log.info("1" + wrapTailBlank("", 3) + "1");

    }

    public static String newHeaderRecord(String serialNo, int total, String currentDate,
                                         String currentTime, String currentSimpleTime, String mid, String portCode) {
        String spfn = LabelUtils.newNumber("92750" + mid + serialNo);
        String uspsEntryCode5 = portCode;
        String uspsEntryCode4 = "    "; // ZipCode4 In Future ????

        System.out.println("MID============" + mid);
        String record = "H1";
        record += wrapTailBlank(spfn, 34);
        record += 1; // Electronic File Type
        record += currentDate; // Date of Mailing
        record += currentTime; // Time of Mailing
        record += " "; // Entry Facility Type
        record += uspsEntryCode5; // Entry Facility zipCode5
        record += uspsEntryCode4; // Entry Facility zipCode4  ????????
        record += fullBlank(2); //
        record += fullBlank(3); //
        record += fullZero(6); //
        record += "03"; // Containerization Indicator
        record += "017"; // Version
        record += currentDate + currentSimpleTime;
        record += fullBlank(4); //Software VendorCode
        record += fullBlank(8); //Product Version Number
        record += fillHeadZero(total, 9);
        record += mid;
        record += fullBlank(11);
        return record;
    }

    public static String newDetailRecord1(PkgInfo info) {
        String record = "D1";
        record += wrapTailBlank(info.trackNo, 34);
        record += info.isF() ? "FC" : "PM";
        record += info.getServiceTypeCode() + " ";
        record += wrapTailBlank("C10", 4);
        record += info.receiverZipCode;
        record += StringUtils.isEmpty(info.receiverZipCode4) ? fullBlank(4) : info.receiverZipCode4;
        record += " ";  //Destination Facility Type
        record += fullBlank(2);
        record += fullBlank(11);
        record += wrapTailBlank(info.carrierRoute, 5);
        record += fullBlank(9);
        record += fullBlank(9);   // MID
        record += fullBlank(34);
        record += fullBlank(2);
        record += fullBlank(34);
        record += fullBlank(2);
        record += fullBlank(34);
        record += fullBlank(2);
        record += fullBlank(15);
        record += fullBlank(30);
        record += fullBlank(15);
        record += fullZero(8);
        record += fullZero(6);
        record += fillHeadZero(56651, 10);
        record += "01";
        record += "20260";
        record += fullBlank(20);
        record += fullBlank(6);
        record += getFormatDouble(info.costPrice, 4, 3); //Postage，成本价
        record += "C";
        record += fullBlank(22);
        record += fullBlank(14);
        record += 1;
        record += getFormatDouble(info.getRealWeight(), 5, 4);
        record += 3;  //包装类型
        record += "SP";
        record += "N";
        record += "0" + info.domesticZone;
        record += getFormatDouble(info.length, 3, 2);
        record += getFormatDouble(info.width, 3, 2);
        record += getFormatDouble(info.height, 3, 2);
        record += getFormatDouble(info.getRealWeight(), 4, 2);  //体积重
        record += "920";
        record += fullZero(6);
        record += fullBlank(3);
        record += fullZero(6);
        record += fullBlank(3);
        record += fullZero(6);
        record += fullBlank(3);
        record += fullZero(6);
        record += fullBlank(3);
        record += fullZero(6);
        record += getFormatDouble(info.articleUnitPrice * info.articleNumber, 5, 2);  //Value of Article
        record += fullBlank(6);
        record += fullBlank(4);
        record += fullBlank(2);
        record += fullBlank(7);
        record += fullBlank(2);
        record += fullBlank(7);
        record += fullBlank(2);
        record += fullBlank(2);
        record += fullBlank(7);
        record += fullBlank(9);
        record += fullBlank(1);
        record += "1";
        record += fullBlank(2);
        record += "N";
        record += fullBlank(1);
        record += 1; //Delivery Option Indicator
        record += StringUtils.isEmpty(info.receiverDO) ? fullBlank(2) : info.receiverDO;
        record += fullBlank(1);
        record += fullBlank(2);
        record += fullBlank(4);
        record += fullBlank(34);
        record += fullBlank(30); // Internal reference number
        record += wrapTailBlank(info.receiverName, 48);
        record += wrapTailBlank(info.receiverAddress, 48);
        record += fullBlank(3);

        record += fullBlank(9);
        record += fullBlank(16);
        record += fullBlank(48);
        record += fullBlank(28);
        record += fullBlank(2);
        record += fullBlank(5);
        record += fullBlank(15);
        record += fullBlank(88);
        return record;
    }

    public static String newDetailRecord2(PkgInfo info) {
        log.info(" newDetailRecord2 info: " + info);
        String record = "D2";
        record += wrapTailBlank(info.trackNo, 34);
        record += fullBlank(96);
        record += wrapTailBlank(info.receiverCity, 28);
        record += info.receiverState;
        record += info.receiverZipCode;
        record += StringUtils.isEmpty(info.receiverZipCode4) ? fullBlank(4) : info.receiverZipCode4;
        record += StringUtils.isEmpty(info.receiverEmail) ? fullBlank(64) : wrapTailBlank(info.receiverEmail, 64);
        record += fullBlank(64); //接收者接收短信的手机；
        record += wrapTailBlank(info.senderName, 48);
        record += wrapTailBlank(info.senderEmail, 64);
        record += fullBlank(64);   //发送者接收短信的手机；
        record += fullBlank(25);
        return record;
    }

    public static void batchUpload(final List<WayBillVo> wayBillDetailsList) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int total = wayBillDetailsList.size();
                log.info(" start batchUpload ");
                long timestamp = System.currentTimeMillis();
                String currDataStr = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN);

                File uploadFile = new File(PathUtils.resDir + SSF_DIR + timestamp + "/" + currDataStr + "_" + SSF_FILE_NAME);
                if (uploadFile.exists()) {
                    boolean deleteRs = uploadFile.delete();
                    log.info(" batchUpload deleteRs: " + deleteRs);
                }

                //减去12个小时，变成美国时区
                long time = System.currentTimeMillis() - HALF_DAY;
                Date date = new Date(time);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HHmm");
                String currentDate = dateFormat.format(date);
                String currentTime = timeFormat.format(date);
                String currentSimpleTime = simpleTimeFormat.format(date);

                //USPS邮政编码 Queens P&DC facility (14-02 20th Ave, Queens, NY, 11356)
                String uspsEntryCode5 = "11356";
                String uspsEntryCode4 = "xxxx";
                List<String> lines = new ArrayList<>();
                increaseNo();

//                String pmid = wayBillDetailsList.get(0).getPortContact().getMid();
//                if (!StringUtils.isEmpty(pmid)){
//                    mid=pmid;
//                }
                String portCode = total == 0 ? "" : wayBillDetailsList.get(0).getPortContact().getPortCode();
                log.info(" SSF portCode: " + portCode);
                lines.add(newHeaderRecord(fillHeadZero(noIndex, 11), (total * 2) + 1, currentDate, currentTime, currentSimpleTime, mid, portCode));

                for (WayBillVo vo : wayBillDetailsList) {
                    PkgInfo info = PkgInfo.create(vo);
                    log.info(" create ssf info: " + String.valueOf(info));
                    lines.add(newDetailRecord1(info));
                    lines.add(newDetailRecord2(info));
                }

                FileUtil.appendLines(lines, uploadFile, "UTF-8");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.warn(" upload SSF sleep error: " + String.valueOf(e));
                    e.printStackTrace();
                }
                upload(uploadFile);
            }
        });

        thread.start();

    }


    public static void upload(File file) {
        String url = "https://pdx.usps.com/api/manifests";
        log.info(" AUTHORIZATION: " + AUTHORIZATION);
        HttpResponse response = HttpUtil.createPost(url)
                .header("Authorization", AUTHORIZATION)
                .header(Header.ACCEPT, CONTENT_TYPE_APPLICATION_JSON)
                .form("filename", file.getName())
//                .form("filename", SSF_FILE_NAME)
                .form("environment", "PROD")
                .form("environment", "TEM")
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

    private static long noIndex = 1;

    public static final String NO_FILE = "ssfNoFile.txt";
    public static final String SSF_DIR = "ssf/";
    public static final String NO_FILE_PATH = PathUtils.resDir + SSF_DIR + NO_FILE;

    public static void initSSFNoFile() {
        log.info(" bino NO_FILE_PATH: " + NO_FILE_PATH);
        String currNo = FileUtil.readString(NO_FILE_PATH, "UTF-8");
        log.info(" initSSFNoFile currNo: " + currNo);
        noIndex = Long.parseLong(currNo.trim());
        log.info(" initSSFNoFile noIndex: " + noIndex);
    }

    public static void increaseNo() {
        noIndex++;
        FileUtil.writeString(String.valueOf(noIndex), NO_FILE_PATH, "UTF-8");
        log.info(" increaseNo noIndex: " + noIndex);
    }

}
