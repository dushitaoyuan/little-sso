package com.taoyuanx.littlesso.server.login.session;

import java.util.concurrent.TimeUnit;

/**
 * @author lianglei
 * @date 2019/1/9 16:01
 * @desc 会话管理接口 会话在活动中,要延迟过期
 **/
public interface SessionManager {
    //创建会话
    SessionStore createSession(String sessionId);

    //创建会话
    SessionStore createSession(String sessionId, Long expire, TimeUnit timeUnit);
    //获取会话
    SessionStore getSessionStore(String sessionId);

    //过期session
    void expireSession(String sessionId);

    //延迟session过期
    void delaySession(String sessionId, Long time, TimeUnit timeUnit);

    //延迟session
    void delaySession(String sessionId);

}
