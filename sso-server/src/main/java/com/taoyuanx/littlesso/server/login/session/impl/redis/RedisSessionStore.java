package com.taoyuanx.littlesso.server.login.session.impl.redis;

import com.taoyuanx.littlesso.server.login.session.SessionStore;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

/**
 * @author lianglei
 * @date 2019/1/9 15:57
 * @desc 内存会话 数据保存
 **/
public class RedisSessionStore implements SessionStore {
    private RedisTemplate redisTemplate;


    private String sessionId;

    private static final String CRETETIME_KEY = "createtime", ENDTIME_KEY = "endtime", SESSION_ID_KEY = "sessionId";


    public RedisSessionStore(RedisTemplate redisTemplate, String sessionId) {
        this.sessionId = sessionId;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, Object value) {
        redisTemplate.boundHashOps(sessionId).put(key, value);
    }

    @Override
    public void remove(String key) {
        redisTemplate.boundHashOps(sessionId).delete(key);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return (T) redisTemplate.boundHashOps(sessionId).get(key);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.boundHashOps(sessionId).get(key);
    }

    @Override
    public Long getCreateTime() {
        return (Long) redisTemplate.boundHashOps(sessionId).get(CRETETIME_KEY);
    }

    @Override
    public Long getEndTime() {
        return (Long) redisTemplate.boundHashOps(sessionId).get(ENDTIME_KEY);

    }

    public void setCreateTime(Long createTime) {
        redisTemplate.boundHashOps(sessionId).put(CRETETIME_KEY, createTime);
    }

    public void setEndTime(Long endTime) {
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(sessionId);
        hashOperations.put(ENDTIME_KEY, endTime);
        hashOperations.expireAt(new Date(endTime));

    }


}
