package com.taoyuanx.sso.controller;

import com.taoyuanx.sso.config.SSOProperties;
import com.taoyuanx.sso.core.dto.Result;
import com.taoyuanx.sso.core.dto.ResultBuilder;
import com.taoyuanx.sso.core.session.SessionHelper;
import com.taoyuanx.sso.core.session.SessionIdGenerate;
import com.taoyuanx.sso.core.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    SessionHelper sessionHelper;

    @Autowired
    SessionIdGenerate sessionIdGenerate;
    @Autowired
    SSOProperties ssoProperties;

    @GetMapping("loginCheck")
    public void loginCheck(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = RequestUtil.getValue(request, ssoProperties.getSessionKeyName());
        if (!sessionHelper.isLogin(sessionId)) {
            response.setStatus(500);
        }
    }

    @GetMapping("info")
    public Result getSSOUser(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = RequestUtil.getValue(request, ssoProperties.getSessionKeyName());
        return ResultBuilder.success(sessionHelper.getSSOUser(sessionId));
    }

    @GetMapping
    public void hello() {

    }
}
