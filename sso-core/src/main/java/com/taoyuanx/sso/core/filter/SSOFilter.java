package com.taoyuanx.sso.core.filter;

import com.taoyuanx.sso.core.bean.SSOBeanProvider;
import com.taoyuanx.sso.core.consts.SSOConst;
import com.taoyuanx.sso.core.dto.ResultBuilder;
import com.taoyuanx.sso.core.exception.SSOException;
import com.taoyuanx.sso.core.session.SessionHelper;
import com.taoyuanx.sso.core.session.SessionIdGenerate;
import com.taoyuanx.sso.core.utils.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private List<String> excludePath;

    private List<String> path;
    private String logoutUrl;
    private String logoutMethod;

    private String loginUrl;

    private SessionIdGenerate sessionIdGenerate;

    private SessionHelper sessionHelper;

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
                sessionIdGenerate.isSessionIdValid(sessionId);
                sessionHelper.logout(sessionId);
                response.sendRedirect(loginUrl);
            } catch (SSOException e) {
                /**
                 * delete cookie
                 */
                CookieUtil.deleteCookieValue(request, response, SSOConst.SSO_SESSION_ID);
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
                String uniqueData = sessionIdGenerate.isSessionIdValid(sessionId);
                if (!sessionHelper.isLogin(sessionId)) {
                    throw new SSOException("sessionId[" + sessionId + "] is invalid");
                }
                request.setAttribute(SSOConst.SSO_SESSION_UNIQUE_DATA, uniqueData);
                request.setAttribute(SSOConst.SSO_SESSION_ID, sessionId);
            } catch (SSOException e) {
                log.error("check login failed", e);
                /**
                 * delete cookie
                 */
                CookieUtil.deleteCookieValue(request, response, SSOConst.SSO_SESSION_ID);
                toLogin(request, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPathFilter(String requestURI) {
        for (int i = 0, len = excludePath.size(); i < len; i++) {
            String exclude = excludePath.get(i);
            if (antPathMatcher.match(exclude, requestURI)) {
                return false;
            }
        }
        for (int i = 0, len = path.size(); i < len; i++) {
            String matchPath = excludePath.get(i);
            if (antPathMatcher.match(matchPath, requestURI)) {
                log.debug("{} match sso filter ", requestURI);
                return true;
            }
        }
        return false;
    }

    private boolean isLogout(String requestURI, String method) {
        if (antPathMatcher.match(logoutUrl, requestURI)) {
            return HelperUtil.isEmpty(logoutMethod) || method.equalsIgnoreCase(logoutMethod);
        }
        return false;
    }


    private String getSessionId(HttpServletRequest request) {
        //fetch order   cookie > header > parameter
        String value = CookieUtil.getCookieValue(request, SSOConst.SSO_SESSION_ID);
        if (HelperUtil.isNotEmpty(value)) {
            return value;
        }
        value = request.getHeader(SSOConst.SSO_SESSION_ID);
        if (HelperUtil.isNotEmpty(value)) {
            return value;
        } else {
            return request.getParameter(SSOConst.SSO_SESSION_ID);
        }
    }

    private void toLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (ResponseUtil.isAcceptJson(request)) {
            ResponseUtil.responseJson(response, JSONUtil.toJsonString(ResultBuilder.failed(SSOConst.SSO_NOT_LOGIN_CODE, "未登录,请先登录")), 200);
        } else {
            response.sendRedirect(loginUrl);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String path = filterConfig.getInitParameter(SSOConst.FILTER_PATH);
        String excludePath = filterConfig.getInitParameter(SSOConst.FILTER_EXCLUDE_PATH);
        String logoutUrl = filterConfig.getInitParameter(SSOConst.LOGOUT_URL);
        String loginUrl = filterConfig.getInitParameter(SSOConst.LOGIN_URL);

        String beanProviderClass = filterConfig.getInitParameter(SSOConst.BEAN_PROVIDER);
        if (HelperUtil.isEmpty(path) ||
                HelperUtil.isEmpty(excludePath)
                || HelperUtil.isEmpty(logoutUrl)
                || HelperUtil.isEmpty(loginUrl) || HelperUtil.isEmpty(beanProviderClass)) {
            throw new SSOException("path or excludePath,or loginUrl,or logoutUrl,or bean_provider not config");
        }
        this.path = Arrays.stream(path.split(SSOConst.CONFIG_SPILT)).filter(HelperUtil::isNotEmpty).collect(Collectors.toList());
        this.excludePath = Arrays.stream(excludePath.split(SSOConst.CONFIG_SPILT)).filter(HelperUtil::isNotEmpty).collect(Collectors.toList());

        this.loginUrl = logoutUrl;
        this.logoutUrl = logoutUrl;
        this.logoutMethod = filterConfig.getInitParameter(SSOConst.LOGOUT_HTTP_METHOD);
        try {
            SSOBeanProvider ssoBeanProvider = (SSOBeanProvider) Class.forName(beanProviderClass).newInstance();
            this.sessionIdGenerate = ssoBeanProvider.getBean(SessionIdGenerate.class);
            this.sessionHelper = ssoBeanProvider.getBean(SessionHelper.class);
        } catch (Exception e) {
            throw new SSOException("init bean_provider error", e);
        }
    }
}
