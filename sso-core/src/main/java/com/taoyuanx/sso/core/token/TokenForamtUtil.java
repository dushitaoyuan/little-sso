package com.taoyuanx.sso.core.token;

import org.apache.commons.codec.binary.Base64;

/**
 * @author lianglei
 * @date 2020/01/17 9:43
 * @desc token格式化工具 格式：data.sign
 **/
public class TokenForamtUtil {
    public static final String TOKEN_SPLIT = ".";

    /**
     * 位置标记
     */
    public static final int DATA_INDEX = 0, SING_INDEX = 1;

    public static String format(String data, String sign) {
        return data + TOKEN_SPLIT + sign;
    }


    public static String format(byte[] data, byte[] sign) {
        return format(byteToString(data), byteToString(sign));
    }

    public static String byteToString(byte[] data) {
        return Base64.encodeBase64URLSafeString(data);
    }

    public static byte[] stringToByte(String string) {
        return Base64.decodeBase64(string);
    }

    public static String[] splitToken(String token) {
        return token.split("\\" + TOKEN_SPLIT);
    }

    public static byte[][] splitTokenToByte(String token) {
        String[] split = splitToken(token);
        byte[][] bytes = new byte[split.length][];
        for (int i = 0; i < split.length; i++) {
            bytes[i] = stringToByte(split[i]);
        }
        return bytes;
    }


}
