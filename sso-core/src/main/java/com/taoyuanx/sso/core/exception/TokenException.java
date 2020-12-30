package com.taoyuanx.sso.core.exception;


/**
 * @author dushitaoyuan
 * @desc token异常
 */
public class TokenException extends RuntimeException {
    public TokenException() {
    }

    public TokenException(String message) {
        super(message);
    }


    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
