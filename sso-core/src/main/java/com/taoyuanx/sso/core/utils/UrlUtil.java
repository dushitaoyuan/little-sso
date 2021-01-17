package com.taoyuanx.sso.core.utils;

import com.taoyuanx.sso.core.token.sign.impl.HMacSign;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dushitaoyuan
 * @date 2021/1/1618:32
 */
public class UrlUtil {


    public static final String SIGN_KEY = "_s", END_KEY = "_e";


    public static String addParamToUrl(String url, String paramKey, String paramValue) {
        int flagIndex = url.indexOf("?");
        if (flagIndex > -1) {
            return url.substring(0, flagIndex) + "?" + paramKey + "=" + paramValue + "&" + url.substring(flagIndex + 1);
        }
        return url + "?" + paramKey + "=" + paramValue;
    }

    public static String addParamToUrl(String url, Map<String, String> signParam) {
        for (String key : signParam.keySet()) {
            url = addParamToUrl(url, key, signParam.get(key));
        }
        return url;
    }

    public static String addParamAndSign(String url, String signKey, int expire, TimeUnit timeUnit, Map<String, String> signParam) {
        HMacSign hMacSign = new HMacSign(signKey.getBytes());
        Long end = System.currentTimeMillis() + timeUnit.toMillis(expire);
        signParam.put(END_KEY, String.valueOf(end));
        String signStr = signParam.entrySet().stream().sorted().map(key -> {
            return signParam.get(key);
        }).collect(Collectors.joining(","));
        String sign = Base64.encodeBase64URLSafeString(hMacSign.sign(signStr.getBytes()));
        signParam.put(SIGN_KEY, sign);
        return addParamToUrl(url, signParam);

    }

    public static boolean verifyUrl(HttpServletRequest request, String signKey, String... signParamKey) {
        String end = request.getParameter(END_KEY);
        String sign = request.getParameter(SIGN_KEY);
        if (HelperUtil.isEmpty(sign) || HelperUtil.isEmpty(end)) {
            return false;
        }
        String signStr = Stream.concat(Arrays.stream(signParamKey), Stream.of(END_KEY)).distinct().sorted().map(key -> {
            return request.getParameter(key);
        }).collect(Collectors.joining(","));
        if (Long.parseLong(end) > System.currentTimeMillis()) {
            HMacSign hMacSign = new HMacSign(signKey.getBytes());
            return hMacSign.verifySign(signStr.getBytes(), Base64.decodeBase64(sign));
        }
        return false;
    }


}
