package com.ncs.ticket;

import org.apache.commons.codec.binary.Base64;

/**
 * @author lianglei
 * @date 2019/1/8 9:43
 * @desc token格式化工具 格式：header(可以没有,明文).data.sign
 **/
public class TokenForamtUtil {
    public static final String TOKEN_SPLIT = ".";

    //位置标记
    public static final int HEADER_INDEX = 0, DATA_NOHEADER_INDEX = 0, DATA_HEADER_INDEX = 1;

    public static String format(String data, String sign) {
        return data + TOKEN_SPLIT + sign;
    }

    public static String format(String header, String data, String sign) {
        return header + TOKEN_SPLIT + data + TOKEN_SPLIT + sign;
    }

    public static String format(byte[] data, byte[] sign) {
        return format(Base64.encodeBase64URLSafeString(data), Base64.encodeBase64URLSafeString(sign));
    }

    public static String format(byte[] header, byte[] data, byte[] sign) {
        return format(Base64.encodeBase64URLSafeString(header), Base64.encodeBase64URLSafeString(data), Base64.encodeBase64URLSafeString(sign));
    }

    public static String[] splitToken(String token) {
        return token.split("\\" + TOKEN_SPLIT);
    }

    public static byte[][] splitTokenToByte(String token) {
        String[] split = splitToken(token);
        byte[][] bytes = new byte[split.length][];
        for (int i = 0; i < split.length; i++) {
            bytes[i] = Base64.decodeBase64(split[i]);
        }
        return bytes;
    }


}
