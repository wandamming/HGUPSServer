package com.hgups.express.domain;


import com.hgups.express.constant.ResponseCode;
import org.apache.commons.lang3.StringUtils;

public class Response<T>{
    /**
     * 200是正确的 其他都是错误的
     */
    public int statusCode = 200;

    /**
     * 状态描述
     */
    public String msg = "成功";

    /**
     * 用来返回各种数据
     */
    public T data;

    public Response() {
    }
    public Response(T data) {
        this.data = data;
    }

    public Response(int statusCode, String msg, T data) {
        this.statusCode = statusCode;
        this.msg = msg;
        this.data = data;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        setMsg();
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private void setMsg() {
        msg = ResponseCode.getMsg(statusCode);
    }

    public void setFail() {
        setStatusCode(ResponseCode.FAILED_CODE);
    }

    public void setSuccess() {
        setStatusCode(ResponseCode.SUCCESS_CODE);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "statusCode=" + statusCode +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public void setResponseByErrorMsg(String errorMsg){
        if(StringUtils.isNotBlank(errorMsg)){
            setStatusCode(ResponseCode.FAILED_CODE);
            setMsg("失败："+errorMsg);
        }
    }
    public void setResponseBySuccessMsg(String successMsg){
        if(StringUtils.isNotBlank(successMsg)){
            setStatusCode(ResponseCode.SUCCESS_CODE);
            setMsg("成功："+successMsg);
        }
    }
}
