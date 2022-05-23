package com.hgups.express.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: LZJ
 * @Date: 2021/3/3 13:52
 */
@Data
public class ResultPageVo<T> {
    private List<T> records;
    private long total;
    private int current;
    private int size;
    private long pages;
}
