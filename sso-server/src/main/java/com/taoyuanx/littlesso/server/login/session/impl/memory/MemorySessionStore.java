package com.taoyuanx.littlesso.server.login.session.impl.memory;

import com.google.common.collect.Maps;
import com.taoyuanx.littlesso.server.login.session.SessionStore;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;

/**
 * @author lianglei
 * @date 2019/1/9 15:57
 * @desc 内存会话 数据保存
 **/
public class MemorySessionStore implements SessionStore {
    private Map<String, Object> sessionDataMap = Maps.newHashMap();
    @Setter
    private Long createTime;
    @Setter
    private Long endTime;

    @Override
    public void set(String key, Object value) {
        sessionDataMap.put(key, value);
    }

    @Override
    public void remove(String key) {
        sessionDataMap.remove(key);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Object value = sessionDataMap.get(key);
        if (Objects.nonNull(value)) {
            return (T) value;
        }
        return null;
    }

    @Override
    public Object get(String key) {
        return sessionDataMap.get(key);
    }

    @Override
    public Long getCreateTime() {
        return createTime;
    }

    @Override
    public Long getEndTime() {
        return endTime;
    }


}
