package com.taoyuanx.littlesso.server.service.cache;

import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author lianglei
 * @date 2020/3/9 17:44
 * @desc redis计数实现
 **/
public class RedisPassowrdLoginErrorCountCache implements IPassowrdLoginErrorCountCache {


    private Long countCacheTimeWindow;
    private RedisTemplate redisTemplate;

    public RedisPassowrdLoginErrorCountCache(Integer countCacheTimeWindow, RedisTemplate redisTemplate) {
        this.countCacheTimeWindow = countCacheTimeWindow.longValue();
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Integer getErrorCount(Long accountId) {
        if (redisTemplate.hasKey(accountId)) {
            return (Integer) redisTemplate.boundValueOps(accountId).get();
        }
        return 0;
    }

    @Override
    public void addErrorCount(Long accountId) {
        if (redisTemplate.hasKey(accountId)) {
            redisTemplate.boundValueOps(accountId).increment(1L);
        } else {
            BoundValueOperations ops = redisTemplate.boundValueOps(accountId);
            ops.expire(countCacheTimeWindow, TimeUnit.MINUTES);
            ops.set(1L);
        }
    }
}
