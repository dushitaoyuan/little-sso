package com.taoyuanx.sso.vo;

import com.taoyuanx.sso.core.dto.SSOUser;
import com.taoyuanx.sso.entity.UserEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @author dushitaoyuan
 * @desc 登录包装对象
 * @date 2020/12/30
 */
@Data
public class LoginUserVo extends SSOUser {
    private UserEntity userEntity;

    private String redirectUrl;



    private String refreshToken;


}
