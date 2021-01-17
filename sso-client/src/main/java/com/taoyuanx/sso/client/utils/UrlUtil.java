package com.taoyuanx.sso.client.utils;

import com.taoyuanx.sso.client.core.SSOClientConfig;
import com.taoyuanx.sso.client.core.SSOClientConstant;
import com.taoyuanx.sso.client.core.sign.sign.impl.HMacVerifySign;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dushitaoyuan
 * @date 2021/1/1618:32
 */
public class UrlUtil {


    public static final String SIGN_KEY = "_s", END_KEY = "_e";

    public static boolean verifyUrl(HttpServletRequest request, String signKey, String... signParamKey) {
        String end = request.getParameter(END_KEY);
        String sign = request.getParameter(SIGN_KEY);
        if (StrUtil.isEmpty(sign) || StrUtil.isEmpty(end)) {
            return false;
        }
        String signStr = Stream.concat(Arrays.stream(signParamKey), Stream.of(END_KEY)).filter(StrUtil::isNotEmpty).distinct().sorted().map(key -> {
            return request.getParameter(key);
        }).collect(Collectors.joining(","));
        if (Long.parseLong(end) > System.currentTimeMillis()) {
            HMacVerifySign hMacSign = new HMacVerifySign(signKey.getBytes());
            return hMacSign.verifySign(signStr.getBytes(), Base64.decodeBase64(sign));
        }
        return false;
    }

    public static boolean verifyUrl(HttpServletRequest request, SSOClientConfig clientConfig) {
        /**
         * 未开启url 校验，则不校验
         */
        if (!clientConfig.isEnableRedirectUrlCheck()) {
            return true;
        }
        if (clientConfig.getSessionMode().equals(SSOClientConstant.SESSION_MODE_SERVER)) {
            if (clientConfig.isEnableCookie()) {
                return verifyUrl(request, "");
            } else {
                return verifyUrl(request, clientConfig.getSessionKeyName());
            }
        } else if (clientConfig.getSessionMode().equals(SSOClientConstant.SESSION_MODE_CLIENT)) {
            return verifyUrl(request, SSOClientConstant.SSO_SESSION_TOKEN, SSOClientConstant.SSO_REFRESH_TOKEN, SSOClientConstant.SSO_TOKEN_EXPIRE);
        }

        return false;
    }

}
