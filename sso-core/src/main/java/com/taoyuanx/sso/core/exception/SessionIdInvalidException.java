package com.taoyuanx.sso.core.exception;


/**
 * @author dushitaoyuan
 */
public class SessionIdInvalidException extends SSOException {
    public SessionIdInvalidException() {
        super("sessionId invalid");
    }


    public SessionIdInvalidException(Throwable cause) {
        super("sessionId invalid", cause);
    }
}
