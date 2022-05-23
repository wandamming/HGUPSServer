package com.hgups.express.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PathUtils {
    public static String resDir;
    public static String uploadImg;

    @Value(value = "${res.dir}")
    public void setResDir(String dir) {
        resDir = dir;
    }
    @Value(value = "${uploadImg}")
    public void setUploadImg(String img) {
        uploadImg = img;
    }

}
