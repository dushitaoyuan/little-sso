package com.taoyuanx.littlesso.server.config;

import com.taoyuanx.littlesso.server.login.session.SessionManager;
import com.taoyuanx.littlesso.server.login.session.impl.memory.MemorySessionManager;
import com.taoyuanx.littlesso.server.service.cache.GuavaPassowrdLoginErrorCountCache;
import com.taoyuanx.littlesso.server.service.cache.IPassowrdLoginErrorCountCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author lianglei
 * @date 2019/1/6 14:40
 * @desc 单点登录配置
 **/
@Configuration
public class SSOConfig {
    @Bean
    public SessionManager menorySessionManager() {
        MemorySessionManager sessionManager = new MemorySessionManager(8L, TimeUnit.HOURS);
        return sessionManager;
    }

    @Bean
    @Autowired
    public IPassowrdLoginErrorCountCache guavaPassowrdLoginErrorCountCache(SsoServerProperties ssoServerProperties) {
        return new GuavaPassowrdLoginErrorCountCache(ssoServerProperties.getPassordErrorLockTimeWindowMin());
    }


   /* @Bean
    @Autowired
    public SessionManager redisSessionManager(RedisTemplate redisTemplate) {
        RedisSessionManager sessionManager = new RedisSessionManager(redisTemplate, 8L, TimeUnit.HOURS);
        return sessionManager;
    }

    @Bean
    @Autowired
    public IPassowrdLoginErrorCountCache guavaPassowrdLoginErrorCountCache(SsoServerProperties ssoServerProperties, RedisTemplate redisTemplate) {
        return new RedisPassowrdLoginErrorCountCache(ssoServerProperties.getPassordErrorLockTimeWindowMin(), redisTemplate);
    }*/

}
