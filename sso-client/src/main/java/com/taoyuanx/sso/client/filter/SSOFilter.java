package com.taoyuanx.sso.client.filter;


import com.alibaba.fastjson.JSON;
import com.taoyuanx.sso.client.core.Result;
import com.taoyuanx.sso.client.core.SSOClientConfig;
import com.taoyuanx.sso.client.core.SSOClientConstant;
import com.taoyuanx.sso.client.core.sign.sign.impl.HMacVerifySign;
import com.taoyuanx.sso.client.ex.SessionIdInvalidClientException;
import com.taoyuanx.sso.client.impl.SSOClient;
import com.taoyuanx.sso.client.impl.SSOClientImpl;
import com.taoyuanx.sso.client.utils.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author dushitaoyuan
 * @desc sso过滤器
 * @date 2020/12/29
 */
@Slf4j

public class SSOFilter implements Filter {
    protected static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    protected SSOClientConfig ssoClientConfig;

    protected SSOClient ssoClient;


    public SSOFilter() {

    }

    public SSOFilter(SSOClientConfig ssoClientConfig, SSOClient ssoClient) {
        this.ssoClientConfig = ssoClientConfig;
        this.ssoClient = ssoClient;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        if (isResources(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        String sessionId = ssoClient.getSessionId(request);
        try {
            /**
             *  when  ssoUrl  ，clientLogoutPath is config
             *   not login visit filterIncludePath redirect to sso loginUrl
             *   visit logoutPath logout from ssoServer then  redirect to sso loginUrl
             *   or sso-app handle login or logout logic
             */
            if (matchLogout(requestURI, method)) {
                ssoClient.logout(sessionId);
                logOutSuccessHandler(request, response);
                return;
            }

            /**
             *  match url check isLogin
             */
            if (isPathFilter(requestURI)) {
                if (ssoClient.loginCheck(sessionId)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                checkLoginFailedHandler(request, response);
                return;
            }
        } catch (SessionIdInvalidClientException e) {
            log.debug("sessionId[" + sessionId + "] is invalid", e);
            checkLoginFailedHandler(request, response);
            return;
        }
        filterChain.doFilter(request, response);

    }

    private static final Pattern RESOURCES_PATTERN = Pattern.compile(".*\\..*$");

    private boolean isResources(String requestURI) {
        return RESOURCES_PATTERN.matcher(requestURI).matches();
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

    private boolean matchLogout(String requestURI, String method) {

        if (Objects.nonNull(ssoClientConfig.getClientLogoutPath()) && antPathMatcher.match(ssoClientConfig.getClientLogoutPath(), requestURI)) {
            String clientLogoutMethod = ssoClientConfig.getClientLogoutMethod();
            return StrUtil.isEmpty(clientLogoutMethod) || method.equalsIgnoreCase(clientLogoutMethod);
        }
        return false;
    }


    public void checkLoginFailedHandler(HttpServletRequest request, HttpServletResponse response) {

        try {
            if (ssoClientConfig.isEnableCookie()) {
                /**
                 * delete cookie and  redirect  to login
                 */
                CookieUtil.deleteCookieValue(request, response, ssoClientConfig.getSessionKeyName(), ssoClientConfig.getSessionIdCookieDomain(), SSOClientConstant.COOKIE_STORE_PATH);
            }
            if (ResponseUtil.isAcceptJson(request)) {
                Result result = new Result();
                result.setCode(SSOClientConstant.LOGIN_CHECK_FAILED_CODE);
                result.setMsg("login check failed,sso-client application must delete the sessionId and  must relogin");
                Map toLogin = new HashMap<>();
                String loginUrl = RequestUtil.addParamToUrl(ssoClientConfig.getSsoLoginUrl(), SSOClientConstant.REDIRECT_URL, ssoClientConfig.getRedirectUrl());
                toLogin.put(SSOClientConstant.REDIRECT_URL, loginUrl);
                result.setData(JSON.toJSONString(toLogin));
                ResponseUtil.responseJson(response, JSON.toJSONString(result), 200);
            } else {
                String loginUrl = RequestUtil.addParamToUrl(ssoClientConfig.getSsoLoginUrl(), SSOClientConstant.REDIRECT_URL, ssoClientConfig.getRedirectUrl());
                response.sendRedirect(loginUrl);
            }

        } catch (Exception e) {
            log.warn("to login error", e);
        }
    }


    public void logOutSuccessHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (ssoClientConfig.isEnableCookie()) {
            /**
             * delete cookie and  redirect  to login
             */
            CookieUtil.deleteCookieValue(request, response, ssoClientConfig.getSessionKeyName(), ssoClientConfig.getSessionIdCookieDomain(), SSOClientConstant.COOKIE_STORE_PATH);
        }
        Result result = new Result();
        result.setCode(Result.SUCCESS_CODE);
        result.setMsg("logout success");
        ResponseUtil.responseJson(response, JSON.toJSONString(result), 200);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (Objects.nonNull(ssoClientConfig)) {
            String configPath = filterConfig.getInitParameter(SSOClientConstant.SSO_CLIENT_CONFIG_NAME);
            if (StrUtil.isNotEmpty(configPath)) {
                ssoClientConfig = new SSOClientConfig(configPath);
            } else {
                ssoClientConfig = new SSOClientConfig();
            }

        }
        if (Objects.nonNull(ssoClient)) {
            ssoClient = new SSOClientImpl(ssoClientConfig, new HMacVerifySign(ssoClientConfig.getSessionIdSignHmacKey().getBytes()));
        }
    }


}
