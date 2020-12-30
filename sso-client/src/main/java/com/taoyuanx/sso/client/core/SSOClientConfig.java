package com.taoyuanx.sso.client.core;


import com.sun.deploy.config.ClientConfig;
import com.taoyuanx.sso.client.ex.SSOClientException;
import com.taoyuanx.sso.client.impl.SSOClient;
import com.taoyuanx.sso.client.impl.loadbalance.ILoadbalance;
import com.taoyuanx.sso.client.impl.loadbalance.LoadbalanceEnum;
import com.taoyuanx.sso.client.utils.ServerUtil;
import com.taoyuanx.sso.client.utils.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Getter
@Setter
@Slf4j
public class SSOClientConfig {
    public static final String DEFAULT_CONFIG = "sso.properties";
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
     * 心跳监测时间间隔 默认30秒
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
    private String sessionKeyName = SSOClientConstant.SESSION_KEY_NAME;

    /**
     * sso 过滤器排除地址
     */
    private List<String> filterExcludePath = Collections.EMPTY_LIST;
    /**
     * 拦截地址
     */
    private List<String> filterIncludePath;
    /**
     * client退出地址
     */
    private String clientLogoutUrl;
    /**
     * client退出http方法
     */
    private String clientLogoutMethod;
    /**
     * client 登录地址
     */
    private String clientLoginUrl;
    /**
     * session 校验
     */
    private String sessionIdSignHmacKey;


    /**
     * 心跳监测 定时器
     */
    private static ScheduledExecutorService ssoServerCheckPool = Executors.newScheduledThreadPool(1);


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
            this.loadbalance = LoadbalanceEnum.valueOf(getProperty(config, String.class, CONFIG_PREFIX, "loadbalance", LoadbalanceEnum.Round.name())).getLoadbalance();
            this.heartIdleTime = getProperty(config, Integer.class, CONFIG_PREFIX, "heartIdleTime", 30);
            this.sessionKeyName = getProperty(config, String.class, CONFIG_PREFIX, "sessionKeyName", SSOClientConstant.SESSION_KEY_NAME);
            String filterExcludePath = getProperty(config, String.class, CONFIG_PREFIX, "filterExcludePath", null);
            String filterIncludePath = getProperty(config, String.class, CONFIG_PREFIX, "filterIncludePath", null);
            String clientLogoutMethod = getProperty(config, String.class, CONFIG_PREFIX, "clientLogoutMethod", null);
            String clientLoginUrl = getProperty(config, String.class, CONFIG_PREFIX, "clientLoginUrl", null);
            this.sessionIdSignHmacKey = getProperty(config, String.class, CONFIG_PREFIX, "sessionIdSignHmacKey", "dushitaoyuan");
            if (StrUtil.isEmpty(filterIncludePath)
                    || StrUtil.isEmpty(clientLoginUrl)) {
                throw new SSOClientException("filterIncludePath,or clientLoginUrl  not config");
            }
            if (StrUtil.isNotEmpty(filterExcludePath)) {
                this.filterExcludePath = Arrays.stream(filterExcludePath.split(",")).filter(StrUtil::isNotEmpty).collect(Collectors.toList());
            }
            this.filterIncludePath = Arrays.stream(filterIncludePath.split(",")).filter(StrUtil::isNotEmpty).collect(Collectors.toList());
            this.clientLoginUrl = clientLoginUrl;
            this.clientLogoutMethod = clientLogoutMethod;

            SSOClientConfig myClientConfig = this;
            //定时心跳检测
            ssoServerCheckPool.scheduleAtFixedRate(() -> {
                try {
                    ServerUtil.heartBeatCheck(myClientConfig);
                } catch (Exception e) {
                    log.warn("server check error", e);
                }
            }, 30, this.heartIdleTime, TimeUnit.SECONDS);

        } catch (SSOClientException e) {
            throw e;
        } catch (Exception e) {
            log.error("加载默认配置[" + DEFAULT_CONFIG + "]失败", e);
            throw new SSOClientException("加载默认配置[" + DEFAULT_CONFIG + "]失败", e);
        }
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

}
