package com.taoyuanx.sso.core.dto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 响应码枚举，参考HTTP状态码的语义
 */
public enum ResultCode {
    OK(1, "success"),
    FAIL(0, "failed"),
    UNAUTHORIZED(401, "权限异常"),
    NOT_FOUND(404, "service not found"),
    UN_SUPPORT_MEDIATYPE(415, "不支持媒体类型"),
    PARAM_ERROR(400, "参数异常"),
    TOO_MANY_REQUESTS(429, "请求被限制"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    BUSSINESS_ERROR(1001, "业务异常");
    public int code;
    public String desc;

     ResultCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    private  static  final Map<Integer, ResultCode> enumHolder=new HashMap<>();
    static {
        ResultCode[] enumArray  = ResultCode.values();
        Arrays.stream(enumArray).forEach(enumObj -> {
            enumHolder.put(enumObj.code,enumObj);
        });
    }

    public static ResultCode resultCode(Integer code){
        return enumHolder.get(code);
    }
}