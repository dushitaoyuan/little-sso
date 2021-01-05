package com.taoyuanx.sso.client.ex;



public class SSOTokenException extends RuntimeException {


    public SSOTokenException(String msg) {
        super(msg);
    }

    public SSOTokenException(String msg, Throwable e) {
        super(msg, e);
    }

    public SSOTokenException(Throwable e) {
        super(e);
    }


}
