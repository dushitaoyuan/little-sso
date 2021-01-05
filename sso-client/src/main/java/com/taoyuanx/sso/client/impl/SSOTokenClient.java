package com.taoyuanx.sso.client.impl;


import com.taoyuanx.sso.client.core.SSOClientConfig;
import com.taoyuanx.sso.client.core.SSOClientConstant;
import com.taoyuanx.sso.client.dto.SSOUser;
import com.taoyuanx.sso.client.token.AbstractSSOTokenVerify;
import com.taoyuanx.sso.client.token.SSOTokenResult;
import com.taoyuanx.sso.client.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class SSOTokenClient {


    private String sessionKeyName;

    private AbstractSSOTokenVerify ssoTokenManager;

    public SSOTokenClient(SSOClientConfig clientConfig, AbstractSSOTokenVerify ssoTokenManager) {
        this.sessionKeyName = clientConfig.getSessionKeyName();
        this.ssoTokenManager = ssoTokenManager;
    }


    public boolean verifySessionToken(String sessionId) {
        return ssoTokenManager.verify(sessionId, SSOTokenResult.TOKEN_TYPE_SESSION);
    }

    public SSOUser getSSOUser(String sessionId) {
        SSOUser ssoTokenUser = ssoTokenManager.parseToken(sessionId);
        return ssoTokenUser;
    }

    public void logout(String sessionId) {
        log.debug("nothing to do");
    }


    public String getSessionId(HttpServletRequest request) {
        Object sessionIdObj = request.getAttribute(SSOClientConstant.SESSION_KEY_NAME);
        if (sessionIdObj != null) {
            return (String) sessionIdObj;
        }
        String sessionId = RequestUtil.getHeaderOrParamValue(request, sessionKeyName);
        if (sessionId != null) {
            request.setAttribute(SSOClientConstant.SESSION_KEY_NAME, sessionId);
        }
        return sessionId;

    }


}
