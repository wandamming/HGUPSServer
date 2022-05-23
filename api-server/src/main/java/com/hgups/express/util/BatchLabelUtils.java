package com.hgups.express.util;

import com.hgups.express.domain.ShippingBatch;
import com.hgups.express.domain.UserBatch;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.util.utiltest.PDFUtilsTest;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;

/*
 *           批次面单
 *
 *
 * */
@Component
@Slf4j
public class BatchLabelUtils {
    @Resource
    private WayBillService wayBillService;
    private static WayBillService staticWayBillService;

    @PostConstruct
    public void init() {
        staticWayBillService = wayBillService;
    }

    public static String createUserBatchPDF(UserBatch batch, String userName, String userCompany) {
        long current = System.currentTimeMillis();
        try {
            String batchNumber = batch.getTrackingNumber();//批次单号
            int totalSacks = batch.getSacksNumber();//批次麻袋数量
            int totalParcel = batch.getWaybillNumber();//批次运单数量
            String totalAmount = String.format("%.2f",batch.getTotalAmount());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(batch.getCreateTime());

            //pdf路径
           // String pdfPath = "D:/Download/userBatch.pdf";
            String pdfPath = PathUtils.resDir + "batch/" + current + batch.hashCode() + ".pdf";
            File pdfFile = new File(pdfPath);
            if(!pdfFile.exists()) {
                pdfFile.getParentFile().mkdirs();
                pdfFile.createNewFile();
            }
            // 创建文件
            int pdfWidth = 288, pdfHeight = 432;
            Document document = new Document(new Rectangle(pdfWidth, pdfHeight));
            // 建立一个书写器
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));

            //打开文件
            document.open();

            //创建内容文本器
            PdfContentByte content = writer.getDirectContent();

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

            //路由通道的框
            Rectangle rect2 = new Rectangle(startX + 230, fakeStartY - 120, startX + 266, fakeStartY - 133);//文本框位置
            rect2.setBorder(Rectangle.BOX);//显示边框，默认不显示，常量值：LEFT, RIGHT, TOP, BOTTOM，BOX,
            rect2.setBorderWidth(defLineW);//边框线条粗细
            rect2.setBorderColor(BaseColor.BLACK);//边框颜色


            //上
            content.setLineWidth(4f);
            content.moveTo(startX+38, fakeStartY - 286);
            content.lineTo(endX-38, fakeStartY - 286);

            //下
            content.moveTo(startX+38, fakeStartY - 255);
            content.lineTo(endX-38, fakeStartY - 255);

            //左
            content.moveTo(startX+40, fakeStartY - 286);
            content.lineTo(startX+40, fakeStartY - 255);

            //右
            content.moveTo(startX+240, fakeStartY - 286);
            content.lineTo(startX+240, fakeStartY - 255);


            //恢复默认的线条高度
            content.setLineWidth(4f);
            content.stroke();

            //顶部面单类型区域
            /*String type = "P";
            String typeDesc = "F".equals(type) ? "USPS FIRST-CLASS PKG" : "USPS PRIORITY MAIL";
            String typeTitleDesc1 = "F".equals(type) ? "FIRST-CLASS PKG" : "PRIORITY MAIL";
            float typeTitleDesc1Offset = "F".equals(type) ? 0 : 5;
            String typeTitleDesc2 = "U.S. POSTAGE PAID";
            String typeTitleDesc3 = "HGUPS";
            String typeTitleDesc4 = "eVS";
            float offsetTypeDesc = "F".equals(type) ? 12 : 37;*/

            //String type3 = "JFK Entry 1 2030";
            //String type4 = "AW: 27.234Kg";
            //String type6 = "Qty: 326";

            String sacks = "TotalSacks: "+totalSacks;
            String createTime = "CreateTime: "+time;
            String number = "TotalPackage: "+totalParcel;
            String name = "User: "+userName;
            String company = "Company: "+userCompany;

            String amount = "TotalPrice: "+totalAmount;
            String instructions = "Customer Batch";



            BaseFont baseF = BaseFont.createFont();
            BaseFont baseFBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            content.beginText();
            content.setFontAndSize(baseFBold, 18);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, instructions, startX + 68, fakeStartY - 73, 0);
            content.setFontAndSize(baseF, 11);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, name, startX + 58, fakeStartY - 105, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, company, startX + 58, fakeStartY - 130, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, sacks, startX + 58, fakeStartY - 155, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, number, startX + 58, fakeStartY - 180, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, amount, startX + 58, fakeStartY - 205, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, createTime, startX + 58, fakeStartY - 230, 0);

            String type7 = "INTERNAL USE ONLY";
            content.setFontAndSize(baseF, 16);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, type7, startX + 60, fakeStartY - 277, 0);

            //条形码区域
            //String trackAreaTitle = "SelectFirstClass";
            //content.setFontAndSize(baseF, 11);
            //content.showTextAligned(PdfContentByte.ALIGN_CENTER, trackAreaTitle, startX + 140, fakeStartY - 310, 0);
            content.setFontAndSize(baseFBold, 12);
            content.showTextAligned(PdfContentByte.ALIGN_CENTER, addblankinmiddle(batchNumber), startX + 143, fakeStartY - 399, 0);

            Image trackNoImg = Image.getInstance(BarcodeUtil.generate(batchNumber));
            //图片的位置（坐标）
            trackNoImg.setAbsolutePosition(25, 50);
            // image of the absolute
            trackNoImg.scaleToFit(240, 160);
//            trackNoImg.scalePercent(12);//依照比例缩放
            content.addImage(trackNoImg);

            String logoPath = PathUtils.resDir + "hgups_logo.png";
            //公司图标
            Image image = Image.getInstance(logoPath);
            //图片的位置（坐标）
            image.setAbsolutePosition(192, 394);
            // image of the absolute
            image.scaleToFit(70, 35);
            content.addImage(image);


            //结束内容的输出
            content.endText();
            // 关闭文档
            document.close();
            // 关闭书写器
            writer.close();

            String base64 = PDFUtils.getPDFBinary(new File(pdfPath));
            File targetTemplePDF = new File(pdfPath);
            targetTemplePDF.delete();
            return base64;
        } catch (DocumentException e) {
            System.out.println("create pdf error: " + e);
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("create pdf file not found error: " + e);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

/*
*
*       航运批次面单
*
*
* */
    public static String createShippingBatchPDF(ShippingBatch batch, String userName, String userCompany) {
        long current = System.currentTimeMillis();
        try {

            String batchNumber = batch.getTrackingNumber();//批次单号
            int totalSacks = batch.getSacksNumber();//批次麻袋数量
            int totalParcel = batch.getParcelNumber();//批次运单数量
            String totalAmount = String.format("%.2f",batch.getWarePrice());//批次运费总价
            System.out.println(staticWayBillService);
            Double totalWeight = staticWayBillService.getWareWeightByBatchId(batch.getTrackingNumber());
            String portEntry = batch.getEntrySite();//入境地点
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(batch.getCreateTime());

            //pdf路径
            //String pdfPath = "D:/Download/shippingBatch.pdf";
            String pdfPath = PathUtils.resDir + "batch/" + current + batch.hashCode() + ".pdf";
            File pdfFile = new File(pdfPath);
            if(!pdfFile.exists()) {
                pdfFile.getParentFile().mkdirs();
                pdfFile.createNewFile();
            }

            // 创建文件
            int pdfWidth = 288, pdfHeight = 432;
            Document document = new Document(new Rectangle(pdfWidth, pdfHeight));
            // 建立一个书写器
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));

            //打开文件
            document.open();

            //创建内容文本器
            PdfContentByte content = writer.getDirectContent();

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

            //路由通道的框
            Rectangle rect2 = new Rectangle(startX + 230, fakeStartY - 120, startX + 266, fakeStartY - 133);//文本框位置
            rect2.setBorder(Rectangle.BOX);//显示边框，默认不显示，常量值：LEFT, RIGHT, TOP, BOTTOM，BOX,
            rect2.setBorderWidth(defLineW);//边框线条粗细
            rect2.setBorderColor(BaseColor.BLACK);//边框颜色


            //上
            content.setLineWidth(4f);
            content.moveTo(startX+38, fakeStartY - 286);
            content.lineTo(endX-38, fakeStartY - 286);

            //下
            content.moveTo(startX+38, fakeStartY - 255);
            content.lineTo(endX-38, fakeStartY - 255);

            //左
            content.moveTo(startX+40, fakeStartY - 286);
            content.lineTo(startX+40, fakeStartY - 255);

            //右
            content.moveTo(startX+240, fakeStartY - 286);
            content.lineTo(startX+240, fakeStartY - 255);


            //恢复默认的线条高度
            content.setLineWidth(4f);
            content.stroke();

            //顶部面单类型区域
            /*String type = "P";
            String typeDesc = "F".equals(type) ? "USPS FIRST-CLASS PKG" : "USPS PRIORITY MAIL";
            String typeTitleDesc1 = "F".equals(type) ? "FIRST-CLASS PKG" : "PRIORITY MAIL";
            float typeTitleDesc1Offset = "F".equals(type) ? 0 : 5;
            String typeTitleDesc2 = "U.S. POSTAGE PAID";
            String typeTitleDesc3 = "HGUPS";
            String typeTitleDesc4 = "eVS";
            float offsetTypeDesc = "F".equals(type) ? 12 : 37;*/

            //String type3 = "JFK Entry 1 2030";
            //String type4 = "AW: 27.234Kg";
            //String type6 = "Qty: 326";

            String sacks = "TotalSacks: "+totalSacks;
            String createTime = "CreateTime: "+time;
            String number = "TotalPackage: "+totalParcel;
            String name = "User: "+userName;
            String company = "Company: "+userCompany;
            String portEntry1 = "PortEntry: "+portEntry;
            String amount = "TotalPrice: "+totalAmount;
            String instructions = "Shipping Batch";
            String weight = "TotalWeight: "+totalWeight;



            BaseFont baseF = BaseFont.createFont();
            BaseFont baseFBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            content.beginText();
            content.setFontAndSize(baseFBold, 18);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, instructions, startX + 68, fakeStartY - 70, 0);
            content.setFontAndSize(baseF, 11);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, name, startX + 58, fakeStartY - 100, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, company, startX + 58, fakeStartY - 120, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, sacks, startX + 58, fakeStartY - 140, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, number, startX + 58, fakeStartY - 160, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, amount, startX + 58, fakeStartY - 180, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, weight, startX + 58, fakeStartY - 200, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, createTime, startX + 58, fakeStartY - 220, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, portEntry1, startX + 58, fakeStartY - 240, 0);


            String type7 = "INTERNAL USE ONLY";
            content.setFontAndSize(baseF, 16);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, type7, startX + 60, fakeStartY - 277, 0);

            //条形码区域
            //String trackAreaTitle = "SelectFirstClass";
            //content.setFontAndSize(baseF, 11);
            //content.showTextAligned(PdfContentByte.ALIGN_CENTER, trackAreaTitle, startX + 140, fakeStartY - 310, 0);
            content.setFontAndSize(baseFBold, 12);
            content.showTextAligned(PdfContentByte.ALIGN_CENTER, addblankinmiddle(batchNumber), startX + 143, fakeStartY - 399, 0);

            Image trackNoImg = Image.getInstance(BarcodeUtil.generate(batchNumber));
            //图片的位置（坐标）
            trackNoImg.setAbsolutePosition(25, 50);
            // image of the absolute
            trackNoImg.scaleToFit(240, 160);
//            trackNoImg.scalePercent(12);//依照比例缩放
            content.addImage(trackNoImg);

            String logoPath = PathUtils.resDir + "hgups_logo.png";
            //公司图标
            Image image = Image.getInstance(logoPath);
            //图片的位置（坐标）
            image.setAbsolutePosition(192, 394);
            // image of the absolute
            image.scaleToFit(70, 35);
            content.addImage(image);


            //结束内容的输出
            content.endText();
            // 关闭文档
            document.close();
            // 关闭书写器
            writer.close();

            String base64 = PDFUtils.getPDFBinary(new File(pdfPath));
            File targetTemplePDF = new File(pdfPath);
            targetTemplePDF.delete();
            return base64;
        } catch (DocumentException e) {
            System.out.println("create pdf error: " + e);
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("create pdf file not found error: " + e);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public static String getWrapperBillPDFBase64(String srcBase64, String remark, String no, String entry) {

        long current = System.currentTimeMillis();
        String pdfPath = "/tmp/hgups_label_" + current + ".pdf";
        String newPdfPath = "/tmp/hgups_label_new_" + current + ".pdf";
        PDFUtilsTest.base64StringToPDF(srcBase64, pdfPath);
        String logoPath = "D:/Download/logo.png";
        String baoGuanLogoPath = "D:/Download/baoguanlogo.jpg";
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(newPdfPath)));
            setWaterPrint(bos, pdfPath, remark, no, entry, logoPath, baoGuanLogoPath);

            File newPDF = new File(newPdfPath);
            String newBase64 = PDFUtilsTest.getPDFBinary(newPDF);
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

    private static String addblankinmiddle(String str) {
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
            System.out.println("输入的字符串不多于4位，不需要添加空格");
        }
        return str;
    }

}