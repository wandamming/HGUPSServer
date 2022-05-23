package com.hgups.express.domain.enmus;

/**
 * @Author: LZJ
 * @Date: 2021/3/11 15:32
 */
public enum PointScanType {
    FINISHED(1, "已完成"),
    UNFINISHED(2, "未完成"),
    UNTREATED(3, "未处理"),
    PRESENT(4, "运单当前阶段"),
    THISTIME(5, "本次扫码负责阶段"),
    THISTIME_REPLENISH(6, "本次扫码补扫阶段");

    PointScanType(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
    private int status;
    private String msg;

    public int getStatus() {
        return status;
    }
    public String getMsg() {
        return msg;
    }
}
