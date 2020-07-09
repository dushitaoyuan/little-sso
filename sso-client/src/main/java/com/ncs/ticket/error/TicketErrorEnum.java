package com.ncs.ticket.error;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lianglei
 * @date 2019/1/7 18:14
 * @desc 票据异常分类
 **/
public enum  TicketErrorEnum {
    EXPIRE(1,"票据过期"),
    SING_ERROR(2,"签名非法"),
    FORMAT_ERROR(3,"格式非法"),
    UNKNOW_ERROR(4,"未知异常");
    public int code;
    public String desc;
    private  static  final Map<Integer, TicketErrorEnum> enumHolder=new HashMap<>();
    static {
        TicketErrorEnum[] loginTypeArray = TicketErrorEnum.values();
        Arrays.stream(loginTypeArray).forEach(loginType -> {
            enumHolder.put(loginType.code,loginType);
        });
    }
    TicketErrorEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TicketErrorEnum errorType(Integer loginType){
        return enumHolder.get(loginType);
    }
}
