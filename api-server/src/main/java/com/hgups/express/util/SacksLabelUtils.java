package com.hgups.express.util;

import com.hgups.express.domain.ShippingSacks;
import com.hgups.express.domain.UserSacks;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/*
 *           麻袋面单
 *
 *
 * */
public class SacksLabelUtils{

    public static String createUserSacksPDF(UserSacks sacks, String userName, String userCompany) {
        long current = System.currentTimeMillis();
        try {
            String sacksNumber = sacks.getSacksNumber();
            String totalWeight = String.format("%.4f",sacks.getWareWeight());

            int totalParcel = sacks.getParcelNumber();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(sacks.getCreateTime());

            //pdf路径
            //String pdfPath = "D:/Download/userSacks.pdf";
            String pdfPath = PathUtils.resDir + "sacks/" + current + sacks.hashCode() + ".pdf";
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
            String service = sacks.getService();
            String instructions = "Customer Sacks";
            String sacksWeight = "TotalWeight: "+totalWeight+"LB";
            String createTime = "CreateTime: "+time;
            String number = "TotalPackage: "+totalParcel;
            String name = "User: "+userName;
            String company = "Company: "+userCompany;


            float ta = 1f, tb = 0f, tc = 0f, td = 1f, tx = 0f, ty = 0f;
            ta = ta + 0.15f;
            td = td + 0.05f;
            ty = ty - 0.15f;

            BaseFont baseF = BaseFont.createFont();
            BaseFont baseFBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            content.beginText();
            content.setFontAndSize(baseFBold, 90);
            content.setTextMatrix(ta, tb, tc, td, tx, ty);//这里对文字进行变形以达到想要的效果
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, service, startX + 8, fakeStartY - 71, 0);
            content.setFontAndSize(baseFBold, 18);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, instructions, startX + 68, fakeStartY - 97, 0);
            content.setFontAndSize(baseF, 11);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, name, startX + 58, fakeStartY - 130, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, company, startX + 58, fakeStartY - 155, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, sacksWeight, startX + 58, fakeStartY - 180, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, number, startX + 58, fakeStartY - 205, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, createTime, startX + 58, fakeStartY - 230, 0);

            String type7 = "INTERNAL USE ONLY";
            content.setFontAndSize(baseF, 16);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, type7, startX + 60, fakeStartY - 277, 0);

            //条形码区域
            //String trackAreaTitle = "SelectFirstClass";
            //content.setFontAndSize(baseF, 11);
            //content.showTextAligned(PdfContentByte.ALIGN_CENTER, trackAreaTitle, startX + 140, fakeStartY - 310, 0);
            content.setFontAndSize(baseFBold, 12);
            content.showTextAligned(PdfContentByte.ALIGN_CENTER, addblankinmiddle(sacksNumber), startX + 143, fakeStartY - 399, 0);

            Image trackNoImg = Image.getInstance(BarcodeUtil.generate(sacksNumber));
            //图片的位置（坐标）
            trackNoImg.setAbsolutePosition(25, 50);
            // image of the absolute
            trackNoImg.scaleToFit(240, 160);
//            trackNoImg.scalePercent(12);//依照比例缩放
            content.addImage(trackNoImg);

            //公司图标
            String logoPath = PathUtils.resDir + "hgups_logo.png";
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
*       航运麻袋面单
*
*
* */
    public static String createShippingSacksPDF(ShippingSacks sacks, String userName, String userCompany) {
        long current = System.currentTimeMillis();
        try {

            String sacksNumber = sacks.getSacksNumber();
            String portEntry = sacks.getEntrySite();
            String totalWeight = String.format("%.4f",sacks.getWareWeight());


            int totalParcel = sacks.getParcelNumber();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(sacks.getCreateTime());
            String comment = sacks.getComment();

            //pdf路径
            //String pdfPath = "D:/Download/shippingSacks.pdf";
            String pdfPath = PathUtils.resDir + "sacks/" + current + sacks.hashCode() + ".pdf";
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
            String service = sacks.getService();
            String instructions = "Shipping Sacks";
            String sacksWeight = "TotalWeight: "+totalWeight+"LB";
            String createTime = "CreateTime: "+time;
            String number = "TotalPackage: "+totalParcel;
            String name = "User: "+userName;
            String company = "Company: "+userCompany;
            String port = "PortEntry: "+ portEntry;


            float ta = 1f, tb = 0f, tc = 0f, td = 1f, tx = 0f, ty = 0f;
            ta = ta + 0.15f;
            td = td + 0.05f;
            ty = ty - 0.15f;

            BaseFont baseF = BaseFont.createFont();
            BaseFont baseFBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            content.beginText();
            content.setFontAndSize(baseFBold, 90);
            content.setTextMatrix(ta, tb, tc, td, tx, ty);//这里对文字进行变形以达到想要的效果
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, service, startX + 8, fakeStartY - 71, 0);
            content.setFontAndSize(baseFBold, 18);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, instructions, startX + 68, fakeStartY - 97, 0);
            content.setFontAndSize(baseF, 11);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, name, startX + 58, fakeStartY - 130, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, company, startX + 58, fakeStartY - 150, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, sacksWeight, startX + 58, fakeStartY - 170, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, number, startX + 58, fakeStartY - 190, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, createTime, startX + 58, fakeStartY - 210, 0);
            content.setFontAndSize(baseFBold, 11);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, port, startX + 58, fakeStartY - 230, 0);

            String type7 = "INTERNAL USE ONLY";
            content.setFontAndSize(baseF, 16);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, type7, startX + 60, fakeStartY - 277, 0);

            //条形码区域
            //String trackAreaTitle = "SelectFirstClass";
            //content.setFontAndSize(baseF, 11);
            //content.showTextAligned(PdfContentByte.ALIGN_CENTER, trackAreaTitle, startX + 140, fakeStartY - 310, 0);
            content.setFontAndSize(baseFBold, 12);
            content.showTextAligned(PdfContentByte.ALIGN_CENTER, addblankinmiddle(sacksNumber), startX + 143, fakeStartY - 399, 0);

            Image trackNoImg = Image.getInstance(BarcodeUtil.generate(sacksNumber));
            //图片的位置（坐标）
            trackNoImg.setAbsolutePosition(25, 50);
            // image of the absolute
            trackNoImg.scaleToFit(240, 160);
//            trackNoImg.scalePercent(12);//依照比例缩放
            content.addImage(trackNoImg);

           /* String logoDir = "D:/Download/";
            String logoPath = logoDir + "logo.png";*/

            //公司图标
            String logoPath = PathUtils.resDir + "hgups_logo.png";
            Image image = Image.getInstance(logoPath);
            //图片的位置（坐标）
            image.setAbsolutePosition(192, 394);
            // image of the absolute
            image.scaleToFit(70, 35);
            content.addImage(image);


            content.setFontAndSize(baseF, 6);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, comment==null?"":comment, startX + 20, fakeStartY - 15, 0);

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