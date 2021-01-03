package com.taoyuanx.sso.client.impl;


import com.taoyuanx.sso.client.core.Result;
import com.taoyuanx.sso.client.core.SSOClientConfig;
import com.taoyuanx.sso.client.core.SSOClientConstant;
import com.taoyuanx.sso.client.core.SSOServerApi;
import com.taoyuanx.sso.client.core.sign.sign.IVerifySign;
import com.taoyuanx.sso.client.dto.SSOUser;
import com.taoyuanx.sso.client.ex.SSOClientException;
import com.taoyuanx.sso.client.ex.SessionIdInvalidClientException;
import com.taoyuanx.sso.client.utils.OkHttpUtil;
import com.taoyuanx.sso.client.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
public class SSOClientImpl implements SSOClient {
    private OkHttpClient client;
    private SSOClientConfig clientConfig;
    private Pattern sessionIdPattern = Pattern.compile(".*\\..*");
    private IVerifySign verifySign;

    private String sessionKeyName;

    private boolean enableCookie;

    public SSOClientImpl(SSOClientConfig clientConfig, IVerifySign verifySign) {
        this.client = clientConfig.getOkHttpClient();
        this.clientConfig = clientConfig;
        this.sessionKeyName = clientConfig.getSessionKeyName();
        this.verifySign = verifySign;
        this.enableCookie = clientConfig.isEnableCookie();
    }


    @Override
    public boolean loginCheck(String sessionId) {
        String url = buildUrlWithSessionId(SSOServerApi.LOGIN_CHECK, sessionId);
        try {
            Result result = OkHttpUtil.request(client, new Request.Builder()
                    .url(url).header("Accept", "application/json").get().build(), Result.class);
            return result.success();
        } catch (SSOClientException e) {
            log.warn("sso check error", e);
            return false;
        }
    }

    @Override
    public SSOUser getSSOUser(String sessionId) {
        String url = buildUrlWithSessionId(SSOServerApi.GET_SSO_USER, sessionId);
        return OkHttpUtil.request(client, new Request.Builder()
                .url(url).header("Accept", "application/json").get().build(), SSOUser.class);

    }

    @Override
    public void logout(String sessionId) {
        String url = buildUrlWithSessionId(SSOServerApi.LOGOUT, sessionId);
        Result result = OkHttpUtil.request(client, new Request.Builder()
                .url(url).header("Accept", "application/json").get().build(), Result.class);
        if (!result.success()) {
            log.warn("logout failed msg:{}", result.getMsg());
        }
    }

    @Override
    public boolean isSessionIdValid(String sessionId) {
        if (Objects.isNull(sessionId) || !sessionIdPattern.matcher(sessionId).matches()) {
            return false;
        }
        String[] split = sessionId.split("\\.");
        byte[] data = Base64.decodeBase64(split[0]);
        byte[] sign = Base64.decodeBase64(split[1]);
        return verifySign.verifySign(data, sign);
    }

    @Override
    public String getSessionId(HttpServletRequest request) {
        Object sessionIdObj = request.getAttribute(SSOClientConstant.SESSION_KEY_NAME);
        if (sessionIdObj != null) {
            return (String) sessionIdObj;
        }
        String sessionId = null;
        if (enableCookie) {
            sessionId = RequestUtil.getCookieValue(request, sessionKeyName);

        } else {
            sessionId = RequestUtil.getHeaderOrParamValue(request, sessionKeyName);
        }
        if (sessionId != null) {
            request.setAttribute(SSOClientConstant.SESSION_KEY_NAME, sessionId);
        }
        return sessionId;


    }

    private String buildUrlWithSessionId(SSOServerApi ssoServerApi, String sessionId) {
        if (Objects.isNull(sessionId) || !isSessionIdValid(sessionId)) {
            throw new SessionIdInvalidClientException("sessionId invalid");
        }
        return clientConfig.getApiMap()
                .get(ssoServerApi) + "?" + sessionKeyName + "=" + sessionId;
    }


}
