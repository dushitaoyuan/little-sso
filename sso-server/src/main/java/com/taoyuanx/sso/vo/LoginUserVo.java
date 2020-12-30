package com.taoyuanx.sso.vo;

import com.taoyuanx.sso.entity.UserEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @author dushitaoyuan
 * @desc 登录包装对象
 * @date 2020/12/30
 */
@Data
public class LoginUserVo implements Serializable {
    private UserEntity userEntity;

    private String redirectUrl;

    private String sessionId;
}
