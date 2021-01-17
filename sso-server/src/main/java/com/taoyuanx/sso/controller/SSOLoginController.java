package com.taoyuanx.sso.controller;

import com.taoyuanx.sso.config.SSOProperties;
import com.taoyuanx.sso.core.consts.SSOConst;
import com.taoyuanx.sso.core.dto.Result;
import com.taoyuanx.sso.core.dto.ResultBuilder;
import com.taoyuanx.sso.core.dto.SSOTokenUser;
import com.taoyuanx.sso.core.exception.SSOException;
import com.taoyuanx.sso.core.exception.SSOTokenException;
import com.taoyuanx.sso.core.exception.SessionIdInvalidException;
import com.taoyuanx.sso.core.session.SessionManager;
import com.taoyuanx.sso.core.session.TokenSessionManager;
import com.taoyuanx.sso.core.utils.CookieUtil;
import com.taoyuanx.sso.core.utils.RequestUtil;
import com.taoyuanx.sso.core.utils.UrlUtil;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
        } catch (SSOTokenException e) {
            log.error("token  is error", e.getMessage());
        }
        modelAndView.addObject(SSOConst.SSO_REDIRECT_URL, redirectUrl);
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

        if (ssoProperties.getSessionMode().equals(SSOConst.SESSION_MODE_CLIENT)) {
            SSOTokenUser ssoTokenUser = new SSOTokenUser();
            ssoTokenUser.setUserId(dbUser.getId());
            ssoTokenUser.setUsername(dbUser.getUsername());
            /**
             * 分布式session example:jwt
             */
            return tokenSessionSuccessHandler(ssoTokenUser, redirectUrl, response);
        } else {
            LoginUserVo loginUserVo = new LoginUserVo();
            loginUserVo.setUserEntity(dbUser);
            loginUserVo.setUserId(dbUser.getId());
            loginUserVo.setUsername(dbUser.getUsername());
            loginUserVo.setRedirectUrl(redirectUrl);
            /**
             * 集中式session处理 example:redis
             */
            return centralizedSessionSuccessHandler(loginUserVo, response);
        }
    }

    private Result centralizedSessionSuccessHandler(LoginUserVo loginUserVo, HttpServletResponse response) {
        try {
            if (ssoProperties.isEnableCookie()) {
                cookieCentralizedSessionSuccessHandler(loginUserVo, response);
            } else {
                paramCentralizedSessionSuccessHandler(loginUserVo);
            }
            Map loginResult = new HashMap<>();
            loginResult.put(SSOConst.SSO_REDIRECT_URL, loginUserVo.getRedirectUrl());
            return ResultBuilder.success(loginResult);
        } catch (Exception e) {
            log.error("登录失败", e);
            throw new SSOException("登录失败");
        }

    }


    /**
     * 将cookie 写入浏览器,domain为主域名,可在使用子域名的sso-client application间自动传输
     */
    private void cookieCentralizedSessionSuccessHandler(LoginUserVo loginUserVo, HttpServletResponse response) throws IOException {
        sessionManager.createSession(loginUserVo);
        Map<String, String> urlParamMap = new HashMap<>();
        urlParamMap.put(ssoProperties.getSessionKeyName(), loginUserVo.getSessionId());
        String redirectUrlWithSessionId = UrlUtil.addParamAndSign(loginUserVo.getRedirectUrl(),
                ssoProperties.getRedirectUrlSignKey(), 15, TimeUnit.MINUTES,
                urlParamMap);
        loginUserVo.setRedirectUrl(redirectUrlWithSessionId);
        CookieUtil.addCookie(response, ssoProperties.getSessionKeyName(), loginUserVo.getSessionId(), ssoProperties.getSessionIdCookieDomain(), ssoProperties.getSessionTimeOut() * 60, SSOConst.SSO_COOKIE_PATH);
    }

    /**
     * 将sessionId传递给sso-client application,由 sso-client 自定义存储,并在跳转时由发起方系统传递给另一方系统
     */
    private void paramCentralizedSessionSuccessHandler(LoginUserVo loginUserVo) throws IOException {
        sessionManager.createSession(loginUserVo);
        Map<String, String> urlParamMap = new HashMap<>();
        urlParamMap.put(ssoProperties.getSessionKeyName(), loginUserVo.getSessionId());
        String redirectUrlWithSessionId = UrlUtil.addParamAndSign(loginUserVo.getRedirectUrl(),
                ssoProperties.getRedirectUrlSignKey(), 15, TimeUnit.MINUTES,
                urlParamMap);
        loginUserVo.setRedirectUrl(redirectUrlWithSessionId);
    }


    private Result tokenSessionSuccessHandler(SSOTokenUser ssoTokenUser, String redirectUrl, HttpServletResponse response) {
        try {
            /**
             *  登录成功,将sessionToken(sessionId)和refreshToken都传递给接入的系统
             *  服务端不存储session,由接入系统存储
             *  token 过期前调 用refresh接口获取新的token
             */
            sessionManager.createSession(ssoTokenUser);
            Map<String, String> urlParamMap = new HashMap<>();
            urlParamMap.put(SSOConst.SSO_SESSION_TOKEN, ssoTokenUser.getSessionId());
            urlParamMap.put(SSOConst.SSO_REFRESH_TOKEN, ssoTokenUser.getRefreshToken());
            urlParamMap.put(SSOConst.SSO_TOKEN_EXPIRE, String.valueOf(ssoTokenUser.getExpire()));
            redirectUrl = UrlUtil.addParamAndSign(redirectUrl,
                    ssoProperties.getRedirectUrlSignKey(), 15, TimeUnit.MINUTES,
                    urlParamMap);
            Map loginResult = new HashMap<>();
            loginResult.put(SSOConst.SSO_REDIRECT_URL, redirectUrl);
            return ResultBuilder.success(loginResult);
        } catch (Exception e) {
            log.error("登录失败", e);
            throw new SSOException("登录失败");
        }
    }

    /**
     * 续期处理
     */
    @PostMapping("/token/refresh")
    @ResponseBody
    public Result refresh(HttpServletRequest request) {
        try {
            if (ssoProperties.getSessionMode().equals(SSOConst.SESSION_MODE_CLIENT)) {
                String refreshToken = request.getParameter(SSOConst.SSO_REFRESH_TOKEN);
                SSOTokenUser ssoUser = (SSOTokenUser) sessionManager.getSSOUser(refreshToken);
                if (!ssoUser.getTokenType().equals(TokenSessionManager.TOKEN_TYPE_REFRESH)) {
                    throw new SSOException("tokenType 不匹配");
                }
                SSOTokenUser ssoTokenUser = new SSOTokenUser();
                ssoTokenUser.setUserId(ssoUser.getUserId());
                ssoTokenUser.setUsername(ssoUser.getUsername());
                sessionManager.createSession(ssoTokenUser);
                Map refreshResult = new HashMap<>();
                refreshResult.put(SSOConst.SSO_SESSION_TOKEN, ssoTokenUser.getSessionId());
                refreshResult.put(SSOConst.SSO_REFRESH_TOKEN, ssoTokenUser.getRefreshToken());
                refreshResult.put(SSOConst.SSO_TOKEN_EXPIRE, ssoTokenUser.getExpire());
                return ResultBuilder.success(refreshResult);
            }
            throw new SSOTokenException("接口未启用");
        } catch (SSOException e) {
            throw e;
        } catch (Exception e) {
            log.error("refresh失败", e);
            throw new SSOException("续期失败");
        }
    }

}
