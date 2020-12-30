package com.taoyuanx.sso.service;

import com.taoyuanx.sso.entity.UserEntity;

/**
 * @author dushitaoyuan
 * @date 2020/12/30
 */
public interface UserService {

    UserEntity findByUsername(String username);
}
