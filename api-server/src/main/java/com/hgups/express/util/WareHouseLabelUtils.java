package com.hgups.express.util;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hgups.express.domain.*;
import com.hgups.express.service.warehousemgi.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/*
 *           入库面单
 *
 *
 * */
@Component
public class WareHouseLabelUtils {

    @Resource
    private InventoryProductService inventoryProductService;
    private static InventoryProductService inventoryProductServiceStatic;
    @Resource
    private ProductInfoService productInfoService;
    private static ProductInfoService productInfoServiceStatic;
    @Resource
    private InventoryService inventoryService;
    private static InventoryService inventoryServiceStatic;
    @Resource
    private OutboundService outboundService;
    private static OutboundService outboundServiceStatic;
    @Resource
    private OutboundProductService outboundProductService;
    private static OutboundProductService outboundProductServiceStatic;
    @PostConstruct
    public void init(){
        inventoryProductServiceStatic=inventoryProductService;
        productInfoServiceStatic=productInfoService;
        inventoryServiceStatic=inventoryService;
        outboundServiceStatic=outboundService;
        outboundProductServiceStatic=outboundProductService;
    }

    public static void test() {
        Inventory inventory = inventoryServiceStatic.selectById(11);
        Outbound outbound = outboundServiceStatic.selectById(4);
        //createInventoryPDF(inventory);
        createOutboundPDF(outbound);
    }

    /*
    *
    *   入库单面单
    *
    *
    * */
    public static String createInventoryPDF(Inventory inventory) {
        long current = System.currentTimeMillis();
        try {
            if (null==inventory){
                return "";
            }
            Long inventoryId = inventory.getId();//入库单ID
            EntityWrapper<InventoryProduct> wrapper = new EntityWrapper<>();
            wrapper.eq("inventory_id",inventoryId);
            List<InventoryProduct> inventoryProducts = inventoryProductServiceStatic.selectList(wrapper);

            String receiptOrder = inventory.getReceiptOrder();//入库单号
            Integer receiptNumber = inventory.getReceiptNumber();//当前库单产品总数
            Integer skuNumber = inventory.getSkuNumber();//当前库单产品种类总数
            Integer arrive = inventory.getArrive();//已到数量
            Integer noArrive = inventory.getNoArrive();//未到数量
            Integer qualified = inventory.getQualified();//合格数量
            Integer noQualified = inventory.getNoQualified();//不合格数量
            String senderName = inventory.getSenderName();//收件人姓名
            String senderAddress = inventory.getSenderAddress();//收件人地址
            String receiveName = inventory.getReceiveName();//发件人姓名
            String receiveAddress = inventory.getReceiveAddress();//发件人地址
            String inventoryAddress = inventory.getInventoryAddress();//发件人地址
            if (org.apache.commons.lang3.StringUtils.isEmpty(inventoryAddress)){
                inventoryAddress = "Not to Warehouse";
            }

            //pdf路径
            //String pdfPath = "D:/Download/userSacks.pdf";
            String pdfPath = PathUtils.resDir + "warehouse/inventory/" + current + inventory.hashCode() + ".pdf";
            File pdfFile = new File(pdfPath);
            if(!pdfFile.exists()) {
                pdfFile.getParentFile().mkdirs();
                pdfFile.createNewFile();
            }
            // 创建文件
            int pdfWidth = 595, pdfHeight = 842;
            Document document = new Document(new Rectangle(pdfWidth, pdfHeight));
            // 建立一个书写器
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));

            //打开文件
            document.open();

            //创建内容文本器
            PdfContentByte content = writer.getDirectContent();

            //下面添加内容

            //设置起、始点
            int startX = 4, starY = 5, endX = 591, endY = 838;
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


            //恢复默认的线条高度
            content.setLineWidth(4f);
            content.stroke();


            float ta = 1f, tb = 0f, tc = 0f, td = 1f, tx = 0f, ty = 0f;
            ta = ta + 0.15f;
            td = td + 0.05f;
            ty = ty - 0.15f;

            BaseFont baseF = BaseFont.createFont();
            /*Font font = null;
            baseF = BaseFont.createFont( "STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);//创建字体
            font = new Font(baseF,12);//使用字体*/
            BaseFont baseFBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            content.beginText();
            content.setFontAndSize(baseFBold, 24);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Inventory List", startX + 200, fakeStartY - 30, 0);
            content.setFontAndSize(baseF, 11);
            //收、发件人信息
            /*content.showTextAligned(PdfContentByte.ALIGN_LEFT, "ReceiveName: xxx xxx", startX + 20, fakeStartY - 70, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "ReceiveAddress: XXXXXXXXXXXXXXXXXXXXXXXX", startX + 190, fakeStartY - 70, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "SendName: XXX XXX", startX + 20, fakeStartY - 85, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "SendAddress: XXXXXXXXXXXXXXXXXXXXXXXX", startX + 190, fakeStartY - 85, 0);*/
            //数量相关信息
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "SKU Total: "+(skuNumber==null?0:skuNumber), startX + 20, fakeStartY - 70, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Receipt Total: "+(receiptNumber==null?0:receiptNumber), startX + 190, fakeStartY - 70, 0);

            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Warehouse Address: "+inventoryAddress, startX + 360, fakeStartY - 85, 0);
            int i = 110;
            int arrived = 0;
            int noArrived = 0;
            int quality = 0;
            int noQuality = 0;

            for (InventoryProduct inventoryProduct : inventoryProducts) {

                arrived += inventoryProduct.getArrive();
                noArrived += inventoryProduct.getNoArrive();
                quality += inventoryProduct.getQualified();
                noQuality += inventoryProduct.getNoQualified();

                i = i+15;
                //入库单中的产品信息
                ProductInfo productInfo = productInfoServiceStatic.selectById(inventoryProduct.getProductId());
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "SKU Code: "+productInfo.getSkuCode(), startX + 20, fakeStartY - i, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Chinese Name: "+productInfo.getCName(), startX + 170, fakeStartY - i, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "English Name: "+productInfo.getEName(), startX + 320, fakeStartY - i, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Total: "+(inventoryProduct.getProductNumber()==null?0:inventoryProduct.getProductNumber()), startX + 480, fakeStartY - i, 0);
            }

            if(WareHouseState.ALREADY_INVENTORY.equals(inventory.getState())) {
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Reach: "+ arrived, startX + 20, fakeStartY - 85, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "No Reach: "+ noArrived, startX + 190, fakeStartY - 85, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Qualified: "+ quality, startX + 20, fakeStartY - 100, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "No Qualified: "+ noQuality, startX + 190, fakeStartY - 100, 0);
            }

            //条形码区域
            //String trackAreaTitle = "SelectFirstClass";
            //content.setFontAndSize(baseF, 11);
            //content.showTextAligned(PdfContentByte.ALIGN_CENTER, trackAreaTitle, startX + 140, fakeStartY - 310, 0);
            content.setFontAndSize(baseFBold, 18);
            content.showTextAligned(PdfContentByte.ALIGN_CENTER, addblankinmiddle(receiptOrder), startX + 283, fakeStartY - 800, 0);

            Image trackNoImg = Image.getInstance(BarcodeUtil.generate(receiptOrder));
            //图片的位置（坐标）
            trackNoImg.setAbsolutePosition(135, 60);
            // image of the absolute
            trackNoImg.scaleToFit(300, 200);
//            trackNoImg.scalePercent(12);//依照比例缩放
            content.addImage(trackNoImg);

            //公司图标
            String logoPath = PathUtils.resDir + "hgups_logo.png";
            Image image = Image.getInstance(logoPath);
            //图片的位置（坐标）
            image.setAbsolutePosition(470, 800);
            // image of the absolute
            image.scaleToFit(100, 50);
            content.addImage(image);


            //结束内容的输出
            content.endText();
            // 关闭文档
            document.close();
            // 关闭书写器
            writer.close();
            String base64 = PDFUtils.getPDFBinary(new File(pdfPath));
            /*File targetTemplePDF = new File(pdfPath);
            targetTemplePDF.delete();*/
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
     *   出库单面单
     *
     *
     * */
    public static String createOutboundPDF(Outbound outbound) {
        long current = System.currentTimeMillis();
        try {
            if (null==outbound){
                return "";
            }
            Long outboundId = outbound.getId();//出库单ID
            EntityWrapper<OutboundProduct> wrapper = new EntityWrapper<>();
            wrapper.eq("outbound_id",outboundId);
            List<OutboundProduct> outboundProducts = outboundProductServiceStatic.selectList(wrapper);

            String outboundOrder = outbound.getOutboundOrder();//出库单号
            Integer outboundNumber = outbound.getOutboundNumber();//当前出库单产品总数
            Integer skuNumber = outbound.getSkuOutboundNumber();//当前出库单产品种类总数

            String sendName = outbound.getSendName();//发件人姓名
            String sendAddress = outbound.getSendAddress();//发件人地址
            String receiveName = outbound.getReceiveName();//收件人姓名
            String receiveAddress = outbound.getReceiveAddress();//收件人地址
            Integer logisticsMode = outbound.getLogisticsMode();//物流方式


            //pdf路径
            //String pdfPath = "D:/Download/userSacks.pdf";
            String pdfPath = PathUtils.resDir + "warehouse/outbound/" + current + outbound.hashCode() + ".pdf";
            File pdfFile = new File(pdfPath);
            if(!pdfFile.exists()) {
                pdfFile.getParentFile().mkdirs();
                pdfFile.createNewFile();
            }
            // 创建文件
            int pdfWidth = 595, pdfHeight = 842;
            Document document = new Document(new Rectangle(pdfWidth, pdfHeight));
            // 建立一个书写器
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));

            //打开文件
            document.open();

            //创建内容文本器
            PdfContentByte content = writer.getDirectContent();

            //下面添加内容

            //设置起、始点
            int startX = 4, starY = 5, endX = 591, endY = 838;
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


            //恢复默认的线条高度
            content.setLineWidth(4f);
            content.stroke();


            float ta = 1f, tb = 0f, tc = 0f, td = 1f, tx = 0f, ty = 0f;
            ta = ta + 0.15f;
            td = td + 0.05f;
            ty = ty - 0.15f;

            BaseFont baseF = BaseFont.createFont();
            /*Font font = null;
            baseF = BaseFont.createFont( "STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);//创建字体
            font = new Font(baseF,12);//使用字体*/
            BaseFont baseFBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            content.beginText();
            content.setFontAndSize(baseFBold, 24);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Outbound List", startX + 200, fakeStartY - 30, 0);
            content.setFontAndSize(baseF, 11);
            //收、发件人信息
            /*content.showTextAligned(PdfContentByte.ALIGN_LEFT, "ReceiveName: xxx xxx", startX + 20, fakeStartY - 70, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "ReceiveAddress: XXXXXXXXXXXXXXXXXXXXXXXX", startX + 190, fakeStartY - 70, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "SendName: XXX XXX", startX + 20, fakeStartY - 85, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "SendAddress: XXXXXXXXXXXXXXXXXXXXXXXX", startX + 190, fakeStartY - 85, 0);*/
            //数量相关信息
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "SKU Total: "+(skuNumber==null?0:skuNumber), startX + 20, fakeStartY - 70, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Outbound Total: "+(outboundNumber==null?0:outboundNumber), startX + 220, fakeStartY - 70, 0);
            //暂时先隐藏 物流方式的显示
//            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Logistics Mode: "+(logisticsMode==1?"国内":"国外"), startX + 400, fakeStartY - 70, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "ReceiveName: "+receiveName, startX + 20, fakeStartY - 85, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "ReceiveAddress: "+receiveAddress, startX + 220, fakeStartY - 85, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "SendName: "+sendName, startX + 20, fakeStartY - 100, 0);
            content.showTextAligned(PdfContentByte.ALIGN_LEFT, "SendAddress: "+sendAddress, startX + 220, fakeStartY - 100, 0);
            int i = 110;
            for (OutboundProduct outboundProduct : outboundProducts) {
                i = i+15;
                //入库单中的产品信息
                ProductInfo productInfo = productInfoServiceStatic.selectById(outboundProduct.getProductId());
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "SKU Code: "+productInfo.getSkuCode(), startX + 20, fakeStartY - i, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Chinese Name: "+productInfo.getCName(), startX + 170, fakeStartY - i, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "English Name: "+productInfo.getEName(), startX + 320, fakeStartY - i, 0);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, "Total: "+(outboundProduct.getProductNumber()==null?0:outboundProduct.getProductNumber()), startX + 480, fakeStartY - i, 0);
            }

            //条形码区域
            //String trackAreaTitle = "SelectFirstClass";
            //content.setFontAndSize(baseF, 11);
            //content.showTextAligned(PdfContentByte.ALIGN_CENTER, trackAreaTitle, startX + 140, fakeStartY - 310, 0);
            content.setFontAndSize(baseFBold, 18);
            content.showTextAligned(PdfContentByte.ALIGN_CENTER, addblankinmiddle(outboundOrder), startX + 283, fakeStartY - 800, 0);

            Image trackNoImg = Image.getInstance(BarcodeUtil.generate(outboundOrder));
            //图片的位置（坐标）
            trackNoImg.setAbsolutePosition(135, 60);
            // image of the absolute
            trackNoImg.scaleToFit(300, 200);
//            trackNoImg.scalePercent(12);//依照比例缩放
            content.addImage(trackNoImg);

            //公司图标
            String logoPath = PathUtils.resDir + "hgups_logo.png";
            Image image = Image.getInstance(logoPath);
            //图片的位置（坐标）
            image.setAbsolutePosition(470, 800);
            // image of the absolute
            image.scaleToFit(100, 50);
            content.addImage(image);


            //结束内容的输出
            content.endText();
            // 关闭文档
            document.close();
            // 关闭书写器
            writer.close();
            String base64 = PDFUtils.getPDFBinary(new File(pdfPath));
            /*File targetTemplePDF = new File(pdfPath);
            targetTemplePDF.delete();*/
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