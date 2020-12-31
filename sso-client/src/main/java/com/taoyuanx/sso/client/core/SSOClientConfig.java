package com.taoyuanx.sso.client.core;


import com.sun.deploy.config.ClientConfig;
import com.taoyuanx.sso.client.ex.SSOClientException;
import com.taoyuanx.sso.client.impl.SSOClient;
import com.taoyuanx.sso.client.impl.SSOClientImpl;
import com.taoyuanx.sso.client.impl.interceptor.SSOClientInterceptor;
import com.taoyuanx.sso.client.impl.loadbalance.ILoadbalance;
import com.taoyuanx.sso.client.impl.loadbalance.LoadbalanceEnum;
import com.taoyuanx.sso.client.utils.ServerUtil;
import com.taoyuanx.sso.client.utils.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Getter
@Setter
@Slf4j
public class SSOClientConfig {
    public static final String DEFAULT_CONFIG = "sso-client.properties";
    public static final String CONFIG_PREFIX = "sso.client.";
    /**
     * 服务地址
     */
    private List<SSOServer> serverList;
    /**
     * 连接超时时间 默认 5 秒
     */
    private Integer connectTimeout;
    /**
     * 连接数 默认 100
     */
    private Integer maxIdleConnections;
    /**
     * 连接保持时间 默认 15秒
     */
    private Integer keepAliveDuration;

    /**
     * 心跳监测时间间隔 默认5秒
     */
    private Integer heartIdleTime;
    /**
     * 负载策略 支持Random(随机),Round(轮询)
     */
    private ILoadbalance loadbalance;
    /**
     * client Holder
     */
    private OkHttpClient okHttpClient;
    /**
     * client api
     */
    private Map<SSOServerApi, String> apiMap;
    private String sessionKeyName;

    /**
     * sso 过滤器排除地址
     */
    private List<String> filterExcludePath = Collections.EMPTY_LIST;
    /**
     * 拦截地址
     */
    private List<String> filterIncludePath;
    /**
     * client退出拦截地址
     */
    private String clientLogoutPath;
    /**
     * client退出http方法
     */
    private String clientLogoutMethod;
    /**
     * client 登录拦截地址
     */
    private String clientLoginPath;

    /**
     * sso 服务端登陆Url地址
     */
    private String ssoLoginUrl;
    /**
     * 登录成功后跳转地址
     */
    private String redirectUrl;
    /**
     * session 校验
     */
    private String sessionIdSignHmacKey;


    /**
     * 心跳监测 定时器
     */
    private static ScheduledExecutorService ssoServerCheckPool;

    private boolean loadbalanceEnable = false;


    public SSOClientConfig() {
        this(DEFAULT_CONFIG);
    }

    public SSOClientConfig(String configPath) {
        try {
            Properties config = new Properties();
            config.load(ClientConfig.class.getClassLoader().getResourceAsStream(configPath));
            this.serverList = Arrays.asList(getProperty(config, CONFIG_PREFIX, "serverList").split(",")).stream().map(SSOServer::new).collect(Collectors.toList());
            this.connectTimeout = getProperty(config, Integer.class, CONFIG_PREFIX, "connectTimeout", 5);
            this.maxIdleConnections = getProperty(config, Integer.class, CONFIG_PREFIX, "maxIdleConnections", 100);
            this.keepAliveDuration = this.connectTimeout = getProperty(config, Integer.class, CONFIG_PREFIX, "keepAliveDuration", 15);
            String loadbalance = getProperty(config, String.class, CONFIG_PREFIX, "loadbalance", null);
            this.heartIdleTime = getProperty(config, Integer.class, CONFIG_PREFIX, "heartIdleTime", 5);
            this.sessionKeyName = getProperty(config, String.class, CONFIG_PREFIX, "sessionKeyName", SSOClientConstant.SESSION_KEY_NAME);
            String filterExcludePath = getProperty(config, String.class, CONFIG_PREFIX, "filterExcludePath", null);
            String filterIncludePath = getProperty(config, String.class, CONFIG_PREFIX, "filterIncludePath", null);
            this.clientLogoutMethod = getProperty(config, String.class, CONFIG_PREFIX, "clientLogoutMethod", null);
            this.clientLogoutPath = getProperty(config, String.class, CONFIG_PREFIX, "clientLogoutPath", null);
            this.clientLoginPath = getProperty(config, String.class, CONFIG_PREFIX, "clientLoginPath", null);
            this.ssoLoginUrl = getProperty(config, String.class, CONFIG_PREFIX, "ssoLoginUrl", null);
            this.redirectUrl = getProperty(config, String.class, CONFIG_PREFIX, "redirectUrl", null);

            this.sessionIdSignHmacKey = getProperty(config, String.class, CONFIG_PREFIX, "sessionIdSignHmacKey", "dushitaoyuan");
            if (StrUtil.isEmpty(filterIncludePath) || StrUtil.isEmpty(redirectUrl)) {
                throw new SSOClientException("filterIncludePath or redirectUrl not config");
            }
            if (StrUtil.isNotEmpty(filterExcludePath)) {
                this.filterExcludePath = Arrays.stream(filterExcludePath.split(",")).filter(StrUtil::isNotEmpty).collect(Collectors.toList());
            }
            this.filterIncludePath = Arrays.stream(filterIncludePath.split(",")).filter(StrUtil::isNotEmpty).collect(Collectors.toList());
            SSOClientConfig myClientConfig = this;
            if (StrUtil.isNotEmpty(loadbalance) && serverList.size() > 1) {
                this.loadbalanceEnable = true;
                this.loadbalance = LoadbalanceEnum.valueOf(loadbalance.toUpperCase()).getLoadbalance();
                ssoServerCheckPool = Executors.newScheduledThreadPool(1);
                //定时心跳检测
                ssoServerCheckPool.scheduleAtFixedRate(() -> {
                    try {
                        ServerUtil.heartBeatCheck(myClientConfig);
                    } catch (Exception e) {
                        log.warn("server check error", e);
                    }
                }, 30, this.heartIdleTime, TimeUnit.SECONDS);
            }
            initOkhttpClient();
        } catch (SSOClientException e) {
            throw e;
        } catch (Exception e) {
            log.error("加载默认配置[" + DEFAULT_CONFIG + "]失败", e);
            throw new SSOClientException("加载默认配置[" + DEFAULT_CONFIG + "]失败", e);
        }
    }

    private void initOkhttpClient() throws Exception {
        SSLParams sslParams = new SSLParams();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        X509TrustManager trustManager = new UnSafeTrustManager();
        sslContext.init(null, new TrustManager[]{trustManager}, null);
        sslParams.sSLSocketFactory = sslContext.getSocketFactory();
        sslParams.trustManager = trustManager;
        Interceptor clientInterceptor = new SSOClientInterceptor(this);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }).sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .connectTimeout(this.getConnectTimeout(), TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(this.getMaxIdleConnections(), this.getKeepAliveDuration(), TimeUnit.SECONDS))
                .retryOnConnectionFailure(false)
                .addInterceptor(clientInterceptor)
                .build();
        List<SSOServerApi> serverApiList = Arrays.asList(SSOServerApi.values());
        Map<SSOServerApi, String> apiMap = new HashMap(serverApiList.size());
        serverApiList.stream().forEach(api -> {
            if (this.loadbalanceEnable) {
                String virtualUrl = SSOClientConstant.SSO_CLIENT_BASE_URL + api.path;
                apiMap.put(api, virtualUrl);
            } else {
                String realUrl = serverList.get(0).getServerUrl() + api.path;
                apiMap.put(api, realUrl);
            }
        });

        this.setApiMap(apiMap);
        this.setOkHttpClient(okHttpClient);
    }


    private <T> T getProperty(Properties config, Class<T> type, String configPrefix, String key, T defaultValue) {
        if (StrUtil.isNotEmpty(configPrefix)) {
            key = configPrefix + key;
        }
        String value = config.getProperty(key);
        if (StrUtil.isEmpty(value)) {
            return defaultValue;
        }
        if (type.equals(String.class)) {
            return (T) value;
        }
        try {
            if (type.equals(Long.class)) {
                Long result = Long.parseLong(value);
                return (T) result;
            }
            if (type.equals(Integer.class)) {
                Integer result = Integer.parseInt(value);
                return (T) result;
            }
            if (type.equals(Boolean.class)) {
                Boolean result = Boolean.valueOf(value);
                return (T) result;
            }

        } catch (Exception e) {
        }
        return defaultValue;

    }

    private String getProperty(Properties config, String configPrefix, String key) {
        if (StrUtil.isNotEmpty(configPrefix)) {
            key = configPrefix + key;
        }
        String value = config.getProperty(key);
        if (StrUtil.isEmpty(value)) {
            throw new SSOClientException("config 异常:" + key);
        }
        return value;
    }

    public static class SSLParams {
        public SSLSocketFactory sSLSocketFactory;
        public X509TrustManager trustManager;
    }

    private static class UnSafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }


}
