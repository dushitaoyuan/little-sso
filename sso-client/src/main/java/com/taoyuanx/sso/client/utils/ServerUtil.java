package com.taoyuanx.sso.client.utils;

import com.taoyuanx.sso.client.core.SSOClientConfig;
import com.taoyuanx.sso.client.core.SSOServer;
import com.taoyuanx.sso.client.core.SSOServerApi;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;

/**
 * @author dushitaoyuan
 * @date 2020/4/515:22
 */
@Slf4j
public class ServerUtil {

    /**
     * 只心跳notalive的server,当server上线时自动加入到负载列表
     */
    public static void heartBeatCheck(SSOClientConfig clientConfig) {
        if (clientConfig.getServerList() == null) {
            return;
        }
        clientConfig.getServerList().stream().filter(server -> {
            return !server.isAlive();
        }).forEach(server -> {
            doCheck(server, clientConfig);
        });

    }

    private static boolean doCheck(SSOServer server, SSOClientConfig clientConfig) {
        try {
            String url = server.getServerUrl() + SSOServerApi.HELLO.path;
            OkHttpUtil.request(clientConfig.getOkHttpClient(), new Request.Builder()
                    .url(url).get().build(), null);
            server.alive(true);
            return true;
        } catch (Exception e) {
            log.warn("server ->{}, heart error", server.getServerUrl());
            server.alive(false);
            return false;
        }
    }

}
