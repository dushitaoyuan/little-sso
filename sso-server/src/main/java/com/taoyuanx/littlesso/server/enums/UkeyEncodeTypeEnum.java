package com.taoyuanx.littlesso.server.enums;

/**
 * @author dushitaoyuan
 * @desc ukey encodeType枚举
 * @date 2019/12/17
 */
public enum UkeyEncodeTypeEnum {

    SOFT(1, "软加密"),
    MACHINE(2, "硬加密｜加密机");


    public int code;
    public String desc;

    UkeyEncodeTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static UkeyEncodeTypeEnum type(Integer encodeType){
        if(encodeType==null){
            return null;
        }
        UkeyEncodeTypeEnum[] values = UkeyEncodeTypeEnum.values();
        for(UkeyEncodeTypeEnum typeEnum:values){
            if(encodeType.equals(typeEnum.code)){
                return typeEnum;
            }
        }
        return null;
    }
}
