package com.taoyuanx.sso.controller;

import com.taoyuanx.sso.config.SSOProperties;
import com.taoyuanx.sso.core.dto.Result;
import com.taoyuanx.sso.core.dto.ResultBuilder;
import com.taoyuanx.sso.core.exception.SessionIdInvalidException;
import com.taoyuanx.sso.core.session.SessionIdGenerate;
import com.taoyuanx.sso.core.session.SessionManager;
import com.taoyuanx.sso.core.utils.RequestUtil;
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
 * @desc sso api
 * @date 2020/12/30
 */
@RequestMapping("/sso")
@ResponseBody
@Controller

public class SSOApiController {
    @Autowired
    SessionManager sessionManager;
    @Autowired
    SessionIdGenerate sessionIdGenerate;
    @Autowired
    SSOProperties ssoProperties;

    @GetMapping("loginCheck")
    public Result loginCheck(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = RequestUtil.getValue(request, ssoProperties.getSessionKeyName());
        if (Objects.isNull(sessionId)) {
            throw new SessionIdInvalidException();
        }
        if (!sessionManager.isLogin(sessionId)) {
            return ResultBuilder.failed("未登录");
        }
        return ResultBuilder.success();
    }

    @GetMapping("info")
    public Result getSSOUser(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = RequestUtil.getValue(request, ssoProperties.getSessionKeyName());
        if (Objects.isNull(sessionId)) {
            throw new SessionIdInvalidException();
        }
        return ResultBuilder.success(sessionManager.getSSOUser(sessionId));
    }

    @GetMapping
    public void hello() {

    }
}
