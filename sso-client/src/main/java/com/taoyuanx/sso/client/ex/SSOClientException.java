package com.taoyuanx.sso.client.ex;


public class SSOClientException extends RuntimeException {

    private Integer code;

    public SSOClientException(String msg) {
        super(msg);
    }

    public SSOClientException(String msg, Throwable e) {
        super(msg, e);
    }

    public SSOClientException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
