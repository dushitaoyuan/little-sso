package com.taoyuanx.littlesso.server.exception;

import lombok.Data;

/**
 * @author dushitaoyuan
 * @desc 异常封装
 * @date 2019/12/27
 */
@Data
public class InnerError {
    private Integer httpCode;
    private Integer errorCode;
    private String errorMsg;
    private Throwable exception;
}
