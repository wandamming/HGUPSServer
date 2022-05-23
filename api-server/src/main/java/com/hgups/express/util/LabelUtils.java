package com.hgups.express.util;

import cn.hutool.core.io.FileUtil;
import com.hgups.express.business.ShipPartnerFile;
import com.hgups.express.domain.PortContact;
import com.hgups.express.service.usermgi.RightsManagementService;
import com.hgups.express.vo.WayBillVo;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;

@Slf4j
@Component
public class LabelUtils {
    private static final int MAX_LENGTH_LINE = 40;
    private static String trackNoFilePath;
    @Resource
    private RightsManagementService rightsManagementService;
    private static RightsManagementService rightsManagementServiceStatic;

    @PostConstruct
    public void init() {
        rightsManagementServiceStatic = rightsManagementService;
    }

    @Value(value = "${track.no.path}")
    public void setTrackNoFilePath(String no) {
        trackNoFilePath = no;
    }

    //Production
//    public static final String trackNoFilePath = "//opt/crafttime/hgups/data/track_no_index.txt";
//    public static final String logoDir = "//opt/crafttime/hgups/static/";

    //Bingo PC
//    public static final String trackNoFilePath = "//Users/Bingo/Downloads/track_no_index.txt";
//    public static final String logoDir = "/Users/Bingo/Downloads/";

    //JiaiBin PC
//    public static final String logoDir = "D:/Download/";

    private static long noIndex = 3;
    private static final String FNC1_1 = "~213";
    private static final String FNC1_2 = "~212";

    public static void main(String[] args) {

//        String no = newTrackNo("00010000002", true);
//        System.out.println(" no : " + no);
//        long halfDay = 43200000;
//        long time = System.currentTimeMillis() - halfDay;
//        Date date = new Date(time);
//        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
//        SimpleDateFormat format2 = new SimpleDateFormat("HHmmss");
//        System.out.println(format1.format(date));
//        System.out.println(format2.format(date));

        /*String trackAreaTitle = "USPS TRACKING # eVS";
        String serialNo = "";
        String trackNo = "";
        boolean isF = false;
        synchronized (sLock) {
//            increaseTrackNo();
            serialNo = ShipPartnerFile.fillHeadZero(noIndex, 11);
            trackNo = newTrackNo(serialNo, isF);
        }

        String barCodeTrackNo = DEF_TRACK_NO_PREFIX + "11753" + trackNo;
        String trackNoFile = "/Users/Bingo/Downloads/barcode222.png";
        BarcodeUtil.generateFile(barCodeTrackNo, trackNoFile);

        StringTokenizer st = new StringTokenizer("(420)n5+(92)n3", "()", false);
        log.info("bingo st.size:" +st.countTokens());
        while (st.hasMoreTokens()) {
            log.info("bingo st.nextToken:" + st.nextToken());
        }*/
    }

    public static class Label {
        public String base64;
        public String trackNo;

        public Label(String base64, String trackNo) {
            this.base64 = base64;
            this.trackNo = trackNo;
        }
    }

    public static Label createLabel(WayBillVo vo) {
        String route = vo.getWayBill().getCarrierRoute();
        try {
            //pdf路径
//           String pdfPath = "D:/Download/billPdf.pdf";
//            String pdfPath = "/tmp/billPdf.pdf";
            String flag = System.currentTimeMillis() + "_" + vo.hashCode();
            String pdfPath = PathUtils.resDir + "waybill/" + flag + "/waybill.pdf";
            File pdfFile = new File(pdfPath);
            if (!pdfFile.exists()) {
                pdfFile.getParentFile().mkdirs();
                pdfFile.createNewFile();
            }
            // 创建文件
            int pdfWidth = 288, pdfHeight = 432;
            Document document = new Document(new Rectangle(pdfWidth, pdfHeight));
            // 建立一个书写器
            PdfWriter writer = PdfWriter.getInstance(document,
                    new FileOutputStream(pdfPath));

            //打开文件
            document.open();

            //创建内容文本器
            PdfContentByte content = writer.getDirectContent();
            PdfTemplate pt = content.createTemplate(pdfWidth, pdfHeight);
            content.addTemplate(pt, 1f, 0, 0, 1f, 0, 0);

            //下面添加内容

            //设置起、始点
            int startX = 4, starY = 5, endX = 284, endY = 428;
            int fakeStartX = startX, fakeStartY = pdfHeight - starY;
            float defLineW = 0.7f;
            //外层的边框
            Rectangle rect = new Rectangle(startX, starY, endX, endY);//文本框位置
            rect.setBorder(Rectangle.BOX);//显示边框，默认不显示，常量值：LEFT, RIGHT, TOP, BOTTOM，BOX,
            rect.setBorderWidth(defLineW);//边框线条粗细
            rect.setBorderColor(BaseColor.BLACK);//边框颜色
            content.rectangle(rect);

            content.setLineWidth(defLineW);
            content.moveTo(startX + 71, fakeStartY);
            content.lineTo(startX + 71, fakeStartY - 76);

            content.moveTo(startX + 71, fakeStartY - 76);
            content.lineTo(startX, fakeStartY - 76);

            content.moveTo(startX + 71, fakeStartY - 76);
            content.lineTo(endX, fakeStartY - 76);

            Rectangle rect1 = new Rectangle(startX + 188, fakeStartY - 16, startX + 255, fakeStartY - 53);//文本框位置
            rect1.setBorder(Rectangle.BOX);//显示边框，默认不显示，常量值：LEFT, RIGHT, TOP, BOTTOM，BOX,
            rect1.setBorderWidth(defLineW);//边框线条粗细
            rect1.setBorderColor(BaseColor.BLACK);//边框颜色
            content.rectangle(rect1);

            //路由通道的框
            if (!StringUtils.isEmpty(route)) {
                Rectangle rect2 = new Rectangle(startX + 235, fakeStartY - 190, startX + 271, fakeStartY - 203);//文本框位置
                rect2.setBorder(Rectangle.BOX);//显示边框，默认不显示，常量值：LEFT, RIGHT, TOP, BOTTOM，BOX,
                rect2.setBorderWidth(defLineW);//边框线条粗细
                rect2.setBorderColor(BaseColor.BLACK);//边框颜色
                content.rectangle(rect2);
            }

            content.moveTo(startX, fakeStartY - 76 - 31);
            content.lineTo(endX, fakeStartY - 76 - 31);
            content.stroke();

            content.setLineWidth(4f);
            content.moveTo(startX, fakeStartY - 276);
            content.lineTo(endX, fakeStartY - 276);


            content.moveTo(startX, fakeStartY - 386);
            content.lineTo(endX, fakeStartY - 386);

            //恢复默认的线条高度
            content.setLineWidth(4f);
            content.stroke();

            //顶部面单类型区域
            boolean isF = false;
            if (vo.getWayBill().getWareWeight() <= 0) {
                isF = vo.getParcel().getBillWeight() < 1;
            } else {
                isF = vo.getWayBill().getWareWeight() < 1;
            }
            String type = isF ? "F" : "P";
            String typeDesc = isF ? "USPS FIRST-CLASS PKG" : "USPS PRIORITY MAIL";
            String typeTitleDesc1 = isF ? "FIRST-CLASS PKG" : "PRIORITY MAIL";
            float typeTitleDesc1Offset = isF ? 0 : 5;
            String typeTitleDesc2 = "U.S. POSTAGE PAID";
            String typeTitleDesc3 = "NAEET";
            String typeTitleDesc4 = "eVS";
            float offsetTypeDesc = isF ? 12 : 37;
            //加粗的矩阵
            float ta = 1f, tb = 0f, tc = 0f, td = 1f, tx = 0f, ty = 0f;
            ta = ta + 0.15f;
            td = td + 0.05f;
            ty = ty - 0.15f;

            BaseFont baseF = BaseFont.createFont();
            BaseFont baseFBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
//
            content.beginText();
            content.setFontAndSize(baseFBold, 90);
            content.setTextMatrix(ta, tb, tc, td, tx, ty);//这里对文字进行变形以达到想要的效果
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, type, startX + 8, fakeStartY - 71, 0);

//            pt.saveState();
//            pt.beginText();
//            pt.setFontAndSize(baseF, 90);
//            pt.moveText(15,15);
//            pt.setTextMatrix(ta, tb, tc, td, tx, ty);//这里对文字进行变形以达到想要的效果
//            pt.showText(type);
//            pt.endText();
//            pt.stroke();
//            pt.restoreState();
//            content.beginText();

            content.setFontAndSize(baseFBold, 21);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, typeDesc, startX + offsetTypeDesc, fakeStartY - 100, 0);

            content.setFontAndSize(baseF, 6);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, typeTitleDesc1, startX + 193, fakeStartY - 24, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, typeTitleDesc2, startX + 193, fakeStartY - 32, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, typeTitleDesc3, startX + 193, fakeStartY - 40, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, typeTitleDesc4, startX + 193, fakeStartY - 48, 0);

            //中间联系人区域
            //发件人
            PortContact portContact = vo.getPortContact();
            log.info("入境口岸中间表----》》》" + portContact.toString());
            log.info("入境口岸信息=====》》》》" + vo.getWayBill().getEntrySite());
            log.info("Zone信息=====》》》》" + vo.getWayBill().getZone());
            log.info("联系人信息=====》》》》" + portContact.getConName());
            String senderName = portContact.getConName();
            String senderFirm = portContact.getConCompany();
            String senderMainAddress = portContact.getConAddressTwo();
            String senderSuiteNumber = portContact.getConAddressOne();
            String senderCity = portContact.getConEcity();
            String senderState = portContact.getConEprovince();
            String senderZipCode5 = portContact.getConCode();
            String senderZipCode4 = StringUtils.isEmpty(portContact.getConCodet()) ? "" : portContact.getConCodet();
            String senderLastLine = senderCity + " " + senderState + " " + senderZipCode5;
            if (!StringUtils.isEmpty(senderZipCode4)) {
                senderLastLine += "-" + senderZipCode4;
            }
            //String senderPhone = "5102829691";
            String senderPhone = portContact.getConPhone();

            //收件人
            String receiverName = vo.getReceive().getName();
            String receiverFirm = vo.getReceive().getCompany();
            String receiverMainAddress = vo.getReceive().getAddressTwo();
            String receiverSuiteNumber = vo.getReceive().getAddressOne();
            String receiverCity = vo.getReceive().getCityEname();
            String receiverState = vo.getReceive().getProvinceEname();
            String receiverZipCode5 = vo.getReceive().getPostalCode();
            String receiverZipCode4 = StringUtils.isEmpty(vo.getReceive().getPostalCodet()) ? "" : vo.getReceive().getPostalCodet();
            String receiverLastLine = receiverCity + " " + receiverState + " " + receiverZipCode5;
            if (!StringUtils.isEmpty(receiverZipCode4)) {
                receiverLastLine += "-" + receiverZipCode4;
            }
            //String receiverPhone = "8103777777";
            String receiverPhone = vo.getReceive().getPhone();

            if (!StringUtils.isEmpty(route)) {
                //路由通道
                content.setFontAndSize(baseFBold, 10);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "", startX + 236, fakeStartY - 130, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, route, startX + 238, fakeStartY - 200, 0);
            }
            int senderY = 120;
            float yOffset = 11.3f;
            int xOffset = 8;
            content.setFontAndSize(baseF, 9);

            if (!StringUtils.isEmpty(senderName)) {
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, senderName.toUpperCase(), startX + xOffset, fakeStartY - senderY, 0);
                senderY += yOffset;
            }

            if (!StringUtils.isEmpty(senderFirm)) {
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, senderFirm.toUpperCase(), startX + xOffset, fakeStartY - senderY, 0);
                senderY += yOffset;
            }

            if (!StringUtils.isEmpty(senderSuiteNumber)) {
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, senderSuiteNumber.toUpperCase(), startX + xOffset, fakeStartY - senderY, 0);
                senderY += yOffset;
            }

            if (!StringUtils.isEmpty(senderMainAddress)) {
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, senderMainAddress.toUpperCase(), startX + xOffset, fakeStartY - senderY, 0);
                senderY += yOffset;
            }

            if (!StringUtils.isEmpty(senderLastLine)) {
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, senderLastLine.toUpperCase(), startX + xOffset, fakeStartY - senderY, 0);
            }

            int receiverY = 200;
            int receiverXOffset = 35;
            content.setFontAndSize(baseF, 10);

            if (!StringUtils.isEmpty(receiverName)) {
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, receiverName.toUpperCase(), startX + receiverXOffset, fakeStartY - receiverY, 0);
                receiverY += yOffset;
            }

            if (!StringUtils.isEmpty(receiverFirm)) {
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, receiverFirm.toUpperCase(), startX + receiverXOffset, fakeStartY - receiverY, 0);
                receiverY += yOffset;
            }

            if (!StringUtils.isEmpty(receiverSuiteNumber)) {

                if(receiverSuiteNumber.length() <= MAX_LENGTH_LINE) {
                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, receiverSuiteNumber, startX + receiverXOffset, fakeStartY - receiverY, 0);
                    receiverY += yOffset;
                } else {
                    String line1 = receiverSuiteNumber.substring(0, MAX_LENGTH_LINE);
                    String line2 = receiverSuiteNumber.substring(MAX_LENGTH_LINE);

                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, line1, startX + receiverXOffset, fakeStartY - receiverY, 0);
                    receiverY += yOffset;

                    content.showTextAligned(PdfContentByte.ALIGN_LEFT, line2, startX + receiverXOffset, fakeStartY - receiverY, 0);
                    receiverY += yOffset;
                }

            }
            if (!StringUtils.isEmpty(receiverMainAddress)) {
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, receiverMainAddress.toUpperCase(), startX + receiverXOffset, fakeStartY - receiverY, 0);
                receiverY += yOffset;
            }

            if (!StringUtils.isEmpty(receiverLastLine)) {
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, receiverLastLine.toUpperCase(), startX + receiverXOffset, fakeStartY - receiverY, 0);
            }

            //二维码区域
            String qrCodePath = PathUtils.resDir + "website_logo.png";
            //公司图标
            Image qrCodeImage = Image.getInstance(qrCodePath);
            //图片的位置（坐标）
            qrCodeImage.setAbsolutePosition(startX + 236, fakeStartY - senderY - 10);
            // image of the absolute
            qrCodeImage.scalePercent(15);
            content.addImage(qrCodeImage);

            //条形码区域
            String trackAreaTitle = "USPS TRACKING # eVS";
            String serialNo = "";
            String trackNo = "";
            synchronized (sLock) {
                increaseTrackNo();
                String pcrid = vo.getPortContact().getCrid();
                if (!StringUtils.isEmpty(pcrid)) {
                    crid = pcrid;
                }
                String pmid = vo.getPortContact().getMid();
                if (!StringUtils.isEmpty(pmid)) {
                    mid = pmid;
                }
                serialNo = ShipPartnerFile.fillHeadZero(noIndex, 11);
                trackNo = newTrackNo(serialNo, isF);
            }


//            String barCodeTrackNo = "(" + DEF_TRACK_NO_PREFIX + ")" + receiverZipCode5 + "(" + CHANNEL_APP_ID + ")" + (isF ? LABEL_SERVICE_TYPE_F : LABEL_SERVICE_TYPE_P) + CRID + serialNo;
//            String barCodeTrackNo = FNC1_1 + DEF_TRACK_NO_PREFIX + receiverZipCode5 + FNC1_2 + trackNo + "~m25";
            String barCodeTrackNo = DEF_TRACK_NO_PREFIX + receiverZipCode5 + trackNo;

            log.info(" barCodeTrackNo: " + barCodeTrackNo);
            content.setFontAndSize(baseFBold, 11);
            content.showTextAligned(PdfContentByte.ALIGN_CENTER, trackAreaTitle, startX + 140, fakeStartY - 290, 0);
            content.showTextAligned(PdfContentByte.ALIGN_CENTER, addBlankinMiddle(trackNo), startX + 143, fakeStartY - 379, 0);

            Image trackNoImg = Image.getInstance(BarcodeUtil.generate(barCodeTrackNo));
            //图片的位置（坐标）
            trackNoImg.setAbsolutePosition(27, 68);
            trackNoImg.scalePercent(96);
            // image of the absolute
            content.addImage(trackNoImg);

            //公司的文字（二选一）
            String company = "NAEET";
            content.setFontAndSize(baseF, 18);
            content.showTextAligned(Element.ALIGN_LEFT | Element.ALIGN_BOTTOM, company, 220, 15, 0);

//            //公司logo（二选一）
//            String logoPath = PathUtils.resDir + "hgups_logo.png";
//            String baoGuanLogoPath = PathUtils.resDir + "baoguan_logo.png";
//            //公司图标
//            Image image = Image.getInstance(logoPath);
//            //图片的位置（坐标）
//            image.setAbsolutePosition(240, 10);
//            // image of the absolute
//            image.setWidthPercentage(10);
//            image.scalePercent(7);//依照比例缩放
//            content.addImage(image);

            //底部备注区域
            String desc = vo.getParcel().getAritcleDescribe(), remark = vo.getParcel().getCommentOne(), entry = vo.getWayBill().getEntrySite() + " Entry";
            content.setFontAndSize(baseF, 7);
            content.showTextAligned(Element.ALIGN_LEFT | Element.ALIGN_BOTTOM, desc, startX + 7, 30, 0);
            content.showTextAligned(Element.ALIGN_LEFT | Element.ALIGN_BOTTOM, remark == null ? "" : remark, startX + 7, 20, 0);
            content.showTextAligned(Element.ALIGN_LEFT | Element.ALIGN_BOTTOM, entry, startX + 7, 10, 0);


            int processRole = rightsManagementServiceStatic.isProcessRole(vo.getWayBill().getUserId());
            System.out.println("===========processRole===" + processRole);
            System.out.println("是否是后程用户=====" + processRole);
//            if (1 != processRole) {
//                //报关公司图标
//                Image image2 = Image.getInstance(baoGuanLogoPath);
//                //图片的位置（坐标）
//                image2.setAbsolutePosition(210, 7);
//                // image of the absolute
//                image2.scalePercent(5);//依照比例缩放
//                content.addImage(image2);
//            }

            //结束内容的输出
            content.endText();
            // 关闭文档
            document.close();
            // 关闭书写器
            writer.close();

            //将PDF => base64 => pdf
            String base64 = PDFUtils.getPDFBinary(pdfFile);
            pdfFile.delete();
            pdfFile.getParentFile().delete();
            return new Label(base64, trackNo);
        } catch (DocumentException e) {
            log.info("create pdf error: " + e);
            System.out.println("create pdf error: " + e);
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            log.info("create pdf file FileNotFoundException error: " + e);
            System.out.println("create pdf file FileNotFoundException error: " + e);
            e.printStackTrace();
        } catch (IOException e) {
            log.info("create pdf file IOExceptionerror: " + e);
            System.out.println("create pdf file IOExceptionerror: " + e);
            e.printStackTrace();
        }
        return null;
    }


    public static String getWrapperBillPDFBase64(String srcBase64, String remark, String no, String entry) {

        long current = System.currentTimeMillis();
        String pdfPath = "/tmp/hgups_label_" + current + ".pdf";
        String newPdfPath = "/tmp/hgups_label_new_" + current + ".pdf";
        PDFUtils.base64StringToPDF(srcBase64, pdfPath);
        String logoPath = "/Users/Bingo/Downloads/logo.png";
        String baoGuanLogoPath = "/Users/Bingo/Downloads/baoguanlogo.jpg";
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(newPdfPath)));
            setWaterPrint(bos, pdfPath, remark, no, entry, logoPath, baoGuanLogoPath);

            File newPDF = new File(newPdfPath);
            String newBase64 = PDFUtils.getPDFBinary(newPDF);
            newPDF.delete();
            return newBase64;
        } catch (FileNotFoundException e) {
            System.out.println(" getWrapperBillPDFBase64 file not found error: " + String.valueOf(e));
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(" getWrapperBillPDFBase64 error: " + String.valueOf(e));
            e.printStackTrace();
        }
        return "";
    }

    private static void setWaterPrint(BufferedOutputStream bos, String inputPDFFilePath, String remark, String no, String entry,
                                      String logoPath, String baoGuanLogoPath) throws Exception {
        PdfReader reader = new PdfReader(inputPDFFilePath);
        PdfStamper stamper = new PdfStamper(reader, bos);
        int total = reader.getNumberOfPages() + 1;
        PdfContentByte content;
        BaseFont base = BaseFont.createFont();
        for (int i = 1; i < total; i++) {
            content = stamper.getOverContent(i);// 在内容上方加水印
            content.beginText();

            //字体大小
//            content.setFontAndSize(base, 9f);
//            content.showTextAligned(Element.ALIGN_LEFT | Element.ALIGN_BOTTOM, "SHIP:", 13, 225, 0);
//            content.showTextAligned(Element.ALIGN_LEFT | Element.ALIGN_BOTTOM, "TO:", 13, 213, 0);
            //内容居中，横纵坐标，偏移量
            content.setFontAndSize(base, 6.4f);
            content.showTextAligned(Element.ALIGN_LEFT | Element.ALIGN_BOTTOM, remark, 7, 26, 0);
            content.showTextAligned(Element.ALIGN_LEFT | Element.ALIGN_BOTTOM, no, 7, 17, 0);
            content.showTextAligned(Element.ALIGN_LEFT | Element.ALIGN_BOTTOM, entry, 7, 8, 0);


            //公司图标
            Image image = Image.getInstance(logoPath);
            //图片的位置（坐标）
            image.setAbsolutePosition(230, 14);
            // image of the absolute
            image.scaleToFit(20, 10);
            image.scalePercent(5);//依照比例缩放
            content.addImage(image);

            //报关公司图标
            Image image2 = Image.getInstance(baoGuanLogoPath);
            //图片的位置（坐标）
            image2.setAbsolutePosition(210, 5);
            // image of the absolute
            image2.scaleToFit(20, 10);
            image2.scalePercent(13);//依照比例缩放
            content.addImage(image2);


            content.endText();
        }
        stamper.close();
        //关闭打开的原来PDF文件，不执行reader.close()删除不了（必须先执行stamper.close()，否则会报错）
        reader.close();
        //删除原来的PDF文件
        File targetTemplePDF = new File(inputPDFFilePath);
        targetTemplePDF.delete();
    }

    private static String addBlankinMiddle(String str) {
        //字符串长度
        int strlenth = str.length();
        //需要加空格数量
        int blankcount = 0;
        //判断字符串长度
        if (strlenth <= 4) {
            blankcount = 0;
        } else {
            blankcount = strlenth % 4 > 0 ? strlenth / 4 : str.length() / 4 - 1; //需要加空格数量
        }
        //插入空格
        if (blankcount > 0) {
            for (int i = 0; i < blankcount; i++) {
                str = str.substring(0, (i + 1) * 4 + i) + " " + str.substring((i + 1) * 4 + i, strlenth + i);
            }
        } else {
            log.info("输入的字符串不多于4位，不需要添加空格");
        }
        return str;
    }


    public static final String DEF_TRACK_NO_PREFIX = "420";
    public static final String CHANNEL_APP_ID = "92";
    public static final String LABEL_SERVICE_TYPE_F = "001";
    public static final String LABEL_SERVICE_TYPE_P = "055";
    public static String crid = "36053262";
    public static String mid = "903051896";

    /**
     * @param serialNo 11位序列号
     * @param isF
     * @return
     */
    public static String newTrackNo(String serialNo, boolean isF) {
        log.info(" bingo mid: " + mid);
        String src = CHANNEL_APP_ID + (isF ? LABEL_SERVICE_TYPE_F : LABEL_SERVICE_TYPE_P) + "903051896" + serialNo;
        return newNumber(src);
    }

    public static String newNumber(String src) {
        int eventSum = 0;
        int oddSum = 0;

        for (int i = 0; i < src.length(); i++) {
            int digit = Integer.parseInt(src.charAt(i) + "");
            if (i % 2 == 0) {
                eventSum += digit;
            } else {
                oddSum += digit;
            }
        }

        int checkDigit = 0;
        int sum = eventSum * 3 + oddSum;
        int reset = sum % 10;
        if (reset != 0) {
            checkDigit = 10 - reset;
        }
        String number = src + checkDigit;
        log.info(" eventSum: " + eventSum + ", oddSum: " + oddSum + ", sum: " + sum + ", checkDigit: " + checkDigit + ", number: " + number);
        return number;
    }


    public static void initTrackNo() {
        String currNo = FileUtil.readString(trackNoFilePath, "UTF-8");
        log.info(" initTrackNo currNo: " + currNo);
        noIndex = Long.parseLong(currNo.trim());
        log.info(" initTrackNo noIndex: " + noIndex);
    }

    public static void increaseTrackNo() {
        noIndex++;
        FileUtil.writeString(String.valueOf(noIndex), trackNoFilePath, "UTF-8");
        log.info(" increaseTrackNo noIndex: " + noIndex);
    }

    public static Object sLock = new Object();


    //将图片base64转为图片文件
    public static boolean base64ToFile(String base64, String imgPath,String pdfPath) {
        byte[] buffer;
        try {
            buffer = new BASE64Decoder().decodeBuffer(base64);
            FileOutputStream out = new FileOutputStream(imgPath);
            out.write(buffer);
            out.close();

            imgToPdf(imgPath, pdfPath);


            return true;
        } catch (Exception e) {
            return false;
        }
    }



    //图片转pdf
    public static boolean imgToPdf(String imgFilePath, String pdfFilePath)throws IOException {
        File file=new File(imgFilePath);
        if(file.exists()){
            Document document = document = new Document(new Rectangle(348,532));
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(pdfFilePath);
                PdfWriter.getInstance(document, fos);

// 添加PDF文档的某些信息，比如作者，主题等等
                document.addAuthor("test");
                document.addSubject("test pdf.");
// 设置文档的大小
// 打开文档


                document.open();
// 写入一段文字
//document.add(new Paragraph("JUST TEST ..."));
// 读取一个图片
                Image image = Image.getInstance(imgFilePath);
                float imageHeight=image.getScaledHeight();
                float imageWidth=image.getScaledWidth();
                int i=0;
                while(imageHeight>500||imageWidth>500){
                    image.scalePercent(100-i);
                    i++;
                    imageHeight=image.getScaledHeight();
                    imageWidth=image.getScaledWidth();
                    System.out.println("imageHeight->"+imageHeight);
                    System.out.println("imageWidth->"+imageWidth);
                }

                image.setAlignment(Image.ALIGN_CENTER);
//      //设置图片的绝对位置
  image.setAbsolutePosition(0, 0);
  image.scaleAbsolute(348, 532);
// 插入一个图片
                document.add(image);
            } catch (DocumentException de) {
                System.out.println(de.getMessage());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            document.close();
            fos.flush();
            fos.close();
            return true;
        }else{
            return false;
        }
    }

}
