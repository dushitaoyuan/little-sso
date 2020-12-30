package com.taoyuanx.sso.controller;

import com.taoyuanx.sso.config.SSOProperties;
import com.taoyuanx.sso.core.consts.SSOConst;
import com.taoyuanx.sso.core.dto.Result;
import com.taoyuanx.sso.core.dto.ResultBuilder;
import com.taoyuanx.sso.core.exception.SSOException;
import com.taoyuanx.sso.core.session.SessionHelper;
import com.taoyuanx.sso.core.session.SessionIdGenerate;
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
    SessionHelper sessionHelper;

    @Autowired
    SessionIdGenerate sessionIdGenerate;
    @Autowired
    SSOProperties ssoProperties;

    @GetMapping
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = RequestUtil.getValue(request, ssoProperties.getSessionKeyName());
        sessionIdGenerate.isSessionIdValid(sessionId);
        sessionHelper.logout(sessionId);
        handleResponse(request, response);

    }

    private void handleResponse(HttpServletRequest request, HttpServletResponse response) {
        try {
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
