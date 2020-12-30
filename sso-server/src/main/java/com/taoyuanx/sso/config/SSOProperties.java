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
    private Integer sessionTimeOut;

    private String sessionIdSignHmacKey;
    /**
     * sessionId cookie名称及域名
     */
    private String sessionIdCookieName=SSOConst.SSO_SESSION_ID;

    private String sessionIdCookieDomain;
    /**
     * session参数名称 可以在cookie,header,param中传输
     */
    private String sessionKeyName = SSOConst.SSO_SESSION_ID;

    /**
     * 登录地址
     */
    private String loginUrl="/login";

}
