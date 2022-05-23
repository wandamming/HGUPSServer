package com.hgups.express.util;

import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * @author fanc
 * 2020/7/18 0018-18:18
 *
 *      PDF 转 Base64编码
 *
 *
 */
public class PDFToBase64Util {

    public static String PDFToBase64(String number) {
        String pdfPath = "D:/Download/"+number+".pdf";
        File file = new File(pdfPath);
        BASE64Encoder encoder = new BASE64Encoder();
        FileInputStream fin =null;
        BufferedInputStream bin =null;
        ByteArrayOutputStream baos = null;
        BufferedOutputStream bout =null;
        try {
            fin = new FileInputStream(file);
            bin = new BufferedInputStream(fin);
            baos = new ByteArrayOutputStream();
            bout = new BufferedOutputStream(baos);
            byte[] buffer = new byte[1024];
            int len = bin.read(buffer);
            while(len != -1){
                bout.write(buffer, 0, len);
                len = bin.read(buffer);
            }
            //刷新此输出流并强制写出所有缓冲的输出字节
            bout.flush();
            byte[] bytes = baos.toByteArray();
            return encoder.encodeBuffer(bytes).trim();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fin.close();
                bin.close();
                bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String ff = "2020071815304100001";
        PDFToBase64Util tt = new PDFToBase64Util();

        System.out.println(tt.PDFToBase64(ff));

    }
}
