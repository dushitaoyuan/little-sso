package com.taoyuanx.littlesso.server.vo;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author lianglei
 * @date 2019/1/7 12:15
 * @desc 口令登录表单
 **/
@Data
public class PasswordLoginForm  extends LoginForm{
    @NotEmpty(message = "账户名不可为空")
    private String username;
    @NotEmpty(message = "密码不可为空")
    private String password;

    //验证码
    @NotEmpty(message = "验证码不可为空")
    private String vafyCode;
}
