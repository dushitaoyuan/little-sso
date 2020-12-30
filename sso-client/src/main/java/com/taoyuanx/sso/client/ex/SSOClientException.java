package com.taoyuanx.sso.client.ex;

import java.util.Objects;

public class SSOClientException extends RuntimeException {
    private static final long serialVersionUID = 8793672380339632040L;
    private Integer errorCode;

    public SSOClientException(String msg) {
        super(msg);
        this.errorCode = 500;
    }

    public SSOClientException(String msg, Throwable e) {
        super(msg, e);
        this.errorCode = 500;
    }

    public SSOClientException(Integer errorCode, String msg) {
        super(msg);
        if (Objects.nonNull(errorCode)) {
            this.errorCode = errorCode;
        }

    }


    public Integer getErrorCode() {
        return errorCode;
    }

}
