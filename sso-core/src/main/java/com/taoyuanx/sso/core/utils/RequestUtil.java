package com.taoyuanx.sso.core.utils;


import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * @date 2019/1/7 12:41
 * @desc 请求工具
 **/
public class RequestUtil {


    public static String getCookieValue(HttpServletRequest request, String key) {
        return CookieUtil.getCookieValue(request, key);
    }

    public static String getHeaderOrParamValue(HttpServletRequest request, String key) {
        //fetch odrer  header > parameter
        String value = request.getHeader(key);
        if (HelperUtil.isNotEmpty(value)) {
            return value;
        } else {
            return request.getParameter(key);
        }
    }

    public static String getCookieDomain(HttpServletRequest request, String configDomain) {
        if (HelperUtil.isEmpty(configDomain)) {
            return request.getServerName();
        }
        return configDomain;
    }


    public static String addParamToUrl(String url, String paramKey, String paramValue) {
        int flagIndex = url.indexOf("?");
        if (flagIndex > -1) {
            return url.substring(0, flagIndex) + "?" + paramKey + "=" + paramValue + "&" + url.substring(flagIndex + 1);
        }
        return url + "?" + paramKey + "=" + paramValue;
    }


}
