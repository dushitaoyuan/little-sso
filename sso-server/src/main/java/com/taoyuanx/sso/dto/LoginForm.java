package com.taoyuanx.sso.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author dushitaoyuan
 * @desc 登录参数
 * @date 2020/12/30
 */
@Data
public class LoginForm implements Serializable {
    @NotEmpty(message = "用户名不可为空")
    private String username;
    @NotEmpty(message = "密码不可为空")
    private String password;
}
