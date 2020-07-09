package com.taoyuanx.littlesso.server.anno;

import com.taoyuanx.littlesso.server.enums.LoginTypeEnum;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 登录处理 标记注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface LoginHander {
    LoginTypeEnum support();
}
