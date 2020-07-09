package com.taoyuanx.littlesso.server.web.controller;

import com.alibaba.fastjson.JSON;
import com.ncs.pm.commons.api.ResultBuilder;
import com.ncs.pm.commons.utils.ResponseUtil;
import com.taoyuanx.littlesso.server.commons.AccountConstant;
import com.taoyuanx.littlesso.server.config.SsoServerProperties;
import com.taoyuanx.littlesso.server.login.session.SessionManager;
import com.taoyuanx.littlesso.server.utils.CookieUtil;
import com.taoyuanx.littlesso.server.utils.RequestUtil;
import com.taoyuanx.littlesso.server.utils.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lianglei
 * @date 2019/1/6 18:24
 * @desc 退出控制器
 **/
@RequestMapping("logout")
@Controller
@Slf4j
public class LogoutController {
    @Autowired
    SessionManager sessionManager;
    @Autowired
    SsoServerProperties ssoServerProperties;

    @RequestMapping({""})
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sessionId = RequestUtil.getValue(request, AccountConstant.SSO_GLOBAL_SESSION_ID_KEY);
        /**
         *  全局会话注销逻辑:
         *  1.如果有全局会话,则不执行session注销,当所有局部会话都注销时再注销全局会话
         *  2.如果全局会话与局部会话一致,则局部会话注销,全局会话也注销
         *  全局会话注销代码:sessionManager.expireSession(sessionId);
         *  ,client端注销自身局部会话,需通知sso端, 全局会话注销时,通知client端,注销局部会话
         *  目前注销逻辑为:2
         */
        if (StringUtils.isEmpty(sessionId) || !SessionUtil.isValidSessionId(sessionId)) {
            log.warn("注销请求未携带全局sessionId,或全局sessionId 无效");
        } else {
            //sessionManager.expireSession(sessionId);
        }
        /**
         * 删除cookie 兼容旧
         */
        String cookieDomain = RequestUtil.getCookieDomain(request, ssoServerProperties.getCookieDomain());
        CookieUtil.deleteCookieValue(response, cookieDomain, AccountConstant.SSO_GLOBAL_SESSION_ID_KEY);
        CookieUtil.deleteCookieValue(response, cookieDomain, AccountConstant.SSO_TICKET_KEY);
        if (ResponseUtil.isJson(request)) {
            /**
             * 非界面注销
             */
            ResponseUtil.responseJson(response, JSON.toJSONString(ResultBuilder.success("logout success!")), HttpStatus.OK.value());
        } else {

            /**
             * 界面注销 跳转回原页面,并携带所有参数
             */
            String redirectUrl = RequestUtil.getRedirectUrl(request)+RequestUtil.getQueryString(request,AccountConstant.REDIRECT_URL_PARAM_KEY,AccountConstant.REDIRECT_URL_PARAM_KEY_OLD);
            response.sendRedirect(redirectUrl);
        }

    }


}
