package com.hgups.express.vo;

import com.hgups.express.domain.PointScanRecord;
import com.hgups.express.domain.enmus.PointScanType;
import lombok.Data;

import java.util.List;

/**
 * @Author: LZJ
 * @Date: 2021/3/4 17:28
 */
@Data
public class PointScanRecordVo extends PointScanRecord {
    // 子状态
    private List<PointScanRecord> children;
    // UI-状态 已完成、未完成、未处理
    private String status;
    // 是否是当前阶段
    private boolean currentStage;
    // 提示语:您当前所负责的阶段
    private String comment;
    // 状态
    // 1 已完成、
    // 2 未完成、
    // 3 未处理、
    // 4 运单当前阶段、
    // 5 扫码员本次扫码负责阶段、
    // 6 本次补扫阶段
    private Integer state;

    public void setStateAndStatus(PointScanType code){
        this.state = code.getStatus();
        this.status = code.getMsg();
    }
}
