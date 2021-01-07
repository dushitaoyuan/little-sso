package com.taoyuanx.sso.core.session.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.taoyuanx.sso.core.dto.SSOTokenUser;
import com.taoyuanx.sso.core.dto.SSOUser;
import com.taoyuanx.sso.core.session.TokenSessionManager;
import com.taoyuanx.sso.core.token.SimpleTokenManager;
import com.taoyuanx.sso.core.token.SuperToken;
import com.taoyuanx.sso.core.utils.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author dushitaoyuan
 * @date 2020/12/29
 * 自定义token session 实现
 */
@Slf4j
public class MyTokenSessionManager implements TokenSessionManager {
    private SimpleTokenManager simpleTokenManager;
    private Long sessionTimeOut;

    public MyTokenSessionManager(SimpleTokenManager simpleTokenManager, Long sessionTimeOut) {
        this.simpleTokenManager = simpleTokenManager;
        this.sessionTimeOut = sessionTimeOut;
    }


    @Override
    public void createSession(SSOUser ssoUser) {
        createToken(ssoUser);
    }

    @Override
    public boolean isLogin(String sessionId) {
        return simpleTokenManager.verify(sessionId, TokenSessionManager.TOKEN_TYPE_SESSION);
    }

    @Override
    public SSOTokenUser getSSOUser(String sessionId) {
        SSOToken ssoToken = simpleTokenManager.parseToken(sessionId, SSOToken.class);
        return sessionTokenToSSOUser(sessionId, ssoToken);
    }

    @Override
    public void logout(String sessionId) {
        log.debug("logout nothing to do!");
    }


    private void createToken(SSOUser ssoUser) {
        SSOTokenUser ssoTokenUser = (SSOTokenUser) ssoUser;
        SSOToken ssoToken = new SSOToken();
        Long now = System.currentTimeMillis();
        ssoToken.setEffectTime(now);
        //过期时间宽松五分钟
        int expireWindow = 5;
        ssoToken.setEndTime(now + TimeUnit.MINUTES.toMillis(sessionTimeOut + expireWindow));
        ssoToken.setSessionData(JSONUtil.toJsonString(ssoUser));
        ssoToken.setCreateTime(now);
        ssoToken.setType(TokenSessionManager.TOKEN_TYPE_SESSION);
        String sessionToken = simpleTokenManager.createToken(ssoToken);
        ssoToken.setType(TokenSessionManager.TOKEN_TYPE_REFRESH);
        ssoToken.setEndTime(ssoToken.getEndTime() + TimeUnit.MINUTES.toMillis(sessionTimeOut + expireWindow * 2) / 2);
        String refreshToken = simpleTokenManager.createToken(ssoToken);


        ssoTokenUser.setSessionId(sessionToken);
        ssoTokenUser.setRefreshToken(refreshToken);
        ssoTokenUser.setExpire(sessionTimeOut);
        ssoTokenUser.setSessionId(sessionToken);


    }

    private SSOTokenUser sessionTokenToSSOUser(String sessionId, SSOToken ssoToken) {
        SSOTokenUser ssoTokenUser = JSONUtil.parseObject(ssoToken.getSessionData(), SSOTokenUser.class);
        ssoTokenUser.setSessionId(sessionId);
        ssoTokenUser.setExpire(sessionTimeOut);
        ssoTokenUser.setTokenType(ssoToken.getType());
        return ssoTokenUser;
    }

    @Data
    private static class SSOToken extends SuperToken {
        @JsonProperty("d")
        private String sessionData;
    }

}
