package com.taoyuanx.littlesso.server.login.session.impl.memory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.taoyuanx.littlesso.server.login.session.SessionManager;
import com.taoyuanx.littlesso.server.login.session.SessionStore;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author lianglei
 * @date 2019/1/9 16:02
 * @desc 内存会话管理
 **/
@Slf4j
public class MemorySessionManager implements SessionManager {
    private Map<String, MemorySessionStore> sessionStoreMap = Maps.newConcurrentMap();

    private Timer expireTimer;
    private Long sesssionValidTime;
    //默认延迟时间
    private Long DEFAULT_DELAY_TIME = 5 * 60 * 1000L;
    private static int MAX_SIZE = 100000;
    /**
     * cache满后驱逐大小,最大大小的1/10
     */
    private static int FULL_EVIT_SIZE = MAX_SIZE / 10;


    public MemorySessionManager(Long defaultExpire, TimeUnit timeUnit) {
        expireTimer = new Timer();
        sesssionValidTime = timeUnit.toMillis(defaultExpire);
        Cache<String, MemorySessionStore> build = CacheBuilder.newBuilder().expireAfterWrite(8, TimeUnit.HOURS).build();
        //5分钟扫描一下过期
        expireTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Long now = System.currentTimeMillis();
                sessionStoreMap.forEach((sessionId, sessionStore) -> {
                    //差5分钟到期,也算到期
                    if (sessionStore.getEndTime() + DEFAULT_DELAY_TIME < now) {
                        log.warn("sessionId->[{}],过期删除", sessionId);
                        sessionStoreMap.remove(sessionId);
                    }
                });
            }
        }, 3000L, DEFAULT_DELAY_TIME);
    }

    @Override
    public SessionStore createSession(String sessionId) {
        newSessionStore(sessionId, sesssionValidTime);
        return sessionStoreMap.get(sessionId);
    }

    @Override
    public SessionStore createSession(String sessionId, Long expire, TimeUnit timeUnit) {
        return newSessionStore(sessionId, timeUnit.toMillis(expire));
    }

    @Override
    public SessionStore getSessionStore(String sessionId) {
        return sessionStoreMap.get(sessionId);
    }

    @Override
    public void expireSession(String sessionId) {
        sessionStoreMap.remove(sessionId);
    }

    @Override
    public void delaySession(String sessionId, Long time, TimeUnit timeUnit) {
        if (sessionStoreMap.containsKey(sessionId)) {
            MemorySessionStore sessionStore = sessionStoreMap.get(sessionId);
            Long newEnd = sessionStore.getEndTime() + timeUnit.toMillis(time);
            sessionStore.set(sessionId, newEnd);
        }
    }

    @Override
    public void delaySession(String sessionId) {
        if (sessionStoreMap.containsKey(sessionId)) {
            MemorySessionStore memorySessionStore = sessionStoreMap.get(sessionId);
            Long newEnd = memorySessionStore.getEndTime() + DEFAULT_DELAY_TIME;
            memorySessionStore.setEndTime(newEnd);
        }
    }


    private MemorySessionStore newSessionStore(String sessionId, Long sesssionValidTime) {
        if (sessionStoreMap.size() > MAX_SIZE) {
            fullCacheEvit(FULL_EVIT_SIZE);
        }
        MemorySessionStore memorySessionStore = new MemorySessionStore();
        Long now = System.currentTimeMillis();
        memorySessionStore.setCreateTime(now);
        memorySessionStore.setEndTime(now + sesssionValidTime);
        sessionStoreMap.put(sessionId, memorySessionStore);
        return sessionStoreMap.get(sessionId);
    }

    private synchronized void fullCacheEvit(int evitSize) {
        if (sessionStoreMap.size() > MAX_SIZE) {
            log.warn("session内存 cache已达到最大值->[{}],执行驱逐策略", MAX_SIZE);
            LongAdder evitedSize = new LongAdder();
            sessionStoreMap.entrySet().stream().sorted(Comparator.comparingLong((entry -> {
                return entry.getValue().getEndTime();
            }))).forEachOrdered(entry -> {
                if (evitedSize.intValue() < evitSize) {
                    sessionStoreMap.remove(entry.getKey());
                    evitedSize.increment();
                }
            });
        }
    }

}
