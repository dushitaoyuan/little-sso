package com.taoyuanx.littlesso.server.commons;

/**
 * @author lianglei
 * @date 2019/1/8 14:53
 * @desc 用途描述
 **/
public interface LoginErrorCode {
    /**
     * 口令相关
     */

    //账户不存在
    int ACCOUNT_NOT_EXIST=101;
    //账户已冻结
    int ACCOUNT_FROZEN=102;
    //账户已冻结
    int ACCOUNT_INVALID=103;
    //账户口令不匹配
    int ACCOUNT_PASSWORD_NOT_MATCH=104;



}
