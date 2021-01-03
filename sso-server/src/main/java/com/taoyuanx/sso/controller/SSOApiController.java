package com.taoyuanx.sso.controller;

import com.taoyuanx.sso.config.SSOProperties;
import com.taoyuanx.sso.core.consts.SSOConst;
import com.taoyuanx.sso.core.dto.Result;
import com.taoyuanx.sso.core.dto.ResultBuilder;
import com.taoyuanx.sso.core.exception.SessionIdInvalidException;
import com.taoyuanx.sso.core.session.SessionIdGenerate;
import com.taoyuanx.sso.core.session.SessionManager;
import com.taoyuanx.sso.core.utils.CookieUtil;
import com.taoyuanx.sso.core.utils.RequestUtil;
import com.taoyuanx.sso.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author dushitaoyuan
 * @date 2021/1/323:56
 */
@RequestMapping("/sso")
@ResponseBody
@Controller
@Slf4j
public class SSOApiController {
    @Autowired
    SessionManager sessionManager;
    @Autowired
    SessionIdGenerate sessionIdGenerate;
    @Autowired
    SSOProperties ssoProperties;

    @GetMapping("logout")
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = RequestUtil.getCookieValue(request, ssoProperties.getSessionKeyName());
        try {
            sessionManager.logout(sessionId);
            CookieUtil.deleteCookieValue(request, response, ssoProperties.getSessionKeyName(), ssoProperties.getSessionIdCookieDomain(), SSOConst.SSO_COOKIE_PATH);
            return ResultBuilder.success();
        } catch (SessionIdInvalidException e) {
            handleSessionIdInvalid(request, response, e);
            return ResultBuilder.failed(SSOConst.LOGIN_CHECK_FAILED_CODE, e.getMessage());
        }

    }

    @GetMapping("loginCheck")
    public Result loginCheck(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = RequestUtil.getCookieValue(request, ssoProperties.getSessionKeyName());
        try {
            if (!sessionManager.isLogin(sessionId)) {
                CookieUtil.deleteCookieValue(request, response, ssoProperties.getSessionKeyName(), ssoProperties.getSessionIdCookieDomain(), SSOConst.SSO_COOKIE_PATH);
                return ResultBuilder.failed("未登录");
            }
            return ResultBuilder.success();
        } catch (SessionIdInvalidException e) {
            handleSessionIdInvalid(request, response, e);
            return ResultBuilder.failed(SSOConst.LOGIN_CHECK_FAILED_CODE, e.getMessage());
        }

    }

    @GetMapping("info")
    public Result getSSOUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            String sessionId = RequestUtil.getCookieValue(request, ssoProperties.getSessionKeyName());
            if (Objects.isNull(sessionId)) {
                throw new SessionIdInvalidException();
            }
            return ResultBuilder.success(sessionManager.getSSOUser(sessionId));
        } catch (SessionIdInvalidException e) {
            handleSessionIdInvalid(request, response, e);
            return ResultBuilder.failed(SSOConst.LOGIN_CHECK_FAILED_CODE, e.getMessage());
        }

    }

    private void handleSessionIdInvalid(HttpServletRequest request, HttpServletResponse response, SessionIdInvalidException e) {
        log.error("sessionId is invalid", e);
        if (ssoProperties.isEnableCookie()) {
            CookieUtil.deleteCookieValue(request, response, ssoProperties.getSessionKeyName(), ssoProperties.getSessionIdCookieDomain(), SSOConst.SSO_COOKIE_PATH);
        }
    }
}
