package com.taoyuanx.sso.client.filter;


import com.alibaba.fastjson.JSON;
import com.taoyuanx.sso.client.core.Result;
import com.taoyuanx.sso.client.core.SSOClientConfig;
import com.taoyuanx.sso.client.core.SSOClientConstant;
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

/**
 * @author dushitaoyuan
 * @desc sso过滤器
 * @date 2020/12/29
 */
@Slf4j
@Setter
@Getter
public class SSOFilter implements Filter {
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private SSOClientConfig ssoClientConfig;

    private SSOClient ssoClient;


    public SSOFilter() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String sessionId = getSessionId(request);
        /**
         * logut url
         */
        if (isLogout(requestURI, method)) {
            try {
                ssoClient.isSessionIdValid(sessionId);
                ssoClient.logout(sessionId);
                response.sendRedirect(ssoClientConfig.getClientLoginUrl());
            } catch (SSOClientException e) {
                /**
                 * delete cookie
                 */
                CookieUtil.deleteCookieValue(request, response, ssoClientConfig.getSessionKeyName());
                log.error("check session failed", e);
                toLogin(request, response);
            }
            return;
        }
        /**
         *  match url check isLogin
         *  if not login redirect login
         */

        if (isPathFilter(requestURI)) {
            try {
                ssoClient.isSessionIdValid(sessionId);
                if (!ssoClient.loginCheck(sessionId)) {
                    throw new SSOClientException("sessionId[" + sessionId + "] is invalid");
                }
            } catch (SSOClientException e) {
                log.error("check login failed", e);
                /**
                 * delete cookie
                 */
                CookieUtil.deleteCookieValue(request, response, ssoClientConfig.getSessionKeyName());
                toLogin(request, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPathFilter(String requestURI) {
        List<String> filterExcludePath = ssoClientConfig.getFilterExcludePath();
        for (int i = 0, len = filterExcludePath.size(); i < len; i++) {
            String exclude = filterExcludePath.get(i);
            if (antPathMatcher.match(exclude, requestURI)) {
                return false;
            }
        }
        List<String> filterIncludePath = ssoClientConfig.getFilterIncludePath();
        for (int i = 0, len = filterIncludePath.size(); i < len; i++) {
            String matchPath = filterIncludePath.get(i);
            if (antPathMatcher.match(matchPath, requestURI)) {
                log.debug("{} match sso filter ", requestURI);
                return true;
            }
        }
        return false;
    }

    private boolean isLogout(String requestURI, String method) {
        if (antPathMatcher.match(ssoClientConfig.getClientLogoutUrl(), requestURI)) {
            String clientLogoutMethod = ssoClientConfig.getClientLogoutMethod();
            return StrUtil.isEmpty(clientLogoutMethod) || method.equalsIgnoreCase(clientLogoutMethod);
        }
        return false;
    }


    private String getSessionId(HttpServletRequest request) {
        String sessionKeyName = ssoClientConfig.getSessionKeyName();
        //fetch odrer  parameter > header > cookie
        String value = request.getParameter(sessionKeyName);
        if (StrUtil.isNotEmpty(value)) {
            return value;
        }
        value = request.getHeader(sessionKeyName);
        if (StrUtil.isNotEmpty(value)) {
            return value;
        } else {
            return CookieUtil.getCookieValue(request, sessionKeyName);
        }
    }

    private void toLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (ResponseUtil.isAcceptJson(request)) {
            Result result = new Result();
            result.setCode(SSOClientConstant.NOT_LOGIN_ERROR_CODE);
            result.setMsg("未登录,请先登录");
            ResponseUtil.responseJson(response, JSON.toJSONString(result), 200);
        } else {
            response.sendRedirect(ssoClientConfig.getClientLoginUrl());
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String configPath = filterConfig.getInitParameter(SSOClientConstant.SSO_CLIENT_CONFIG_NAME);
        if (StrUtil.isNotEmpty(configPath)) {
            ssoClientConfig = new SSOClientConfig(configPath);
        } else {
            ssoClientConfig = new SSOClientConfig();
        }
        ssoClient = new SSOClientImpl(ssoClientConfig);


    }
}
