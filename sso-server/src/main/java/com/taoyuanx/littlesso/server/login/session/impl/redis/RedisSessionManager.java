package com.taoyuanx.littlesso.server.login.session.impl.redis;

import com.taoyuanx.littlesso.server.login.session.SessionManager;
import com.taoyuanx.littlesso.server.login.session.SessionStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author lianglei
 * @date 2019/1/9 16:02
 * @desc 内存会话管理
 **/
@Slf4j
public class RedisSessionManager implements SessionManager {
    private Long sesssionValidTime;
    //默认延迟时间 5分钟
    private Long DEFAULT_DELAY_TIME = 5L;

    private RedisTemplate redisTemplate;

    public RedisSessionManager(RedisTemplate redisTemplate, Long defaultExpire, TimeUnit timeUnit) {
        this.redisTemplate = redisTemplate;
        sesssionValidTime = timeUnit.toMillis(defaultExpire);

    }

    @Override
    public SessionStore createSession(String sessionId) {
        return createSession(sessionId, sesssionValidTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public SessionStore createSession(String sessionId, Long expire, TimeUnit timeUnit) {
        return newSessionStore(sessionId, expire, timeUnit);

    }

    @Override
    public SessionStore getSessionStore(String sessionId) {
        return new RedisSessionStore(redisTemplate, sessionId);
    }

    @Override
    public void expireSession(String sessionId) {
        redisTemplate.delete(sessionId);
    }

    @Override
    public void delaySession(String sessionId, Long time, TimeUnit timeUnit) {
        if (redisTemplate.hasKey(sessionId)) {
            RedisSessionStore sessionStore = (RedisSessionStore) getSessionStore(sessionId);
            Long newEnd = sessionStore.getEndTime() + timeUnit.toMillis(time);
            sessionStore.setEndTime(newEnd);
        }
    }

    @Override
    public void delaySession(String sessionId) {
        delaySession(sessionId, DEFAULT_DELAY_TIME, TimeUnit.MINUTES);
    }


    private RedisSessionStore newSessionStore(String sessionId, Long expire, TimeUnit timeUnit) {
        RedisSessionStore redisSessionStore = new RedisSessionStore(redisTemplate, sessionId);
        long now = System.currentTimeMillis();
        redisSessionStore.setCreateTime(now);
        redisSessionStore.setEndTime(now + timeUnit.toMillis(expire));
        return redisSessionStore;
    }


}
