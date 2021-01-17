package com.taoyuanx.sso.config;

import com.taoyuanx.sso.core.consts.SSOConst;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author dushitaoyuan
 * @date 2020/12/30
 */
@Configuration
@ConfigurationProperties(prefix = "sso")
@Data
public class SSOProperties {
    /**
     * session 过期时间 单位分钟
     */
    private Integer sessionTimeOut = SSOConst.DEFAULT_SESSION_TIME_OUT_MINUTE;

    private String sessionIdSignHmacKey = SSOConst.DEFAULT_HMAC_KEY;


    private String sessionIdCookieDomain;
    /**
     * session参数名称 可以在cookie,header,param中传输
     */
    private String sessionKeyName = SSOConst.SSO_SESSION_ID;


    /**
     * 使用采用cookie模式
     */
    private boolean enableCookie;


    /**
     * 会话模式 client or server
     */
    private String sessionMode = SSOConst.SESSION_MODE_SERVER;


    private String clientSessionTokenHmacKey = SSOConst.DEFAULT_HMAC_KEY;

    /**
     * redirectUrl 有效性校验key
     */
    private String redirectUrlSignKey = SSOConst.DEFAULT_HMAC_KEY;
}
