package com.taoyuanx.sso.controller;

import com.taoyuanx.sso.config.SSOProperties;
import com.taoyuanx.sso.core.consts.SSOConst;
import com.taoyuanx.sso.core.dto.Result;
import com.taoyuanx.sso.core.dto.ResultBuilder;
import com.taoyuanx.sso.core.exception.SSOException;
import com.taoyuanx.sso.core.exception.SessionIdInvalidException;
import com.taoyuanx.sso.core.session.SessionIdGenerate;
import com.taoyuanx.sso.core.session.SessionManager;
import com.taoyuanx.sso.core.utils.CookieUtil;
import com.taoyuanx.sso.core.utils.RequestUtil;
import com.taoyuanx.sso.dto.LoginForm;
import com.taoyuanx.sso.entity.UserEntity;
import com.taoyuanx.sso.service.UserService;
import com.taoyuanx.sso.util.PasswordUtil;
import com.taoyuanx.sso.vo.LoginUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author dushitaoyuan
 * @desc sso 登录接口
 * @date 2020/12/30
 */
@RequestMapping("/sso")
@ResponseBody
@Slf4j
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


    @GetMapping("login")
    public ModelAndView login(String redirectUrl, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        String sessionId = null;
        try {
            if (ssoProperties.isEnableCookie()) {
                sessionId = RequestUtil.getCookieValue(request, ssoProperties.getSessionKeyName());
            } else {
                sessionId = RequestUtil.getHeaderOrParamValue(request, ssoProperties.getSessionKeyName());
            }
            if (sessionManager.isLogin(sessionId)) {
                modelAndView.setView(new RedirectView(redirectUrl));
                return modelAndView;
            }
        } catch (SessionIdInvalidException e) {
            log.error("sessionId is invalid", e);
            if (ssoProperties.isEnableCookie()) {
                CookieUtil.deleteCookieValue(request, response, ssoProperties.getSessionKeyName(), ssoProperties.getSessionIdCookieDomain(), SSOConst.SSO_COOKIE_PATH);
            }
        }
        modelAndView.addObject("redirectUrl", redirectUrl);
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @PostMapping("login")
    @ResponseBody
    public Result login(@Valid LoginForm loginForm, HttpServletRequest request, HttpServletResponse response) {
        UserEntity dbUser = userService.findByUsername(loginForm.getUsername());
        if (Objects.isNull(dbUser) || !PasswordUtil.passwordEqual(dbUser.getPassword(), loginForm.getPassword())) {
            throw new SSOException("账户密码不匹配");
        }
        if (UserEntity.STATUS_LOCKED.equals(dbUser.getStatus())) {
            throw new SSOException("账户已冻结,请联系管理员处理!");
        }
        String redirectUrl = loginForm.getRedirectUrl();
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
        try {
            UserEntity userEntity = loginUserVo.getUserEntity();
            Long userId = userEntity.getId();
            loginUserVo.setSessionId(sessionIdGenerate.generate(String.valueOf(userId)));
            loginUserVo.setUserId(userId);
            loginUserVo.setUsername(userEntity.getUsername());
            loginUserVo.setRedirectUrl(redirectUrl);
            if (ssoProperties.isEnableCookie()) {
                cookieSuccessHandler(loginUserVo, response);
            } else {
                paramSuccessHandler(loginUserVo, response);
            }
            Map loginResult = new HashMap<>();
            loginResult.put("redirectUrl", loginUserVo.getRedirectUrl());
            return ResultBuilder.success(loginResult);
        } catch (Exception e) {
            log.error("登录失败", e);
            throw new SSOException("登录失败");
        }


    }


    /**
     * 将cookie 写入浏览器,domain为主域名,可在使用子域名的sso-client application间自动传输
     */
    private void cookieSuccessHandler(LoginUserVo loginUserVo, HttpServletResponse response) throws IOException {
        sessionManager.createSession(loginUserVo);
        CookieUtil.addCookie(response, ssoProperties.getSessionKeyName(), loginUserVo.getSessionId(), ssoProperties.getSessionIdCookieDomain(), ssoProperties.getSessionTimeOut() * 60, SSOConst.SSO_COOKIE_PATH);
    }

    /**
     * 将sessionId传递给sso-client application,由 sso-client 自定义存储,并在跳转时由发起方系统传递给另一方系统
     */
    private void paramSuccessHandler(LoginUserVo loginUserVo, HttpServletResponse response) throws IOException {
        String redirectUrlWithSessionId = RequestUtil.addParamToUrl(loginUserVo.getRedirectUrl(), ssoProperties.getSessionKeyName(), loginUserVo.getSessionId());
        loginUserVo.setRedirectUrl(redirectUrlWithSessionId);
        sessionManager.createSession(loginUserVo);
    }




}
