package com.taoyuanx.sso.controller;

import cn.hutool.core.util.ArrayUtil;
import com.taoyuanx.sso.config.SSOProperties;
import com.taoyuanx.sso.core.consts.SSOConst;
import com.taoyuanx.sso.core.dto.Result;
import com.taoyuanx.sso.core.dto.ResultBuilder;
import com.taoyuanx.sso.core.exception.SSOException;
import com.taoyuanx.sso.core.exception.SessionIdInvalidException;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author dushitaoyuan
 * @desc sso登录接口
 * @date 2020/12/30
 */
@RequestMapping("/sso")
@ResponseBody
@Controller
@Slf4j
public class SSOCookieController {
    @Autowired
    UserService userService;
    @Autowired
    SessionManager sessionManager;

    @Autowired
    SessionIdGenerate sessionIdGenerate;
    @Autowired
    SSOProperties ssoProperties;


    @GetMapping("login")
    public ModelAndView login(String redirectUrl, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        String sessionId = RequestUtil.getCookieValue(request, ssoProperties.getSessionKeyName());
        try {
            if (sessionManager.isLogin(sessionId)) {
                modelAndView.setView(new RedirectView(redirectUrl));
                return modelAndView;
            }
        } catch (SessionIdInvalidException e) {
            log.error("sessionId is invalid", e);
            CookieUtil.deleteCookieValue(request, response, ssoProperties.getSessionKeyName(), ssoProperties.getSessionIdCookieDomain(), SSOConst.SSO_COOKIE_PATH);
        }
        modelAndView.addObject("redirectUrl", redirectUrl);
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @PostMapping("login")
    public void login(@Valid LoginForm loginForm, HttpServletRequest request, HttpServletResponse response) {
        UserEntity dbUser = userService.findByUsername(loginForm.getUsername());
        if (Objects.isNull(dbUser) || !PasswordUtil.passwordEqual(dbUser.getPassword(), loginForm.getPassword())) {
            throw new SSOException("账户密码不匹配");
        }
        if (UserEntity.STATUS_LOCKED.equals(dbUser.getStatus())) {
            throw new SSOException("账户已冻结,请联系管理员处理!");
        }
        String redirectUrl = RequestUtil.getCookieValue(request, SSOConst.SSO_REDIRECT_URL);
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
            UserEntity userEntity = loginUserVo.getUserEntity();
            Long userId = userEntity.getId();
            String sessionId = sessionIdGenerate.generate(String.valueOf(userId));
            loginUserVo.setSessionId(sessionId);
            loginUserVo.setUserId(userId);
            loginUserVo.setUsername(userEntity.getUsername());
            String sessionKeyName = ssoProperties.getSessionKeyName();
            sessionManager.createSession(loginUserVo);
            /**
             * cookie 都设置
             */
            CookieUtil.addCookie(response, sessionKeyName, sessionId, ssoProperties.getSessionIdCookieDomain(), ssoProperties.getSessionTimeOut() * 60);
            response.sendRedirect(loginUserVo.getRedirectUrl());
        } catch (Exception e) {
            log.error("登录失败", e);
            throw new SSOException("登录失败");
        }
    }


}
