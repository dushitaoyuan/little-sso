package com.taoyuanx.sso.client.utils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author dushitaoyuan
 * @date 2019/8/30
 */
public class ResponseUtil {
    public static void responseJson(HttpServletResponse response, String result, Integer httpStatus) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(httpStatus);
        try {
            response.getWriter().write(result);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    public static boolean isAcceptJson(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        if ((accept != null && accept.contains("json"))) {
            return true;
        } else {
            return false;
        }
    }


}
