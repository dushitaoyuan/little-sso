package com.taoyuanx.littlesso.server.utils;

import cn.hutool.core.util.ArrayUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lianglei
 * @date 2019/1/9 20:19
 * @desc cookie工具类
 **/
public class CookieUtil {

    public static void addCookie(HttpServletResponse response, String name, String value, String domain, Integer maxAge) {
        Cookie cookie = new Cookie(name, value);
        if(StringUtils.isNotEmpty(domain)){
            cookie.setDomain(domain);
        }
        cookie.setPath("/");
        if (maxAge == null) {
            maxAge = -1;
        }
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static void deleteCookieValue(HttpServletResponse response, String domain, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setDomain(domain);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (ArrayUtil.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


}
