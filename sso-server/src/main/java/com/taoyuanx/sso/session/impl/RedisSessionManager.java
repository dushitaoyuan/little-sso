package com.taoyuanx.sso.session.impl;

import com.taoyuanx.sso.core.dto.SSOUser;
import com.taoyuanx.sso.core.session.SessionIdGenerate;
import com.taoyuanx.sso.core.session.SessionManager;
import com.taoyuanx.sso.core.utils.JSONUtil;
import com.taoyuanx.sso.vo.LoginUserVo;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author dushitaoyuan
 * @desc redis 会话管理
 * @date 2020/12/30
 */
public class RedisSessionManager implements SessionManager {

    private static final String REDIS_SESSION_NAMESPACE = "s:";
    /**
     * redis session 存储结构 为 hash
     * 存储内容 user(USER_KEY), 创建时间(CREATE_TIME_KEY),上次(LAST_ACTIVE_TIME_KEY,可用来剔除长时间不活动的会话)
     */
    private static final String USER_KEY = "u";
    private static final String CREATE_TIME_KEY = "c";
    private static final String LAST_ACTIVE_TIME_KEY = "lc";
    private StringRedisTemplate redisTemplate;
    /**
     * 会话保持阀值,一般为session过期时间的一半
     */
    private Long sessionTimeKeepLimit;
    /**
     * session保持时长   一般为session过期时间的一半
     */
    private Long keepTimeOut;

    private SessionIdGenerate sessionIdGenerate;

    private Long sessionTimeOutSeconds;

    public RedisSessionManager(StringRedisTemplate redisTemplate, SessionIdGenerate sessionIdGenerate,
                               Long sessionTimeKeepLimit,
                               Long keepTimeOut, Long sessionTimeOutSeconds) {
        this.redisTemplate = redisTemplate;
        this.sessionTimeKeepLimit = sessionTimeKeepLimit;
        this.keepTimeOut = keepTimeOut;
        this.sessionIdGenerate = sessionIdGenerate;
        this.sessionTimeOutSeconds = sessionTimeOutSeconds;
    }

    public RedisSessionManager(StringRedisTemplate redisTemplate, SessionIdGenerate sessionIdGenerate,
                               Long sessionTimeOutSeconds) {
        this(redisTemplate, sessionIdGenerate, sessionTimeOutSeconds / 2, sessionTimeOutSeconds / 2, sessionTimeOutSeconds);

    }


    @Override
    public void createSession(SSOUser ssoUser) {
        sessionIdGenerate.generateSessionId(ssoUser);
        String redisKey = redisSessionKey(ssoUser.getSessionId());
        Map<String, String> sessionHashValue = new HashMap<>();
        sessionHashValue.put(CREATE_TIME_KEY, String.valueOf(System.currentTimeMillis()));
        sessionHashValue.put(USER_KEY, JSONUtil.toJsonString(ssoUser));
        redisTemplate.opsForHash().putAll(redisKey, sessionHashValue);
        redisTemplate.expire(redisKey, sessionTimeOutSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean isLogin(String sessionId) {
        String redisKey = redisSessionKey(sessionId);
        Long expire = redisTemplate.getExpire(redisKey);
        /**
         * session过期时间剩余低于阀值时,保持会话时间
         */
        if (Objects.nonNull(expire) && expire > 0) {
            if (expire <= sessionTimeKeepLimit) {
                redisTemplate.expire(redisKey, expire + keepTimeOut, TimeUnit.SECONDS);
            }
            return true;
        }
        redisTemplate.opsForHash().put(redisKey, LAST_ACTIVE_TIME_KEY, String.valueOf(System.currentTimeMillis()));
        return false;
    }

    @Override
    public SSOUser getSSOUser(String sessionId) {
        String sessionKey = redisSessionKey(sessionId);
        String ssoUser = (String) redisTemplate.opsForHash().get(sessionKey, USER_KEY);
        if (Objects.nonNull(ssoUser)) {
            return JSONUtil.parseObject(ssoUser, LoginUserVo.class);
        }
        return null;
    }

    @Override
    public void logout(String sessionId) {
        String sessionKey = redisSessionKey(sessionId);
        redisTemplate.delete(sessionKey);
    }

    private String redisSessionKey(String sessionId) {
        String userId = sessionIdGenerate.parseSessionId(sessionId);
        return REDIS_SESSION_NAMESPACE + userId;
    }
}
