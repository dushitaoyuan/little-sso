package com.taoyuanx.littlesso.server.utils;

import cn.hutool.core.util.ArrayUtil;
import com.taoyuanx.littlesso.server.commons.AccountConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @author lianglei
 * @date 2019/1/7 12:41
 * @desc 请求工具
 **/
public class RequestUtil {
    public static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getCurrentResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forward-for");
        String unknow = "unknown";
        if (StringUtils.isEmpty(ip) || unknow.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || unknow.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || unknow.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isEmpty(ip) || unknow.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isEmpty(ip) || unknow.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getValue(HttpServletRequest request, String key) {
        //优先级 参数，header，cookie
        String value = request.getParameter(key);
        if (StringUtils.isNotEmpty(value)) {
            return value;
        }
        value = request.getHeader(key);
        if (StringUtils.isNotEmpty(value)) {
            return value;
        } else {
            return CookieUtil.getCookieValue(request, key);
        }
    }


    public static String getRedirectUrl(HttpServletRequest request) {
        String redirectUrl = request.getParameter(AccountConstant.REDIRECT_URL_PARAM_KEY);
        if (StringUtils.isNotEmpty(redirectUrl)) {
            return redirectUrl;
        }
        return request.getParameter(AccountConstant.REDIRECT_URL_PARAM_KEY_OLD);
    }

    public static String getCookieDomain(HttpServletRequest request, String configDomain) {
        if (StringUtils.isEmpty(configDomain)) {
            return request.getServerName();
        }
        return configDomain;
    }

    public static String getQueryString(HttpServletRequest request, String... excludeKeys) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        StringBuilder queryString = new StringBuilder("?");
        parameterMap.entrySet().stream().filter(entry -> {
            String key = entry.getKey();
            String[] value = entry.getValue();
            if (StringUtils.isEmpty(key) || Objects.isNull(value) || value.length == 0) {
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
    }
}
