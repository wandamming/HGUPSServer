package com.hgups.express.util;

import com.hgups.express.domain.vo.ResultPageVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: LZJ
 * @Date: 2021/3/3 11:55
 */
public class ResultParamUtil {
    public static <T> Map<String, Object> result(List<T> list, long total, int current, int size) {
        Map<String, Object> result = new HashMap<>();
        result.put("records", list);
        result.put("total", total);
        result.put("current", current);
        result.put("size", size);
        if (size == 0) {
            result.put("pages", 0);
        } else {
            result.put("pages", (total % size == 0 ? total / size : total / size + 1));
        }
        return result;
    }

    public static <T> ResultPageVo<T> resultResultPage(List<T> list, long total, int current, int size) {
        ResultPageVo<T> result = new ResultPageVo<>();
        result.setRecords(list);
        result.setTotal(total);
        result.setCurrent(current);
        result.setSize(size);
        result.setPages(total % size == 0 ? total / size : total / size + 1);
        return result;
    }
}
