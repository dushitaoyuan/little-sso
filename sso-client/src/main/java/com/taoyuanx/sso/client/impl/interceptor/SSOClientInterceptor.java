package com.taoyuanx.sso.client.impl.interceptor;


import com.taoyuanx.sso.client.core.SSOClientConfig;
import com.taoyuanx.sso.client.core.SSOClientConstant;
import com.taoyuanx.sso.client.core.SSOServer;
import com.taoyuanx.sso.client.ex.SSOClientException;
import com.taoyuanx.sso.client.impl.loadbalance.ILoadbalance;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现负载均衡
 */
@Slf4j
public class SSOClientInterceptor implements Interceptor {

    private SSOClientConfig clientConfig;

    private ILoadbalance loadbalance;

    public SSOClientInterceptor(SSOClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.loadbalance = clientConfig.getLoadbalance();

    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request userRequest = chain.request();
        HttpUrl requestUrl = userRequest.url();
        String stringUrl = requestUrl.toString();
        boolean needBase = Objects.nonNull(stringUrl) && stringUrl.startsWith(SSOClientConstant.SSO_CLIENT_BASE_URL);
        SSOServer choseServer = null;

        if (needBase) {
            /**
             *   添加token,header
             */
            Request.Builder requestBuilder = userRequest.newBuilder();

            /**
             * 替换base url
             */
            if (needBase) {
                choseServer = choseServer();
                requestBuilder.url(buildRealUrl(choseServer, requestUrl));
            }
            userRequest = requestBuilder.build();
            if (needBase) {
                return doProceed(chain, userRequest, choseServer, null);
            }
        }
        return chain.proceed(userRequest);
    }


    private Map<SSOServer, HttpUrl> serverUrlCache = new ConcurrentHashMap<>();

    /**
     * 递归执行,直到所有存活的SSOServer中有一个执行成功
     */
    private Response doProceed(Chain chain, Request userRequest, SSOServer choseServer, List<SSOServer> excludeSSOServer) throws IOException {
        try {
            return chain.proceed(userRequest);
        } catch (ConnectException e) {
            log.warn("file server[{}] connect error", choseServer.getServerUrl());
            /**
             * 连接失败 标记server 不可用
             */
            if (Objects.nonNull(choseServer)) {
                choseServer.alive(false);
            }
            if (Objects.isNull(excludeSSOServer)) {
                excludeSSOServer = new ArrayList<>();
            }
            excludeSSOServer.add(choseServer);
            SSOServer newChoseServer = loopForAlive(excludeSSOServer);
            return doProceed(chain, userRequest.newBuilder().url(buildRealUrl(newChoseServer, userRequest.url())).build(), newChoseServer, excludeSSOServer);
        }
    }

    private HttpUrl buildRealUrl(SSOServer choseServer, HttpUrl oldUrl) {
        HttpUrl choseServerUrl = getSSOServerHttpUrl(choseServer);
        choseServerUrl = oldUrl.newBuilder().scheme(choseServerUrl.scheme()).host(choseServerUrl.host()).port(choseServerUrl.port()).build();
        return choseServerUrl;
    }

    private SSOServer choseServer() {
        SSOServer choseServer = loadbalance.chose(clientConfig.getServerList());
        if (choseServer == null) {
            throw new SSOClientException("no alive SSOServer");
        }
        if (choseServer.isAlive()) {
            return choseServer;
        } else if (clientConfig.getServerList().size() > 1) {
            return loopForAlive(Arrays.asList(choseServer));
        }
        throw new SSOClientException("no alive SSOServer");
    }


    private HttpUrl getSSOServerHttpUrl(SSOServer SSOServer) {
        if (!serverUrlCache.containsKey(SSOServer)) {
            serverUrlCache.put(SSOServer, HttpUrl.parse(SSOServer.getServerUrl()));
        }
        return serverUrlCache.get(SSOServer);
    }

    private SSOServer loopForAlive(List<SSOServer> excludeSSOServerList) {
        Optional<SSOServer> anyAliveSSOServer = clientConfig.getServerList().stream().filter(SSOServer -> {
            for (SSOServer excludeServer : excludeSSOServerList) {
                if (excludeServer.equals(SSOServer)) {
                    return false;
                }
            }
            return SSOServer.isAlive();
        }).findAny();
        if (anyAliveSSOServer.isPresent()) {
            return anyAliveSSOServer.get();
        }
        throw new SSOClientException("no alive SSOServer");

    }


}
