package com.taoyuanx.sso.client.filter;


import com.alibaba.fastjson.JSON;
import com.taoyuanx.sso.client.core.Result;
import com.taoyuanx.sso.client.core.SSOClientConfig;
import com.taoyuanx.sso.client.core.SSOClientConstant;
import com.taoyuanx.sso.client.core.sign.sign.impl.HMacVerifySign;
import com.taoyuanx.sso.client.ex.SSOClientException;
import com.taoyuanx.sso.client.ex.SessionIdInvalidClientException;
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
 * @desc sso过滤器
 * @date 2020/12/29
 */
@Slf4j

public abstract class SSOFilter implements Filter {
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
                throw new SessionIdInvalidClientException("sessionId is invalid");
            }
        } catch (SessionIdInvalidClientException e) {
            log.error("sessionId[" + sessionId + "] is invalid", e);
            checkLoginFailedHandler(request, response);
            return;
        } catch (SSOClientException e) {
            Result result = new Result();
            result.setCode(SSOClientConstant.SSO_SERVER_ERROR_CODE);
            ResponseUtil.responseJson(response, JSON.toJSONString(result), 200);
            return;
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

    private boolean matchLogout(String requestURI, String method) {

        if (Objects.nonNull(ssoClientConfig.getClientLogoutPath()) && antPathMatcher.match(ssoClientConfig.getClientLogoutPath(), requestURI)) {
            String clientLogoutMethod = ssoClientConfig.getClientLogoutMethod();
            return StrUtil.isEmpty(clientLogoutMethod) || method.equalsIgnoreCase(clientLogoutMethod);
        }
        return false;
    }


    public abstract void checkLoginFailedHandler(HttpServletRequest request, HttpServletResponse response);


    public abstract void logOutSuccessHandler(HttpServletRequest request, HttpServletResponse response);

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
