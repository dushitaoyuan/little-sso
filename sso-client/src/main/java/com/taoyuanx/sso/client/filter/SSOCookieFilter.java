package com.taoyuanx.sso.client.filter;


import com.alibaba.fastjson.JSON;
import com.taoyuanx.sso.client.core.Result;
import com.taoyuanx.sso.client.core.SSOClientConfig;
import com.taoyuanx.sso.client.core.SSOClientConstant;
import com.taoyuanx.sso.client.core.sign.sign.impl.HMacVerifySign;
import com.taoyuanx.sso.client.ex.SSOClientException;
import com.taoyuanx.sso.client.impl.SSOClient;
import com.taoyuanx.sso.client.impl.SSOClientImpl;
import com.taoyuanx.sso.client.utils.AntPathMatcher;
import com.taoyuanx.sso.client.utils.CookieUtil;
import com.taoyuanx.sso.client.utils.ResponseUtil;
import com.taoyuanx.sso.client.utils.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author dushitaoyuan
 * @desc sso cookie 过滤器
 * @date 2020/12/29
 */
@Slf4j

public class SSOCookieFilter extends SSOFilter {
    public SSOCookieFilter() {
        super();
    }

    public SSOCookieFilter(SSOClientConfig ssoClientConfig, SSOClient ssoClient) {
        super(ssoClientConfig, ssoClient);
    }

    @Override
    public void checkLoginFailedHandler(HttpServletRequest request, HttpServletResponse response) {
        /**
         * delete cookie and  redirect  to login
         */
        try {
            CookieUtil.deleteCookieValue(request, response, ssoClientConfig.getSessionKeyName(), ssoClientConfig.getSessionIdCookieDomain(), SSOClientConstant.COOKIE_STORE_PATH);
            String loginUrl = ssoClientConfig.getSsoLoginUrl();
            if (!loginUrl.endsWith("?")) {
                loginUrl += "?";
            }
            loginUrl += "&" + SSOClientConstant.redirectUrl + "=" + ssoClientConfig.getRedirectUrl();
            response.sendRedirect(loginUrl);
        } catch (Exception e) {
            log.warn("to login error", e);
        }
    }

    @Override
    public void logOutSuccessHandler(HttpServletRequest request, HttpServletResponse response) {
        Result result = new Result();
        result.setCode(Result.SUCCESS_CODE);
        /**
         * delete cookie  and response data
         */
        CookieUtil.deleteCookieValue(request, response, ssoClientConfig.getSessionKeyName(), ssoClientConfig.getSessionIdCookieDomain(), SSOClientConstant.COOKIE_STORE_PATH);
        ResponseUtil.responseJson(response, JSON.toJSONString(result), 200);
    }
}
