package com.taoyuanx.sso.controller;

import com.taoyuanx.sso.config.SSOProperties;
import com.taoyuanx.sso.core.consts.SSOConst;
import com.taoyuanx.sso.core.dto.ResultBuilder;
import com.taoyuanx.sso.core.exception.SSOException;
import com.taoyuanx.sso.core.session.SessionIdGenerate;
import com.taoyuanx.sso.core.session.SessionManager;
import com.taoyuanx.sso.core.utils.CookieUtil;
import com.taoyuanx.sso.core.utils.JSONUtil;
import com.taoyuanx.sso.core.utils.RequestUtil;
import com.taoyuanx.sso.core.utils.ResponseUtil;
import com.taoyuanx.sso.dto.LoginForm;
import com.taoyuanx.sso.entity.UserEntity;
import com.taoyuanx.sso.service.UserService;
import com.taoyuanx.sso.util.PasswordUtil;
import com.taoyuanx.sso.vo.LoginUserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author dushitaoyuan
 * @desc sso登录接口
 * @date 2020/12/30
 */
@RequestMapping("/sso/login")
@ResponseBody
@Controller
public class SSOLoginController {
    @Autowired
    UserService userService;
    @Autowired
    SessionManager sessionManager;

    @Autowired
    SessionIdGenerate sessionIdGenerate;
    @Autowired
    SSOProperties ssoProperties;


    @PostMapping
    public void login(@Valid LoginForm loginForm, HttpServletRequest request, HttpServletResponse response) {
        UserEntity dbUser = userService.findByUsername(loginForm.getUsername());
        if (Objects.isNull(dbUser) || !PasswordUtil.passwordEqual(dbUser.getPassword(), loginForm.getPassword())) {
            throw new SSOException("账户密码不匹配");
        }
        if (UserEntity.STATUS_LOCKED.equals(dbUser.getStatus())) {
            throw new SSOException("账户已冻结,请联系管理员处理!");
        }
        String redirectUrl = RequestUtil.getValue(request, SSOConst.SSO_REDIRECT_URL);
        if (StringUtils.isEmpty(redirectUrl) && StringUtils.isEmpty(dbUser.getLoginRedirectUrl())) {
            throw new SSOException("参数 redirectUrl 不匹配!");
        }
        if (StringUtils.isNotEmpty(dbUser.getLoginRedirectUrl()) && StringUtils.isNotEmpty(dbUser.getLoginRedirectUrl()) && !dbUser.getLoginRedirectUrl().equals(redirectUrl)) {
            throw new SSOException("跳转地址不匹配!");
        }
        if (StringUtils.isEmpty(redirectUrl)) {
            redirectUrl = dbUser.getLoginRedirectUrl();
        }
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setUserEntity(dbUser);
        loginUserVo.setRedirectUrl(redirectUrl);
        successLogin(loginUserVo, request, response);

    }

    /**
     * 登录成功后,设置cookie
     */
    private void successLogin(LoginUserVo loginUserVo, HttpServletRequest request, HttpServletResponse response) {
        try {
            String userId = String.valueOf(loginUserVo.getUserEntity().getId());
            String sessionId = sessionIdGenerate.generate(userId);
            loginUserVo.setSessionId(sessionId);
            loginUserVo.setUserId(userId);
            String sessionKeyName = ssoProperties.getSessionKeyName();
            sessionManager.createSession(loginUserVo);
            if (ResponseUtil.isAcceptJson(request)) {
                Map map = new HashMap<>();
                map.put("redirectUrl", loginUserVo.getRedirectUrl());
                map.put(sessionKeyName, sessionId);
                ResponseUtil.responseJson(response, JSONUtil.toJsonString(ResultBuilder.success(map)), 200);
            } else {
                response.sendRedirect(loginUserVo.getRedirectUrl());
            }
            /**
             * header 和 cookie 都设置
             */
            response.addHeader(sessionKeyName, sessionId);
            CookieUtil.addCookie(response, ssoProperties.getSessionKeyName(), sessionId, ssoProperties.getSessionIdCookieDomain(), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
