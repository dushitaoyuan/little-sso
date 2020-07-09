package com.taoyuanx.littlesso.server.enums;

/**
 * @author dushitaoyuan
 * @desc ukey hash枚举
 * @date 2019/12/17
 */
public enum UkeyHashTypeEnum {

    MD5(1, "md5"),
    SHA1(2, "sha1");


    public int code;
    public String desc;

    UkeyHashTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static UkeyHashTypeEnum type(Integer hashType){
        if(hashType==null){
            return null;
        }
        UkeyHashTypeEnum[] values = UkeyHashTypeEnum.values();
        for(UkeyHashTypeEnum typeEnum:values){
            if(hashType.equals(typeEnum.code)){
                return typeEnum;
            }
        }
        return null;
    }
}
