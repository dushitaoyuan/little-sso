package com.taoyuanx.littlesso.server.login.handler;

import com.taoyuanx.littlesso.server.vo.LoginForm;
import com.taoyuanx.littlesso.server.vo.SSOLoginUserVo;

/**
 * @author lianglei
 * @date 2019/1/7 12:40
 * @desc 用途描述
 **/
public interface ILoginHandler {
    SSOLoginUserVo login(LoginForm loginForm);
}
