package com.taoyuanx.sso.core.utils;


import javax.servlet.http.HttpServletRequest;


/**
 * @date 2019/1/7 12:41
 * @desc 请求工具
 **/
public class RequestUtil {


    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forward-for");
        String unknow = "unknown";
        if (HelperUtil.isEmpty(ip) || unknow.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (HelperUtil.isEmpty(ip) || unknow.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (HelperUtil.isEmpty(ip) || unknow.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (HelperUtil.isEmpty(ip) || unknow.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (HelperUtil.isEmpty(ip) || unknow.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getValue(HttpServletRequest request, String key) {
        //fetch odrer  parameter > header > cookie
        String value = request.getParameter(key);
        if (HelperUtil.isNotEmpty(value)) {
            return value;
        }
        value = request.getHeader(key);
        if (HelperUtil.isNotEmpty(value)) {
            return value;
        } else {
            return CookieUtil.getCookieValue(request, key);
        }
    }


    public static String getCookieDomain(HttpServletRequest request, String configDomain) {
        if (HelperUtil.isEmpty(configDomain)) {
            return request.getServerName();
        }
        return configDomain;
    }

/*    public static String getQueryString(HttpServletRequest request, String... excludeKeys) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        StringBuilder queryString = new StringBuilder("?");
        parameterMap.entrySet().stream().filter(entry -> {
            String key = entry.getKey();
            String[] value = entry.getValue();
            if (HelperUtil.isEmpty(key) || Objects.isNull(value) || value.length == 0) {
                return false;
            }
            return !ArrayUtil.contains(excludeKeys, key);
        }).forEach(entry -> {
            String key = entry.getKey();
            Arrays.stream(entry.getValue()).forEach(value -> {
                if (!"&?".contains(queryString.charAt(queryString.length()-1)+"")) {
                    queryString.append("&");
                }
                queryString.append(key).append("=").append(value);
            });
        });
        if (queryString.length() > 1) {
            return queryString.toString();
        }
        return "";
    }*/
}
