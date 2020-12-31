package com.taoyuanx.sso.config;

import com.taoyuanx.sso.core.session.SessionIdGenerate;
import com.taoyuanx.sso.core.session.SessionManager;
import com.taoyuanx.sso.core.session.impl.DefaultSignSessionIdGenerate;
import com.taoyuanx.sso.core.token.sign.impl.HMacSign;
import com.taoyuanx.sso.session.impl.RedisSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author dushitaoyuan
 * @desc sso 配置
 * @date 2020/12/30
 */
@Configuration
public class SSOConfig {
    @Autowired
    SSOProperties ssoProperties;


    @Bean
    public SessionIdGenerate sessionIdGenerate() {
        return new DefaultSignSessionIdGenerate(new HMacSign(ssoProperties.getSessionIdSignHmacKey().getBytes()));
    }

    @Bean
    public SessionManager sessionManager(StringRedisTemplate redisTemplate, SessionIdGenerate sessionIdGenerate) {
        return new RedisSessionManager(redisTemplate, sessionIdGenerate, ssoProperties.getSessionTimeOut() * 60L / 2, ssoProperties.getSessionTimeOut() * 60L / 2);
    }
}
