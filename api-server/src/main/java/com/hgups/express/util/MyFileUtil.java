package com.hgups.express.util;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Created by Allen on 2017/4/18.
 */
public class MyFileUtil {

    public static ResponseEntity downloadFile(String absolutePath, String fileName, HttpServletRequest request) throws IOException {
        File file = new File(absolutePath);
        if (!file.exists()) {
            return new ResponseEntity(null, HttpStatus.NO_CONTENT);
        }
        return downloadFile(file, fileName, request);
    }

    public static ResponseEntity downloadFile(File file, String fileName, HttpServletRequest request) throws IOException {
        byte[] fileBytes = FileUtils.readFileToByteArray(file);
        return downloadFile(fileBytes, fileName, request);
    }

    public static ResponseEntity downloadFile(byte[] fileBytes, String fileName, HttpServletRequest request) throws IOException {
        HttpHeaders headers = new HttpHeaders();
//        if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
//        } else {
//           fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");

//        }


        headers.setContentDispositionFormData("attachment",fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setPragma("public");
        headers.setCacheControl("max-age=0");
        headers.setAcceptCharset(Lists.newArrayList(new Charset[0]));
        return new ResponseEntity(fileBytes, headers, HttpStatus.OK);
    }

    public static String getFileSize(Long fileSize) {
        if (fileSize < 1024) {
            return BigDecimal.valueOf(fileSize).setScale(2, BigDecimal.ROUND_CEILING).toString() + "B";
        }

        double fileSizeByKb = fileSize * 1.0 / 1024;
        if (fileSizeByKb < 1024) {
            return BigDecimal.valueOf(fileSizeByKb).setScale(2, BigDecimal.ROUND_CEILING) + "KB";
        }

        double fileSizeByMb = fileSizeByKb / 1024;
        if (fileSizeByMb < 1024) {
            return BigDecimal.valueOf(fileSizeByMb).setScale(2, BigDecimal.ROUND_CEILING) + "MB";
        }

        double fileSizeByGb = fileSizeByKb / 1024;
        return BigDecimal.valueOf(fileSizeByGb).setScale(2, BigDecimal.ROUND_CEILING) + "GB";
    }
}
