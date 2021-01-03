package com.taoyuanx.sso.client.ex;

import java.util.Objects;

public class SSOClientException extends RuntimeException {

    public SSOClientException(String msg) {
        super(msg);
    }

    public SSOClientException(String msg, Throwable e) {
        super(msg, e);
    }

    public SSOClientException(Integer errorCode, String msg) {
        super(msg);
    }



}
