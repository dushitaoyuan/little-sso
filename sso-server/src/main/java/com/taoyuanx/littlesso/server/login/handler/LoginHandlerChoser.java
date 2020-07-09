package com.taoyuanx.littlesso.server.login.handler;

import com.taoyuanx.littlesso.server.anno.LoginHander;
import com.taoyuanx.littlesso.server.enums.LoginTypeEnum;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author lianglei
 * @date 2019/1/7 14:16
 * @desc 登录处理器选择
 **/
@Component
public class LoginHandlerChoser implements ApplicationContextAware {
    private static Map<LoginTypeEnum, ILoginHandler> loginHandlerMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ILoginHandler> loginHandlerBeanMap = applicationContext.getBeansOfType(ILoginHandler.class);
        synchronized (loginHandlerMap) {
            loginHandlerBeanMap.forEach((k, v) -> {
                LoginHander anno = AnnotationUtils.findAnnotation(v.getClass(), LoginHander.class);
                if (Objects.nonNull(anno)) {
                    loginHandlerMap.put(anno.support(), v);
                }
            });
        }
    }

    public ILoginHandler chose(Integer loginType) {
        return loginHandlerMap.get(LoginTypeEnum.type(loginType));
    }
}
