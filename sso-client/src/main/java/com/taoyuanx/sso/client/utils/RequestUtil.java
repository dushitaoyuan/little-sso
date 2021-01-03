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


}
