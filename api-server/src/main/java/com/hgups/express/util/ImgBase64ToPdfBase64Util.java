/*
package com.hgups.express.util;

import java.io.File;

*/
/**
 * @author fanc
 * 2020/11/11-21:53
 *//*

public class ImgBase64ToPdfBase64Util {


    public String imageBase64ToPdfBase64(String bas64ImageStr,String custOrderNbr){
        String pdfBase64 = "";//PDF的base64编码

        try{
            //对路劲进行拼接添加
            String serviceImgPath = PathUtils.resDir + "DHLImg/";//服务器上存放二维码图片的路径
            String servicePdfPath = PathUtils.resDir + "DHLPdf/";//服务器上存放二维码PDF的路径

            //3.判断服务器是否存在此文件夹,不存在则新建
            File file1 =new File(serviceImgPath);
            File file2 =new File(servicePdfPath);
            if(!file1.exists()  && !file1.isDirectory()) {
                file1.mkdir();
            }
            if(!file2.exists()  && !file2.isDirectory()) {
                file2.mkdir();
            }
            //4.二维码图片的文件名字和最终保存的二维码文件路径
            String fileImageName = custOrderNbr+"_DHL.png";//二维码图片路径名字
            String filePdfName = custOrderNbr+"_DHL.pdf";//PDF图片路径名字
            String lastImagePath = serviceImgPath+fileImageName;//最终二维码图片存放的路径
            String lastPdfPath = servicePdfPath+filePdfName;//最终二维码PDF存放的路径

            //5.首先保存二维码图片
            Base64ToImage(bas64ImageStr,lastImagePath);
            //6.然后把二维码图片转成PDF二维码文件进行储存
            ImagePdf.image2pdf(lastImagePath,lastPdfPath);

            //7.最后将PDF转成base64,PDF的base64才是最终能推送到签字版的
            File file3 =new File(lastPdfPath);
            pdfBase64 = PDFToBase64(file3);
            pdfBase64 = pdfBase64.replaceAll("\r|\n", "");

            //8.需要删除创建的临时文件
            File imagefile = new File(lastImagePath);
            if(imagefile.exists()){
                imagefile.delete();
            }
            File pdffile = new File(lastPdfPath);
            if(pdffile.exists()){
                pdffile.delete();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return pdfBase64;
    }




}
*/
