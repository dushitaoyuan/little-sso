package com.ncs.ticket.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lianglei
 * @date 2019/1/8 10:49
 * @desc 票据加密类型枚举
 **/
public enum  TicketEncodeEnum {

    RSA(1, "RSA加密"),
    AES(2, "AES加密");


    public int code;
    public String desc;
    private  static  final Map<Integer, TicketEncodeEnum> enumHolder=new HashMap<>();
    static {
        TicketEncodeEnum[] loginTypeArray = TicketEncodeEnum.values();
        Arrays.stream(loginTypeArray).forEach(loginType -> {
            enumHolder.put(loginType.code,loginType);
        });
    }
    TicketEncodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TicketEncodeEnum type(Integer loginType){
        return enumHolder.get(loginType);
    }
}
