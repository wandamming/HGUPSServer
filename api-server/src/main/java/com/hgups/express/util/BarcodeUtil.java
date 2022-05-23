package com.hgups.express.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanAccessLanguageException;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.EAN128AI;
import org.krysalis.barcode4j.impl.code128.EAN128Bean;
import org.krysalis.barcode4j.impl.code128.EAN128LogicImpl;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 条形码工具类
 *
 * @author tangzz
 * @createDate 2015年9月17日
 */
@Slf4j
public class BarcodeUtil {

    /**
     * 生成文件
     *
     * @param msg
     * @param path
     * @return
     */
    public static File generateFile(String msg, String path) {
        File file = new File(path);
        try {
            generate(msg, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    /**
     * 生成字节
     *
     * @param msg
     * @return
     */
    public static byte[] generate(String msg) {
        ByteArrayOutputStream ous = new ByteArrayOutputStream();
        generate(msg, ous);
        return ous.toByteArray();
    }

    /**
     * 生成到流
     *
     * @param msg
     * @param ous
     */
    public static void generate(String msg, OutputStream ous) {
        if (StringUtils.isEmpty(msg) || ous == null) {
            return;
        }

        EAN128Bean bean = new EAN128Bean();
        // 精细度
        final int dpi = 130;
        // module宽度
        final double moduleWidth = UnitConv.in2mm(1.0f / dpi);

        // 配置对象
        bean.setModuleWidth(moduleWidth);
        bean.setBarHeight(12);
        bean.doQuietZone(false);
        bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        bean.setTemplate("(420)n5+(92)n" + (msg.length() - 10));

        String format = "image/png";
        try {

            // 输出到流
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(ous, format, dpi,
                    BufferedImage.TYPE_BYTE_BINARY, true, 0);

            // 生成条形码
            bean.generateBarcode(canvas, msg);

            // 结束绘制
            canvas.finish();
        } catch (IOException e) {
            log.info(" generate GS1-128 error: " + String.valueOf(e));
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String path1 = "/Users/Bingo/Downloads/barcode1.png";
        BarcodeUtil.generateFile("13133232323232323", path1);
        char str = '\u001d';
        System.out.println(" c=" + str);
    }
}