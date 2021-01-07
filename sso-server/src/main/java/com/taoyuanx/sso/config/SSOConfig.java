package com.taoyuanx.sso.config;

import com.auth0.jwt.algorithms.Algorithm;
import com.taoyuanx.sso.core.consts.SSOConst;
import com.taoyuanx.sso.core.session.SessionIdGenerate;
import com.taoyuanx.sso.core.session.SessionManager;
import com.taoyuanx.sso.core.session.impl.DefaultSignSessionIdGenerate;
import com.taoyuanx.sso.core.session.impl.JwtSessionManager;
import com.taoyuanx.sso.core.session.impl.MyTokenSessionManager;
import com.taoyuanx.sso.core.token.SimpleTokenManager;
import com.taoyuanx.sso.core.token.sign.impl.HMacSign;
import com.taoyuanx.sso.session.impl.RedisSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    @ConditionalOnProperty(prefix = "sso", name = "sessionMode", havingValue = SSOConst.SESSION_MODE_SERVER)
    public SessionManager redisSessionManager(StringRedisTemplate redisTemplate) {
        SessionIdGenerate sessionIdGenerate = new DefaultSignSessionIdGenerate(new HMacSign(ssoProperties.getSessionIdSignHmacKey().getBytes()));
        return new RedisSessionManager(redisTemplate, sessionIdGenerate, ssoProperties.getSessionTimeOut() * 60L);
    }

    @Bean
    @ConditionalOnProperty(prefix = "sso", name = "sessionMode", havingValue = SSOConst.SESSION_MODE_CLIENT)
    public SessionManager myTokenSessionManager() {
        String clientSessionTokenHmacKey = ssoProperties.getClientSessionTokenHmacKey();
        return new MyTokenSessionManager(new SimpleTokenManager(new HMacSign(clientSessionTokenHmacKey.getBytes())), ssoProperties.getSessionTimeOut().longValue());
    }

   /* @Bean
    @ConditionalOnProperty(prefix = "sso", name = "sessionMode", havingValue = SSOConst.SESSION_MODE_CLIENT)
    public SessionManager jwtTokenSessionManager() {
        Algorithm algorithm = Algorithm.HMAC256(ssoProperties.getClientSessionTokenHmacKey());
        return new JwtSessionManager(algorithm, ssoProperties.getSessionTimeOut().longValue());
    }*/
}
