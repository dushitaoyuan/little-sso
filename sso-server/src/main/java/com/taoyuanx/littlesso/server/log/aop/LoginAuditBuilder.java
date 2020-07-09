package com.taoyuanx.littlesso.server.log.aop;

import com.taoyuanx.littlesso.server.entity.LoginAuditEntity;

/**
 * @author lianglei
 * @date 2020/4/7 15:40
 * @desc 登录审计日志
 **/
public class LoginAuditBuilder {
    private static ThreadLocal<LoginAuditEntity> loginAuditContext = ThreadLocal.withInitial(() -> {
        return new LoginAuditEntity();
    });

    public static LoginAuditEntity getLoginAudit() {
        return loginAuditContext.get();
    }

    public static void setLoginAudit(LoginAuditEntity loginAuditEntity) {
        loginAuditContext.set(loginAuditEntity);
    }

    public static void removeLogin() {
        loginAuditContext.remove();
    }
}
