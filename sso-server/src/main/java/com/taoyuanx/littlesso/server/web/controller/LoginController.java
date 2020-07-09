package com.taoyuanx.littlesso.server.web.controller;

import com.ncs.pm.commons.api.Result;
import com.ncs.pm.commons.api.ResultBuilder;
import com.taoyuanx.littlesso.server.commons.AccountConstant;
import com.taoyuanx.littlesso.server.config.SsoServerProperties;
import com.taoyuanx.littlesso.server.exception.ServiceException;
import com.taoyuanx.littlesso.server.login.handler.LoginHandlerChoser;
import com.taoyuanx.littlesso.server.utils.CookieUtil;
import com.taoyuanx.littlesso.server.utils.RequestUtil;
import com.taoyuanx.littlesso.server.vo.LoginForm;
import com.taoyuanx.littlesso.server.vo.PasswordLoginForm;
import com.taoyuanx.littlesso.server.vo.SSOLoginUserVo;
import com.taoyuanx.littlesso.server.vo.UKeyLoginForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lianglei
 * @date 2019/1/6 18:24
 * @desc 登录控制器
 **/
@RequestMapping("login")
@Controller
public class LoginController {
    @Autowired
    LoginHandlerChoser loginHandlerChoser;
    @Autowired
    SsoServerProperties ssoServerProperties;

    @PostMapping("password")
    @ResponseBody
    public Result passwordLogin(@Valid PasswordLoginForm loginForm, HttpServletRequest request, HttpServletResponse response) {
        preCheck(loginForm, request);
        SSOLoginUserVo userVo = loginHandlerChoser.chose(loginForm.getLoginType()).login(loginForm);
        //登录成功
        return success(userVo, request, response);
    }

    @PostMapping("ukey")
    public Result ukeyLogin(@Valid UKeyLoginForm loginForm, HttpServletRequest request, HttpServletResponse response) {
        preCheck(loginForm, request);
        SSOLoginUserVo userVo = loginHandlerChoser.chose(loginForm.getLoginType()).login(loginForm);
        return success(userVo, request, response);
    }

    private void preCheck(LoginForm loginForm, HttpServletRequest request) {
        String random = loginForm.getRandom();
        Object randomObj = request.getSession().getAttribute(AccountConstant.LOGIN_RANDOM);
        //随机数不匹配,非法请求
        if (StringUtils.isEmpty(random) || !random.equals(randomObj)) {
            throw new ServiceException("请求非法");
        }


    }

    private Result success(SSOLoginUserVo loginUserVo, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        result.put(AccountConstant.SSO_GLOBAL_SESSION_ID_KEY, loginUserVo.getSessionId());
        result.put(AccountConstant.SSO_TICKET_KEY, loginUserVo.getTicket());

        // cookie 无法跨域传输
        int sessionTime = ssoServerProperties.getTicketValidTime().intValue() / 1000;
        String cookieDomain = RequestUtil.getCookieDomain(request, ssoServerProperties.getCookieDomain());
        CookieUtil.addCookie(response, AccountConstant.SSO_GLOBAL_SESSION_ID_KEY, loginUserVo.getSessionId(), cookieDomain, sessionTime);
        CookieUtil.addCookie(response, AccountConstant.SSO_TICKET_KEY, loginUserVo.getTicket(), cookieDomain, sessionTime);

        return ResultBuilder.success(result);
    }


}
