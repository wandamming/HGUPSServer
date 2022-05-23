package com.hgups.express.domain;
public enum ResponseMetaEnum {
    SUCCESS(0, "SUCCESS"),
    PAY_SUCCESS(1, "PAY_SUCCESS"),
    PARAM_ERROR(1000, "PARAM_ERROR"),
    ADMISSION_INFO_LOST(1001, "ADMISSION_INFO_LOST"),
    MSG_SEND_ERROR(1002, "MSG SEND ERROR"),
    CODE_ERROR(1003, "CODE_ERROR"),
    TELEPHONE_EXIST(1004, "TELEPHONE_EXIST"),
    PASSWORD_ERROR(1005, "PASSWORD_ERROR"),
    CULTURE_COUNT_EXHAUST(1006, "CULTURE_COUNT_EXHAUST"),
    ADMISSION_COUNT_EXHAUST(1007, "ADMISSION_COUNT_EXHAUST"),
    NOT_PAYED(1008, "NOT_PAYED"),
    INTERNAL_ERROR(2000, "INTERNAL ERROR"),
    NO_AUTH(3000, "NO_AUTH"),
    TOKEN_FAILURE(2000, "token已失效"),
    NO_AUDIT(808, "NO_AUDIT");

    ResponseMetaEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
