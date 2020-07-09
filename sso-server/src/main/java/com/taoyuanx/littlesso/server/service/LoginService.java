package com.taoyuanx.littlesso.server.service;

import com.taoyuanx.littlesso.server.vo.SSOLoginUserVo;

/**
 * @author lianglei
 * @date 2019/1/8 13:39
 * @desc 登录服务接口
 **/
public interface LoginService {
    //口令登录
    SSOLoginUserVo login(String username, String password);

}
