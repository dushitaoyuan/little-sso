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
     * 判断file server是否存活
     */
    public static boolean checkServerAlive(SSOServer server, SSOClientConfig clientConfig) {
        if (server.isAlive()) {
            return true;
        }
        synchronized (server) {
            if (!server.isAlive()) {
                try {
                    OkHttpUtil.request(clientConfig.getOkHttpClient(), new Request.Builder()
                            .url(server.getServerUrl() + SSOServerApi.HELLO.path).get().build(), null);
                    server.alive(true);
                    return true;
                } catch (Exception e) {
                    server.alive(false);
                    return false;
                }
            }
        }
        return true;
    }

    public static void heartBeatCheck(SSOClientConfig clientConfig) {
        if (clientConfig.getServerList() == null) {
            return;
        }
        clientConfig.getServerList().stream().filter(server -> {
            return !server.isAlive();
        }).forEach(server -> {
            try {
                OkHttpUtil.request(clientConfig.getOkHttpClient(), new Request.Builder()
                        .url(server.getServerUrl() + SSOServerApi.HELLO.path).get().build(), null);
                server.alive(true);
            } catch (Exception e) {
                log.warn("server ->{}, heart error", server.getServerUrl());
            }
        });

    }
}
