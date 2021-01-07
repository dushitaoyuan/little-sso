package com.taoyuanx.sso.core.dto;

/**
 * 结果构造
 */
public class ResultBuilder {

    public static Result success(Object data) {
        return Result.build().buildData(data).buildCode(ResultCode.OK.code);
    }


    public static Result success() {
        return Result.build().buildCode(ResultCode.OK.code);
    }

    public static Result successData(String data) {
        return Result.build().buildData(data).buildCode(ResultCode.OK.code);
    }

    public static Result success(String msg) {
        return Result.build().buildCode(ResultCode.OK.code).buildMsg(msg);
    }


    public static Result failed(String msg) {
        return Result.build().buildCode(ResultCode.FAIL.code).buildMsg(msg);
    }


    public static Result failed(Integer code, String msg) {
        return Result.build().buildCode(code).buildMsg(msg);
    }
}
