package com.novagames.common.exception;
public class BusinessException extends RuntimeException {
    public final int status;
    public final String code;
    public BusinessException(int status,String code,String message) {
        super(message);
        this.status=status;
        this.code=code;
    }
    public static BusinessException missing(String code,String msg) {
        return new BusinessException(404,code,msg);
    }
}
