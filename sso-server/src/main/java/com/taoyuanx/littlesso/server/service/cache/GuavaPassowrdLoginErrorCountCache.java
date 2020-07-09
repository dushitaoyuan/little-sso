package com.taoyuanx.littlesso.server.service.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lianglei
 * @date 2020/3/9 17:44
 * @desc 内存计数实现
 **/
public class GuavaPassowrdLoginErrorCountCache implements IPassowrdLoginErrorCountCache {

    private Cache<Long, AtomicInteger> countCache;

    public GuavaPassowrdLoginErrorCountCache(Integer countCacheTimeWindow) {
        countCache = CacheBuilder.newBuilder().maximumSize(1000).initialCapacity(10).expireAfterWrite(countCacheTimeWindow, TimeUnit.MINUTES).build();
    }

    @Override
    public Integer getErrorCount(Long accountId) {
        AtomicInteger ifPresent = countCache.getIfPresent(accountId);
        if (Objects.nonNull(ifPresent)) {
            return ifPresent.get();
        }
        return 0;
    }

    @Override
    public void addErrorCount(Long accountId) {
        AtomicInteger ifPresent = countCache.getIfPresent(accountId);
        if (Objects.nonNull(ifPresent)) {
            ifPresent.incrementAndGet();
        } else {
            countCache.put(accountId, new AtomicInteger(1));
        }
    }
}
