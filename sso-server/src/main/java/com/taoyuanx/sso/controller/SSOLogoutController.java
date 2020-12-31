package com.taoyuanx.sso.controller;

import com.taoyuanx.sso.config.SSOProperties;
import com.taoyuanx.sso.core.dto.ResultBuilder;
import com.taoyuanx.sso.core.exception.SessionIdInvalidException;
import com.taoyuanx.sso.core.session.SessionIdGenerate;
import com.taoyuanx.sso.core.session.SessionManager;
import com.taoyuanx.sso.core.utils.CookieUtil;
import com.taoyuanx.sso.core.utils.JSONUtil;
import com.taoyuanx.sso.core.utils.RequestUtil;
import com.taoyuanx.sso.core.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author dushitaoyuan
 * @desc sso退出接口
 * @date 2020/12/30
 */
@RequestMapping("/sso/logout")
@ResponseBody
@Slf4j
@Controller

public class SSOLogoutController {
    @Autowired
    SessionManager sessionManager;

    @Autowired
    SessionIdGenerate sessionIdGenerate;
    @Autowired
    SSOProperties ssoProperties;

    @GetMapping
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = RequestUtil.getValue(request, ssoProperties.getSessionKeyName());
        if (Objects.isNull(sessionId)) {

            throw new SessionIdInvalidException();
        }
        sessionIdGenerate.isSessionIdValid(sessionId);
        sessionManager.logout(sessionId);
        handleResponse(request, response);

    }

    private void handleResponse(HttpServletRequest request, HttpServletResponse response) {
        try {
            CookieUtil.deleteCookieValue(request, response, ssoProperties.getSessionKeyName());
            if (ResponseUtil.isAcceptJson(request)) {
                ResponseUtil.responseJson(response, JSONUtil.toJsonString(ResultBuilder.success()), 200);
            } else {
                response.sendRedirect(ssoProperties.getLoginUrl());
            }
        } catch (Exception e) {
            log.error("logout error", e);
        }
    }
}
