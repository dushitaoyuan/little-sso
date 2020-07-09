package com.taoyuanx.littlesso.server.utils;

import com.ncs.pm.commons.utils.HashUtil;
import org.apache.commons.codec.binary.Base64;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author lianglei
 * @date 2019/1/9 17:08
 * @desc 会话工具类
 **/
public class SessionUtil {
    public static final String SESSION_SPILT = ".";

    /**
     * 构造全局sessionId
     * 构造算法: data.sign(data+hash(data).sub(dataHash.length() - 4))
     *
     * @param sessionDataList session数据
     * @return
     */
    public static String makeSessionId(String ... sessionDataList) {
        try {
            String data = sessionDataToString(sessionDataList);
            String base64Data = Base64.encodeBase64URLSafeString(data.getBytes("UTF-8"));
            String dataHash = HashUtil.hash(data, HashUtil.MD5, HashUtil.HEX);
            String suffix = dataHash.substring(dataHash.length() - 4);
            String sign = HashUtil.hash(data + suffix, HashUtil.MD5, HashUtil.HEX);
            return format(base64Data, sign);
        } catch (Exception e) {
            throw new RuntimeException("构造sessionId", e);
        }
    }


    public static String[] parseSessionData(String sessionId) {
        try {
            String split = "\\" + SESSION_SPILT;
            String[] splits = sessionId.split(split);
            String data = new String(Base64.decodeBase64(splits[0]), "UTF-8");
            return data.split(split);
        } catch (Exception e) {
            throw new RuntimeException("解析session数据失败", e);
        }
    }

    private static String sessionDataToString(String... sessionDataList) {
        try {
            if (sessionDataList == null || sessionDataList.length == 0) {
                return null;
            }
            String data =Arrays.stream(sessionDataList).collect(Collectors.joining(SESSION_SPILT));
            return Base64.encodeBase64URLSafeString(data.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException("解析session数据失败", e);
        }
    }

    public static boolean isValidSessionId(String sessionId) {
        try {
            String[] splits = sessionId.split("\\" + SESSION_SPILT);
            String base64Data = splits[0];
            String sign = splits[1];
            String data = new String(Base64.decodeBase64(base64Data), "UTF-8");
            String dataHash = HashUtil.hash(data, HashUtil.MD5, HashUtil.HEX);
            String suffix = dataHash.substring(dataHash.length() - 4);
            String calcSign = HashUtil.hash(data + suffix, HashUtil.MD5, HashUtil.HEX);
            if (calcSign.equalsIgnoreCase(sign)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }


    //位置标记
    private static final int DATA_INDEX = 0, SIGN_INDEX = 1;

    private static String format(String data, String sign) {
        return data + SESSION_SPILT + sign;
    }


}
