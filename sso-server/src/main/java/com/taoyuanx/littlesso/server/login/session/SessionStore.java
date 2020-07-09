package com.taoyuanx.littlesso.server.login.session;

/**
 * @author lianglei
 * @date 2019/1/7 13:16
 * @desc session存储 增删改查
 **/
public interface SessionStore {
    void set(String key, Object value);

    void remove(String key);

    <T> T get(String key, Class<T> clazz);

    Object get(String key);

    Long getCreateTime();

    Long getEndTime();


}
