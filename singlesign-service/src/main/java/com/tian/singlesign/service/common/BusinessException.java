package com.tian.singlesign.service.common;

/**
 * 业务异常类
 * Created by Administrator on 2016/11/17 0017.
 */
public class BusinessException extends RuntimeException{
    /** 异常码: 建议5XX*/
    private int errorCode;
    /** 异常信息描述*/
    private String errorMessage;
    public BusinessException(){}
    public BusinessException(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
