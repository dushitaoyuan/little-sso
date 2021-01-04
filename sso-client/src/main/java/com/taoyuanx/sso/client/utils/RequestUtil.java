package com.taoyuanx.sso.client.utils;


import javax.servlet.http.HttpServletRequest;



public class RequestUtil {


    public static String getCookieValue(HttpServletRequest request, String key) {
        return CookieUtil.getCookieValue(request, key);
    }

    public static String getHeaderOrParamValue(HttpServletRequest request, String key) {
        //fetch odrer  header > parameter
        String value = request.getHeader(key);
        if (StrUtil.isNotEmpty(value)) {
            return value;
        } else {
            return request.getParameter(key);
        }
    }

    public static String addParamToUrl(String url, String paramKey, String paramValue) {
        int flagIndex = url.indexOf("?");
        if (flagIndex > -1) {
            return url.substring(0, flagIndex) + "?" + paramKey + "=" + paramValue + "&" + url.substring(flagIndex + 1);
        }
        return url + "?" + paramKey + "=" + paramValue;
    }
}
