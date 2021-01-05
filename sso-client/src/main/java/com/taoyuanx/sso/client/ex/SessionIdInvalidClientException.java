package com.taoyuanx.sso.client.ex;

import java.util.Objects;

public class SessionIdInvalidClientException extends RuntimeException {

    public SessionIdInvalidClientException(String msg) {
        super(msg);
    }

    public SessionIdInvalidClientException(String msg, Throwable e) {
        super(msg, e);
    }


    public SessionIdInvalidClientException(Throwable e) {
        super(e);
    }

}
