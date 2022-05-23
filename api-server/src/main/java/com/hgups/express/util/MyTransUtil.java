package com.hgups.express.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author fanc
 * 2020/10/22 0022-11:37
 */
public class MyTransUtil {


    /*首字母大写转换*/
    public static String FirstLetterCapital(String str){
        String newStr = "";
        String[] split = str.split("\\s+");
        for (int i = 0;i<split.length;i++) {
            String concat = split[i].substring(0, 1).toUpperCase().concat(split[i].substring(1).toLowerCase());
            //拼接字符串，并按空格分隔
            newStr = String.join(" ",newStr,concat);
        }
        return newStr;
    }
}