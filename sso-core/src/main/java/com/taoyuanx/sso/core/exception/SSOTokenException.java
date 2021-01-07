package com.taoyuanx.sso.core.exception;


/**
 * @author dushitaoyuan
 * @desc token异常
 */
public class SSOTokenException extends SSOException {
    public SSOTokenException() {
    }

    public SSOTokenException(String message) {
        super(message);
    }


    public SSOTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
