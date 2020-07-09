package com.taoyuanx.littlesso.server.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author dushitaoyuan
 * @desc 登录表单
 * @date 2019/12/17
 */
@Data
public class LoginForm  implements Serializable {
    //登录类型
    @NotNull(message = "登录类型不可为空")
    private Integer loginType;

    //随机数
    private String random;
    //跳转页面
    private String redirectUrl;


}
