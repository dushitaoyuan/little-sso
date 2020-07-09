package com.taoyuanx.littlesso.server.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lianglei
 * @date 2019/11/12 12:47
 * @desc 证书通用工具类
 **/
@Slf4j
public class StringFormatUtil {
    //0填充
    public static String zeroPadding(int bitNum, Object number) {

        if (Objects.isNull(number)) {
            return "";
        }
        String numberStr = String.valueOf(number);
        if (numberStr.length() > bitNum) {
            return numberStr.substring(0, bitNum);
        }
        StringBuilder zeroPading = new StringBuilder();
        for (int i = 0, len = bitNum - numberStr.length(); i < len; i++) {
            zeroPading.append("0");
        }
        zeroPading.append(number);
        return zeroPading.toString();
    }

    public static String reEncoding(String str, String srcCharSet, String destCharSet) {
        try {
            if (Objects.isNull(srcCharSet)) {
                return new String(str.getBytes(), destCharSet);
            } else {
                return new String(str.getBytes(srcCharSet), destCharSet);
            }
        } catch (Exception e) {
            log.warn("字符[{}]重编码异常,目的编码为:{},原始编码为;{}", str, destCharSet, srcCharSet);
            return str;
        }
    }




}
