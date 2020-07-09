package com.taoyuanx.littlesso.server.exception;

/**
 * @author dushitaoyuan
 * @desc 参数异常
 * @date 2019/8/29
 */
public class ValidatorException extends RuntimeException {
    public ValidatorException(String message) {
        super(message);
    }
}
