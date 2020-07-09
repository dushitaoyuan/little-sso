package com.taoyuanx.littlesso.server.login.handler.impl;

import com.taoyuanx.littlesso.server.anno.LoginHander;
import com.taoyuanx.littlesso.server.commons.AccountConstant;
import com.taoyuanx.littlesso.server.commons.SessionConstants;
import com.taoyuanx.littlesso.server.enums.LoginTypeEnum;
import com.taoyuanx.littlesso.server.exception.ServiceException;
import com.taoyuanx.littlesso.server.log.aop.LoginAuditBuilder;
import com.taoyuanx.littlesso.server.login.handler.ILoginHandler;
import com.taoyuanx.littlesso.server.login.session.SessionManager;
import com.taoyuanx.littlesso.server.login.session.SessionStore;
import com.taoyuanx.littlesso.server.service.LoginService;
import com.taoyuanx.littlesso.server.utils.TicketManager;
import com.taoyuanx.littlesso.server.utils.RequestUtil;
import com.taoyuanx.littlesso.server.utils.SessionUtil;
import com.taoyuanx.littlesso.server.vo.LoginForm;
import com.taoyuanx.littlesso.server.vo.PasswordLoginForm;
import com.taoyuanx.littlesso.server.vo.SSOLoginUserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author lianglei
 * @date 2019/1/7 12:43
 * @desc 口令登录实现
 **/
@LoginHander(support = LoginTypeEnum.PASSWORD_LOGIN)
public class PasswordLoginHandler implements ILoginHandler {
    @Autowired
    LoginService loginService;
    @Autowired
    SessionManager sessionManager;


    @Override
    public SSOLoginUserVo login(LoginForm loginForm) {
        PasswordLoginForm passwordLoginForm = (PasswordLoginForm) loginForm;
        HttpServletRequest request = RequestUtil.getCurrentRequest();
        Object vafyCodeObj = request.getSession().getAttribute(AccountConstant.VAFY_CODE_SESSION_KEY);
        String vafyCode = passwordLoginForm.getVafyCode();
        if (StringUtils.isEmpty(vafyCode) && Objects.nonNull(vafyCodeObj) && !vafyCode.equalsIgnoreCase(vafyCodeObj.toString())) {
            throw new ServiceException("验证码不可为空或验证码不匹配");
        }

        SSOLoginUserVo ssoLoginUserVo = loginService.login(passwordLoginForm.getUsername(), passwordLoginForm.getPassword());
        //登录成功创建会话
        String sessionId = SessionUtil.makeSessionId(String.valueOf(ssoLoginUserVo.getUserId()), String.valueOf(ssoLoginUserVo.getAccountId()));
        ssoLoginUserVo.setSessionId(sessionId);
        String ticket = TicketManager.createTicket(ssoLoginUserVo);
        ssoLoginUserVo.setSessionId(sessionId);
        ssoLoginUserVo.setTicket(ticket);
        SessionStore sessionStore = sessionManager.createSession(sessionId);
        sessionStore.set(AccountConstant.SSO_TICKET_KEY, sessionId);
        sessionStore.set(SessionConstants.USER, ssoLoginUserVo);
        sessionStore.set(SessionConstants.TICKET, ticket);
        sessionStore.set(SessionConstants.REDIRECT_URL, loginForm.getRedirectUrl());
        LoginAuditBuilder.getLoginAudit().setSessionId(sessionId);
        return ssoLoginUserVo;


    }
}
