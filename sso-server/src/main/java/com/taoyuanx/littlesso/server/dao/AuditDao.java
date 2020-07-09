package com.taoyuanx.littlesso.server.dao;

import com.taoyuanx.littlesso.server.entity.LoginAuditEntity;

/**
 * @author lianglei
 * @date 2019/1/9 21:22
 * @desc 审计操作
 **/
public interface AuditDao {
    /**
     * 登录审计
     */
    void insertLoginAudit(LoginAuditEntity loginAuditEntity);
}
