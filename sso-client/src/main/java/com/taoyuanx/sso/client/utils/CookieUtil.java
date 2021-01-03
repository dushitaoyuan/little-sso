package com.taoyuanx.sso.client.utils;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;


public class CookieUtil {

    public static void addCookie(HttpServletResponse response, String name, String value, String domain, Integer maxAge) {
        Cookie cookie = new Cookie(name, value);
        if (StrUtil.isNotEmpty(domain)) {
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

    public static void deleteCookieValue(HttpServletRequest request, HttpServletResponse response, String name, String cookieDomain, String cookiePath) {
        Cookie cookie = getCookie(request, name);
        if (Objects.nonNull(cookie)) {
            Cookie deleteCookie = new Cookie(name, "");
            deleteCookie.setMaxAge(0);

            if (StrUtil.isNotEmpty(cookie.getDomain())) {
                cookieDomain = cookie.getDomain();
            }
            if (StrUtil.isNotEmpty(cookie.getPath())) {
                cookiePath = cookie.getPath();
            }
            deleteCookie.setPath(cookiePath);
            deleteCookie.setDomain(cookieDomain);
            response.addCookie(cookie);
        }

    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        if (Objects.nonNull(cookie)) {
            return cookie.getValue();
        }
        return null;
    }

    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }


}
