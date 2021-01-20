package com.taoyuanx.sso.core.exception;


/**
 * @author dushitaoyuan
 */
public class SSOException extends RuntimeException {

    public SSOException() {
    }

    public SSOException(String message) {
        super(message);
    }


    public SSOException(String message, Throwable cause) {
        super(message, cause);
    }
}
