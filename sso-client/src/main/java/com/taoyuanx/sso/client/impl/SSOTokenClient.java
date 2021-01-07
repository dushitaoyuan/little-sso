package com.taoyuanx.sso.client.impl;


import com.taoyuanx.sso.client.core.SSOClientConfig;
import com.taoyuanx.sso.client.core.SSOClientConstant;
import com.taoyuanx.sso.client.core.SSOServerApi;
import com.taoyuanx.sso.client.dto.SSOUser;
import com.taoyuanx.sso.client.token.AbstractSSOTokenVerify;
import com.taoyuanx.sso.client.token.SSOTokenResult;
import com.taoyuanx.sso.client.utils.OkHttpUtil;
import com.taoyuanx.sso.client.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.Request;

import javax.servlet.http.HttpServletRequest;

/**
 * 非标准client实现
 */
@Slf4j
public class SSOTokenClient {


    private String sessionKeyName;

    private AbstractSSOTokenVerify ssoTokenManager;
    private SSOClientConfig clientConfig;

    public SSOTokenClient(SSOClientConfig clientConfig, AbstractSSOTokenVerify ssoTokenManager) {
        this.sessionKeyName = clientConfig.getSessionKeyName();
        this.clientConfig = clientConfig;
        this.ssoTokenManager = ssoTokenManager;
    }


    public boolean verifySessionToken(String sessionToken) {
        return ssoTokenManager.verify(sessionToken, SSOTokenResult.TOKEN_TYPE_SESSION);
    }

    public SSOUser getSSOUser(String sessionToken) {
        SSOUser ssoTokenUser = ssoTokenManager.parseToken(sessionToken);
        return ssoTokenUser;
    }

    /**
     * bussiness call this manually
     */
    public String getSSOTokenUserDetail(String sessionToken) {
        ssoTokenManager.verify(sessionToken, SSOTokenResult.TOKEN_TYPE_SESSION);
        String refreshUrl = clientConfig.getApiMap()
                .get(SSOServerApi.TOKEN_USER_DETAIL);
        return OkHttpUtil.request(clientConfig.getOkHttpClient(), new Request.Builder()
                .url(refreshUrl).header("Accept", "application/json")
                .header(SSOClientConstant.SSO_SESSION_TOKEN, sessionToken)
                .get()
                .build(), String.class);
    }

    public SSOTokenResult refreshToken(String refreshToken) {
        ssoTokenManager.verify(refreshToken, SSOTokenResult.TOKEN_TYPE_REFRESH);
        String refreshUrl = clientConfig.getApiMap()
                .get(SSOServerApi.TOKEN_REFRESH);
        FormBody formBody = new FormBody.Builder()
                .add(SSOClientConstant.SSO_REFRESH_TOKEN, refreshToken)
                .build();
        return OkHttpUtil.request(clientConfig.getOkHttpClient(), new Request.Builder()
                .url(refreshUrl).header("Accept", "application/json")
                .post(formBody)
                .build(), SSOTokenResult.class);

    }

    public void logout(String sessionToken) {
        log.debug("nothing to do");
    }


    public String getSessionToken(HttpServletRequest request) {
        Object sessionIdObj = request.getAttribute(SSOClientConstant.SSO_SESSION_TOKEN);
        if (sessionIdObj != null) {
            return (String) sessionIdObj;
        }
        String sessionId = RequestUtil.getHeaderOrParamValue(request, SSOClientConstant.SSO_SESSION_TOKEN);
        if (sessionId != null) {
            request.setAttribute(SSOClientConstant.SSO_SESSION_TOKEN, sessionId);
        }
        return sessionId;

    }


}
