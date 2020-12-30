package com.taoyuanx.sso.client.impl;


import com.alibaba.fastjson.JSON;
import com.taoyuanx.sso.client.core.Result;
import com.taoyuanx.sso.client.core.SSOClientConfig;
import com.taoyuanx.sso.client.core.SSOServerApi;
import com.taoyuanx.sso.client.dto.SSOUser;
import com.taoyuanx.sso.client.ex.SSOClientException;
import com.taoyuanx.sso.client.utils.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.Map;

@Slf4j
public class SSOClientImpl implements SSOClient {
    private Map<SSOServerApi, String> apiMap;
    private OkHttpClient client;
    private SSOClientConfig clientConfig;

    public SSOClientImpl(SSOClientConfig clientConfig) {
        this.client = clientConfig.getOkHttpClient();
        this.apiMap = clientConfig.getApiMap();
        this.clientConfig = clientConfig;
    }


    @Override
    public boolean loginCheck(String sessionId) {
        String url = apiMap.get(SSOServerApi.LOGIN_CHECK) + "?" + this.clientConfig.getSessionKeyName() + "=" + sessionId;
        try {
            OkHttpUtil.request(client, new Request.Builder()
                    .url(url).get().build(), null);
            return true;
        } catch (SSOClientException e) {
            log.warn("sso check error", e);
            return false;
        }
    }

    @Override
    public SSOUser getSSOUser(String sessionId) {
        String url = apiMap.get(SSOServerApi.GET_SSO_USER) + "?" + this.clientConfig.getSessionKeyName() + "=" + sessionId;
        return OkHttpUtil.request(client, new Request.Builder()
                .url(url).get().build(), SSOUser.class);

    }

    @Override
    public void logout(String sessionId) {
        String url = apiMap.get(SSOServerApi.LOGOUT) + "?" + this.clientConfig.getSessionKeyName() + "=" + sessionId;
        OkHttpUtil.request(client, new Request.Builder()
                .url(url).get().build(), null);

    }

    @Override
    public boolean isSessionIdValid(String sessionId) {
        return false;
    }


}
